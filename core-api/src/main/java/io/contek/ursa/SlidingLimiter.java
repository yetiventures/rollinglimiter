package io.contek.ursa;

import com.google.common.util.concurrent.ListeningScheduledExecutorService;

import javax.annotation.concurrent.ThreadSafe;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.PriorityQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.util.concurrent.MoreExecutors.listeningDecorator;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

@ThreadSafe
public final class SlidingLimiter {

  private final RateLimit limit;
  private final Semaphore semaphore;
  private final ListeningScheduledExecutorService scheduler;
  private final Clock clock;

  private final AtomicReference<ScheduledFuture<?>> holder = new AtomicReference<>(null);
  private final PriorityQueue<RecoveringPermits> queue = new PriorityQueue<>();

  public SlidingLimiter(RateLimit limit) {
    this.limit = limit;
    semaphore = new Semaphore(limit.getPermits(), true);
    scheduler = createScheduler();
    clock = Clock.systemUTC();
  }

  public RateLimit getLimit() {
    return limit;
  }

  public IPermitSession acquire(int permits) throws PermitCapExceedException, InterruptedException {
    if (permits > limit.getPermits()) {
      throw new PermitCapExceedException(permits, limit.getPermits());
    }

    if (permits < 0) {
      throw new NegativePermitException(permits);
    }

    if (permits == 0) {
      return ZeroPermitSession.getInstance();
    }

    semaphore.acquire(permits);
    return new SimplePermitSession(canceled -> onSessionClose(permits, canceled));
  }

  private void onRefill() {
    Instant now = clock.instant();
    int permits = drainRecoveredPermitsUntil(now);
    semaphore.release(permits);
    scheduleNextRefill();
  }

  private void onSessionClose(int permits, boolean immediate) {
    if (immediate) {
      semaphore.release(permits);
      return;
    }

    Instant now = clock.instant();
    Instant availability = now.plus(limit.getPeriod());
    synchronized (queue) {
      queue.offer(new RecoveringPermits(permits, availability));
    }
    scheduleNextRefill();
  }

  private int drainRecoveredPermitsUntil(Instant instant) {
    int permits = 0;
    synchronized (queue) {
      while (!queue.isEmpty()) {
        RecoveringPermits next = queue.peek();
        if (!next.getAvailability().isAfter(instant)) {
          permits += queue.poll().getPermits();
        } else {
          break;
        }
      }
    }
    return permits;
  }

  private void scheduleNextRefill() {
    synchronized (queue) {
      RecoveringPermits first = queue.peek();
      if (first == null) {
        return;
      }

      Instant availability = first.getAvailability();
      synchronized (holder) {
        Instant now = clock.instant();
        ScheduledFuture<?> future = holder.get();
        if (future != null) {
          if (!future.isDone()) {
            Duration delay = Duration.ofNanos(future.getDelay(NANOSECONDS));
            Instant previous = now.plus(delay);
            if (!previous.isAfter(availability)) {
              return;
            }
            future.cancel(true);
          }
        }

        Duration delay =
            availability.isAfter(now) ? Duration.between(now, availability) : Duration.ZERO;
        future = scheduler.schedule(this::onRefill, delay);
        holder.set(future);
      }
    }
  }

  private static ListeningScheduledExecutorService createScheduler() {
    ThreadFactory factory =
        target -> {
          Thread thread = new Thread(target);
          thread.setDaemon(true);
          return thread;
        };
    return listeningDecorator(newSingleThreadScheduledExecutor(factory));
  }
}

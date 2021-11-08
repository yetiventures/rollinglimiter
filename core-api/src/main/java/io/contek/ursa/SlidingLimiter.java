package io.contek.ursa;

import com.google.common.util.concurrent.ListeningScheduledExecutorService;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.time.Duration;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;

import static com.google.common.util.concurrent.MoreExecutors.listeningDecorator;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

@ThreadSafe
public final class SlidingLimiter {

  private final RateLimit limit;
  private final Semaphore semaphore;
  private final ListeningScheduledExecutorService scheduler;

  public SlidingLimiter(RateLimit limit) {
    this.limit = limit;
    semaphore = new Semaphore(limit.getPermits(), true);
    scheduler = createScheduler();
  }

  public RateLimit getLimit() {
    return limit;
  }

  public IPermitSession acquire()
      throws PermitCapExceedException, NegativePermitException, AcquireTimeoutException,
          InterruptedException {
    return acquire(null);
  }

  public IPermitSession acquire(@Nullable Duration timeout)
      throws PermitCapExceedException, NegativePermitException, AcquireTimeoutException,
          InterruptedException {
    return acquire(1, timeout);
  }

  public IPermitSession acquire(int permits)
      throws PermitCapExceedException, NegativePermitException, AcquireTimeoutException,
          InterruptedException {
    return acquire(permits, null);
  }

  public IPermitSession acquire(int permits, @Nullable Duration timeout)
      throws PermitCapExceedException, NegativePermitException, AcquireTimeoutException,
          InterruptedException {
    IPermitSession session = tryAcquire(permits, timeout);
    if (session == null) {
      throw new AcquireTimeoutException(requireNonNull(timeout));
    }

    return session;
  }

  @Nullable
  public IPermitSession tryAcquire(@Nullable Duration timeout)
      throws PermitCapExceedException, NegativePermitException, InterruptedException {
    return tryAcquire(1, timeout);
  }

  @Nullable
  public IPermitSession tryAcquire(int permits, @Nullable Duration timeout)
      throws PermitCapExceedException, NegativePermitException, InterruptedException {
    if (permits > limit.getPermits()) {
      throw new PermitCapExceedException(permits, limit.getPermits());
    }

    if (permits < 0) {
      throw new NegativePermitException(permits);
    }

    if (permits == 0) {
      return ZeroPermitSession.getInstance();
    }

    if (timeout == null) {
      semaphore.acquire(permits);
    } else {
      boolean acquired = semaphore.tryAcquire(permits, timeout.toNanos(), NANOSECONDS);
      if (!acquired) {
        return null;
      }
    }

    return new SimplePermitSession(canceled -> onSessionClose(permits, canceled));
  }

  private void onSessionClose(int permits, boolean immediate) {
    if (immediate) {
      semaphore.release(permits);
      return;
    }

    scheduler.schedule(() -> semaphore.release(permits), limit.getPeriod());
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

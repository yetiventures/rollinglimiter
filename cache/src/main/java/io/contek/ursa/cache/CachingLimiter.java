package io.contek.ursa.cache;

import io.contek.ursa.*;
import net.jcip.annotations.ThreadSafe;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.synchronizedMap;

@ThreadSafe
final class CachingLimiter {

  private final RateLimit limit;
  private final Map<String, SlidingLimiter> map = synchronizedMap(new HashMap<>());

  CachingLimiter(RateLimit limit) {
    this.limit = limit;
  }

  IPermitSession acquire(String key, int permits, @Nullable Duration timeout)
      throws PermitCapExceedException, AcquireTimeoutException, InterruptedException {
    SlidingLimiter limiter = map.computeIfAbsent(key, k -> create());
    return limiter.acquire(permits, timeout);
  }

  private SlidingLimiter create() {
    return new SlidingLimiter(limit);
  }
}

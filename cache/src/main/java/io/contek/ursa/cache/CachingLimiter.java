package io.contek.ursa.cache;

import io.contek.ursa.IPermitSession;
import io.contek.ursa.RateLimit;
import io.contek.ursa.SlidingLimiter;
import net.jcip.annotations.ThreadSafe;

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

  IPermitSession acquire(String key, int permits) throws InterruptedException {
    SlidingLimiter limiter = map.computeIfAbsent(key, k -> create());
    return limiter.acquire(permits);
  }

  private SlidingLimiter create() {
    return new SlidingLimiter(limit);
  }
}

package io.contek.ursa.cache;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import io.contek.ursa.CombinePermitSession;
import io.contek.ursa.IPermitSession;
import io.contek.ursa.RateLimit;
import net.jcip.annotations.ThreadSafe;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.*;

@ThreadSafe
public final class LimiterManager {

  private final ImmutableMap<String, CachingLimiter> map;

  private LimiterManager(ImmutableMap<String, CachingLimiter> map) {
    this.map = map;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public IPermitSession acquire(PermitRequest r1, PermitRequest r2, PermitRequest... rs)
      throws InterruptedException {
    return acquire(Iterables.concat(Arrays.asList(r1, r2), Arrays.asList(rs)));
  }

  public IPermitSession acquire(Iterable<PermitRequest> requests) throws InterruptedException {
    List<IPermitSession> sessions = new ArrayList<>();
    for (PermitRequest request : requests) {
      sessions.add(acquire(request));
    }
    return CombinePermitSession.wrap(sessions);
  }

  public IPermitSession acquire(PermitRequest request) throws InterruptedException {
    CachingLimiter limiter = map.get(request.getRuleName());
    if (limiter == null) {
      throw new NoSuchRateLimitException(request.getRuleName());
    }

    return limiter.acquire(request.getKey(), request.getPermits());
  }

  @NotThreadSafe
  public static final class Builder {

    private final Map<String, RateLimit> rateLimits = new HashMap<>();

    public Builder addRateLimit(String name, RateLimit rateLimit) {
      this.rateLimits.put(name, rateLimit);
      return this;
    }

    public Builder addAllRateLimits(Map<String, RateLimit> rateLimits) {
      this.rateLimits.putAll(rateLimits);
      return this;
    }

    public LimiterManager build() {
      ImmutableMap.Builder<String, CachingLimiter> builder = ImmutableMap.builder();
      rateLimits.forEach((name, limit) -> builder.put(name, new CachingLimiter(limit)));
      return new LimiterManager(builder.build());
    }

    private Builder() {}
  }
}

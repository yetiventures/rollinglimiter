package com.yvhk.rollinglimiter.cache;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.yvhk.rollinglimiter.*;

import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

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
      throws PermitCapExceedException, AcquireTimeoutException, InterruptedException {
    return acquire(Iterables.concat(asList(r1, r2), asList(rs)));
  }

  public IPermitSession acquire(Iterable<PermitRequest> requests)
      throws PermitCapExceedException, AcquireTimeoutException, InterruptedException {
    List<IPermitSession> sessions = new ArrayList<>();
    try {
      for (PermitRequest request : requests) {
        sessions.add(acquire(request));
      }
    } catch (PermitCapExceedException | AcquireTimeoutException | InterruptedException e) {
      for (IPermitSession acquired : sessions) {
        acquired.cancel();
        acquired.close();
      }
      throw e;
    }

    return CombinePermitSession.wrap(sessions);
  }

  public IPermitSession acquire(PermitRequest request)
      throws PermitCapExceedException, AcquireTimeoutException, InterruptedException {
    CachingLimiter limiter = map.get(request.getRuleName());
    if (limiter == null) {
      throw new NoSuchRateLimitException(request.getRuleName());
    }

    return limiter.acquire(request.getKey(), request.getPermits(), request.getTimeout());
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

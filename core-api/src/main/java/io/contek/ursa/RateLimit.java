package io.contek.ursa;

import javax.annotation.concurrent.Immutable;
import java.time.Duration;

@Immutable
public class RateLimit {

  private final int permits;
  private final Duration period;

  public RateLimit(int permits, Duration period) {
    this.permits = permits;
    this.period = period;
  }

  public int getPermits() {
    return permits;
  }

  public Duration getPeriod() {
    return period;
  }
}

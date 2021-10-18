package io.contek.ursa;

import javax.annotation.concurrent.Immutable;
import java.time.Instant;

@Immutable
final class Recovery implements Comparable<Recovery> {

  private final int permits;
  private final Instant availability;

  Recovery(int permits, Instant availability) {
    this.permits = permits;
    this.availability = availability;
  }

  @Override
  public int compareTo(Recovery that) {
    return this.availability.compareTo(that.availability);
  }

  int getPermits() {
    return permits;
  }

  Instant getAvailability() {
    return availability;
  }
}

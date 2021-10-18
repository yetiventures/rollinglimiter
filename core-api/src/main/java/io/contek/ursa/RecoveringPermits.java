package io.contek.ursa;

import javax.annotation.concurrent.Immutable;
import java.time.Instant;

@Immutable
final class RecoveringPermits implements Comparable<RecoveringPermits> {

  private final int permits;
  private final Instant availability;

  RecoveringPermits(int permits, Instant availability) {
    this.permits = permits;
    this.availability = availability;
  }

  @Override
  public int compareTo(RecoveringPermits that) {
    return this.availability.compareTo(that.availability);
  }

  int getPermits() {
    return permits;
  }

  Instant getAvailability() {
    return availability;
  }
}

package io.contek.ursa;

import javax.annotation.concurrent.ThreadSafe;
import java.io.Closeable;
import java.util.function.IntConsumer;

@ThreadSafe
public final class PermittedSession implements Closeable {

  private static final PermittedSession ZERO = new PermittedSession(0, n -> {});

  private final int permits;
  private final IntConsumer onClose;

  PermittedSession(int permits, IntConsumer onClose) {
    this.permits = permits;
    this.onClose = onClose;
  }

  static PermittedSession zero() {
    return ZERO;
  }

  public int getPermits() {
    return permits;
  }

  @Override
  public void close() {
    onClose.accept(permits);
  }
}

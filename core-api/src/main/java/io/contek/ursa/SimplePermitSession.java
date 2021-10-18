package io.contek.ursa;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@ThreadSafe
final class SimplePermitSession implements IPermitSession {

  private final Consumer<Boolean> onClose;
  private final AtomicBoolean canceled = new AtomicBoolean(false);
  private final AtomicBoolean closed = new AtomicBoolean(false);

  SimplePermitSession(Consumer<Boolean> onClose) {
    this.onClose = onClose;
  }

  @Override
  public void cancel() {
    canceled.set(true);
  }

  @Override
  public void close() throws AlreadyClosedException {
    if (closed.getAndSet(true)) {
      throw new AlreadyClosedException();
    }

    onClose.accept(canceled.get());
  }
}

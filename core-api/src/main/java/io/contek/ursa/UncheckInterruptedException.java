package io.contek.ursa;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public final class UncheckInterruptedException extends UrsaException {

  UncheckInterruptedException(InterruptedException e) {
    super(e);
  }
}

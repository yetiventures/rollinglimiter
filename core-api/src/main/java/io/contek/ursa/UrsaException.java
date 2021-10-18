package io.contek.ursa;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class UrsaException extends RuntimeException {

  protected UrsaException(String message) {
    super(message);
  }

  protected UrsaException(Throwable cause) {
    super(cause);
  }
}

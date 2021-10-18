package io.contek.ursa;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class UrsaException extends RuntimeException {

  UrsaException(String message) {
    super(message);
  }

  UrsaException(String message, Throwable cause) {
    super(message, cause);
  }
}

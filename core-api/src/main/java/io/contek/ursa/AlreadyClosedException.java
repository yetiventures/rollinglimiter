package io.contek.ursa;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public final class AlreadyClosedException extends UrsaException {

  AlreadyClosedException() {
    super("Session already closed");
  }
}

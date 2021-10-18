package io.contek.ursa;

import javax.annotation.concurrent.NotThreadSafe;

import static java.lang.String.format;

@NotThreadSafe
public final class NegativePermitException extends UrsaException {

  NegativePermitException(int requestPermits) {
    super(format("Requested permits is negative: %d", requestPermits));
  }
}

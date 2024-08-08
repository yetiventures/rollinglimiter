package com.yvhk.rollinglimiter;

import javax.annotation.concurrent.NotThreadSafe;

import static java.lang.String.format;

@NotThreadSafe
public final class PermitCapExceedException extends UrsaException {

  PermitCapExceedException(int requestPermits, int permitCap) {
    super(format("Requested permits exceeds cap: %d > %d", requestPermits, permitCap));
  }
}

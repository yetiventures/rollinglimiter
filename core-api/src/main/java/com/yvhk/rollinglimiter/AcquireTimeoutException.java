package com.yvhk.rollinglimiter;

import javax.annotation.concurrent.NotThreadSafe;
import java.time.Duration;

import static java.lang.String.format;

@NotThreadSafe
public final class AcquireTimeoutException extends UrsaException {

  AcquireTimeoutException(Duration duration) {
    super(format("Acquire permit timeout %s exceeded", duration));
  }
}

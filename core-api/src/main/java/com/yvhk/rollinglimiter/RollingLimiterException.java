package com.yvhk.rollinglimiter;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public abstract class RollingLimiterException extends RuntimeException {

  protected RollingLimiterException(String message) {
    super(message);
  }

  protected RollingLimiterException(Throwable cause) {
    super(cause);
  }
}

package com.yvhk.rollinglimiter.cache;

import com.yvhk.rollinglimiter.RollingLimiterException;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public final class InvalidPermitRequestException extends RollingLimiterException {

  InvalidPermitRequestException(String field) {
    super(String.format("Missing rate limit %s", field));
  }
}

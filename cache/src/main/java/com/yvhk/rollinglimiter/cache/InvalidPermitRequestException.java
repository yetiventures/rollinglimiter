package com.yvhk.rollinglimiter.cache;

import com.yvhk.rollinglimiter.UrsaException;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public final class InvalidPermitRequestException extends UrsaException {

  InvalidPermitRequestException(String field) {
    super(String.format("Missing rate limit %s", field));
  }
}

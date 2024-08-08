package com.yvhk.rollinglimiter.cache;

import com.yvhk.rollinglimiter.UrsaException;

import javax.annotation.concurrent.NotThreadSafe;

import static java.lang.String.format;

@NotThreadSafe
public final class NoSuchRateLimitException extends UrsaException {

  NoSuchRateLimitException(String name) {
    super(format("Could not find rate limit %s", name));
  }
}

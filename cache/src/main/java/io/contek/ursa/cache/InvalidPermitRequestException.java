package io.contek.ursa.cache;

import io.contek.ursa.UrsaException;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public final class InvalidPermitRequestException extends UrsaException {

  InvalidPermitRequestException(String field) {
    super(String.format("Missing rate limit %s", field));
  }
}

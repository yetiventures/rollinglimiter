package com.yvhk.rollinglimiter;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface IPermitSession extends AutoCloseable {

  void cancel();

  @Override
  void close();
}

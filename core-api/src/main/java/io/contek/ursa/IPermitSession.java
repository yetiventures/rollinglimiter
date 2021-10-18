package io.contek.ursa;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface IPermitSession extends AutoCloseable {

  void cancel();

  @Override
  void close();
}

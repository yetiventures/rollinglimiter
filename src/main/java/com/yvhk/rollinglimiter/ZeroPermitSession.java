package com.yvhk.rollinglimiter;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class ZeroPermitSession implements IPermitSession {

  public static ZeroPermitSession getInstance() {
    return Holder.INSTANCE;
  }

  @Override
  public void cancel() {}

  @Override
  public void close() {}

  private ZeroPermitSession() {}

  @Immutable
  private static final class Holder {

    private static final ZeroPermitSession INSTANCE = new ZeroPermitSession();
  }
}

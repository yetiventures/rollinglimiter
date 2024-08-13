package com.yvhk.rollinglimiter;

import com.google.common.collect.ImmutableList;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class CombinePermitSession implements IPermitSession {

  private final ImmutableList<IPermitSession> sessions;

  private CombinePermitSession(ImmutableList<IPermitSession> sessions) {
    this.sessions = sessions;
  }

  public static CombinePermitSession wrap(IPermitSession... sessions) {
    return wrap(ImmutableList.copyOf(sessions));
  }

  public static CombinePermitSession wrap(Iterable<IPermitSession> sessions) {
    return new CombinePermitSession(ImmutableList.copyOf(sessions));
  }

  @Override
  public void cancel() {
    sessions.forEach(IPermitSession::cancel);
  }

  @Override
  public void close() {
    sessions.forEach(IPermitSession::close);
  }
}

package io.contek.ursa.cache;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.time.Duration;

@Immutable
public final class PermitRequest {

  private final String ruleName;
  private final String key;
  private final int permits;
  private final Duration timeout;

  private PermitRequest(String ruleName, String key, int permits, Duration timeout) {
    this.ruleName = ruleName;
    this.key = key;
    this.permits = permits;
    this.timeout = timeout;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  String getRuleName() {
    return ruleName;
  }

  String getKey() {
    return key;
  }

  int getPermits() {
    return permits;
  }

  Duration getTimeout() {
    return timeout;
  }

  @NotThreadSafe
  public static final class Builder {

    private String name;
    private String key;
    private Integer permits;
    private Duration timeout;

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder setKey(String key) {
      this.key = key;
      return this;
    }

    public Builder setPermits(Integer permits) {
      this.permits = permits;
      return this;
    }

    public void setTimeout(@Nullable Duration timeout) {
      this.timeout = timeout;
    }

    public PermitRequest build() throws InvalidPermitRequestException {
      if (name == null) {
        throw new InvalidPermitRequestException("name");
      }

      if (key == null) {
        throw new InvalidPermitRequestException("key");
      }

      if (permits == null) {
        throw new InvalidPermitRequestException("permits");
      }

      return new PermitRequest(name, key, permits, timeout);
    }

    private Builder() {}
  }
}

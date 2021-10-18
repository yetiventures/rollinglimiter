package io.contek.ursa.cache;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

@Immutable
public final class PermitRequest {

  private final String name;
  private final String key;
  private final int permits;

  private PermitRequest(String name, String key, int permits) {
    this.name = name;
    this.key = key;
    this.permits = permits;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  String getName() {
    return name;
  }

  String getKey() {
    return key;
  }

  int getPermits() {
    return permits;
  }

  @NotThreadSafe
  public static final class Builder {

    private String name;
    private String key;
    private Integer permits;

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

      return new PermitRequest(name, key, permits);
    }

    private Builder() {}
  }
}

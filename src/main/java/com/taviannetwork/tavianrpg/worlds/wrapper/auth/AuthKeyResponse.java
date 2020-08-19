package com.taviannetwork.tavianrpg.worlds.wrapper.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class AuthKeyResponse {
    private final boolean valid;
    private final AuthKey key;

    @JsonCreator
    public AuthKeyResponse(@JsonProperty("valid") boolean valid, @JsonProperty("key") AuthKey key) {
        this.valid = valid;
        this.key = key;
    }
}

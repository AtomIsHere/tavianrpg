package com.taviannetwork.tavianrpg.worlds.wrapper.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class AuthKey {
    private final String key;
    private final boolean admin;

    public AuthKey(@JsonProperty("key") String key, @JsonProperty("admin") boolean admin) {
        this.key = key;
        this.admin = admin;
    }
}

package com.taviannetwork.tavianrpg.worlds.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ConnectionInfo {
    private static final String URL_TEMPLATE = "http://%s:%s/%s?key=%s";
    private static final String URL_TEMPLATE_NO_KEY = "http://%s:%s/%s";

    private final String address;
    private final String port;

    private final UUID authKey;

    public ConnectionInfo(@JsonProperty("address") String address, @JsonProperty("port") String port, @JsonProperty("auth_key") UUID authKey) {
        this.address = address;
        this.port = port;

        this.authKey = authKey;
    }

    public String getBaseUrl(String endPoint, String options, boolean keyNeeded) {
        return keyNeeded ? String.format(URL_TEMPLATE, address, port, endPoint, authKey.toString()) + options : String.format(URL_TEMPLATE_NO_KEY, address, port, endPoint) + options;
    }
}

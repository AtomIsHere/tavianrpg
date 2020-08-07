package com.taviannetwork.tavianrpg.services;

public interface Service extends Dependable<Service> {
    default void init() {}
    default void start() {}
    default void tick() {}
    default void stop() {}
}

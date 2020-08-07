package com.taviannetwork.tavianrpg.services;

import java.util.Collections;
import java.util.List;

public interface Dependable<T> {
    default List<Class<? extends T>> getDependencies() {
        return Collections.emptyList();
    }
}

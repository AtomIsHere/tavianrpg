package com.taviannetwork.tavianrpg.services;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceInfo {
    String serviceName();
    String serviceVersion();
    String serviceAuthor();
}

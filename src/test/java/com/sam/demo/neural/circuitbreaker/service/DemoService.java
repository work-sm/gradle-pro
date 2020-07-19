package com.sam.demo.neural.circuitbreaker.service;

import com.sam.demo.neural.circuitbreaker.annotation.GuardByCircuitBreaker;

public interface DemoService {

    @GuardByCircuitBreaker(noTripExceptions = {})
    String getUuid(int idx);

    @GuardByCircuitBreaker(noTripExceptions = {IllegalArgumentException.class})
    void illegalEx(int idx);

}

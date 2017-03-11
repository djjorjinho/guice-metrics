package com.dll.metrics.impl;

import com.dll.metrics.traits.MetricPublisher;

import javax.annotation.Nullable;
import javax.inject.Inject;

public abstract class Interceptor {

    @Nullable
    protected MetricPublisher processor;

    @Inject
    public void setPublisher(@Nullable MetricPublisher publisher) {
        this.processor = publisher;
    }

}

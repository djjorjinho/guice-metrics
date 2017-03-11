package com.dll.metrics.impl;

import com.dll.metrics.annotations.Tag;
import com.dll.metrics.handler.PublishedEvent;
import com.dll.metrics.traits.MetricPublisher;
import com.dll.metrics.traits.TagProcessor;
import com.lmax.disruptor.dsl.Disruptor;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class MetricPublisherImpl implements MetricPublisher {


    private Disruptor<PublishedEvent> executorService;

    @Inject
    public MetricPublisherImpl(
            @Named("com.dll.metrics.executor") Disruptor<PublishedEvent> executorService
    ) {
        this.executorService = executorService;
    }

    @Override
    public void publish(
            Object value,
            String name,
            Tag[] tags,
            Class<? extends TagProcessor> tagProcessor,
            Object[] arguments,
            @Nullable Throwable throwable
    ) {
        executorService.publishEvent((event, sequence) -> {
            event.update(value, name, tags, tagProcessor, arguments, throwable);
        });
    }
}

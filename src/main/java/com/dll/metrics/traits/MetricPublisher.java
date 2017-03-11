package com.dll.metrics.traits;

import com.dll.metrics.annotations.Tag;

import javax.annotation.Nullable;

public interface MetricPublisher {
    void publish(Object value,
                 String name,
                 Tag[] tags,
                 Class<? extends TagProcessor> tagProcessor,
                 Object[] arguments,
                 @Nullable Throwable throwable);
}

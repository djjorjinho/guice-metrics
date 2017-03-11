package com.dll.metrics.handler;

import com.dll.metrics.annotations.Tag;
import com.dll.metrics.traits.TagProcessor;

import javax.annotation.Nullable;

public class PublishedEvent {

    public Object value;
    public String name;
    public Tag[] tags;
    public Class<? extends TagProcessor> tagProcessor;
    public Object[] arguments;
    public @Nullable Throwable throwable;

    public void update(
            Object value, String name, Tag[] tags, Class<? extends TagProcessor> tagProcessor, Object[] arguments, Throwable throwable) {
        this.value = value;
        this.name = name;
        this.tags = tags;
        this.tagProcessor = tagProcessor;
        this.arguments = arguments;
        this.throwable = throwable;
    }
}

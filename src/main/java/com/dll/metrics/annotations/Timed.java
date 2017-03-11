package com.dll.metrics.annotations;

import com.dll.metrics.processor.NoOpTagProcessor;
import com.dll.metrics.traits.TagProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.METHOD)
public @interface Timed {
    String value();
    Tag[] tags() default {};
    Class<? extends TagProcessor> tagProcessor() default NoOpTagProcessor.class; // can override tags()
}

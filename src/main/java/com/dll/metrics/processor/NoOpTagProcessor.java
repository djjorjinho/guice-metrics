package com.dll.metrics.processor;


import com.dll.metrics.traits.TagProcessor;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.Map;

@Singleton
public class NoOpTagProcessor implements TagProcessor {
    @Nullable
    public Map<String, String> process(Object[] methodArgs, Throwable throwable) {
        return null;
    }
}

package com.dll.metrics.traits;

import javax.annotation.Nullable;
import java.util.Map;

public interface TagProcessor {
    @Nullable
    Map<String, String> process(Object[] methodArgs, Throwable throwable);
}

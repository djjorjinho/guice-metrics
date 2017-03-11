package com.dll.metrics.traits;

import java.util.Map;

public interface Counter extends Metric {

    void increment(Map<String, String> tags);
    void increment(double count, Map<String, String> tags);

}

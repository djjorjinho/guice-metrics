package com.dll.metrics.traits;

import java.util.Map;

public interface Timer extends Metric {

    void observe(long duration, Map<String, String> tags);

}

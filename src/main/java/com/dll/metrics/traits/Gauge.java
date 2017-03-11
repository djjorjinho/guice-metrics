package com.dll.metrics.traits;

import java.util.Map;

public interface Gauge extends Metric {

    void observe(double value, Map<String, String> tags);

}

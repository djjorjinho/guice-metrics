# Guice Metrics Module

Use annotations and publish metrics asynchronously to any metrics library available.

Features:

* Implement and Bind metrics types - Timers, Counters, Gauges - register by name
* Share metrics by annotating methods with the metric name
* Enrich metrics with key-value pair tags - statically or dynamically from method arguments using tag processors
* Metrics processing does not block regular application execution

## Example

Please check out **PrometheusMetricsModule** and **MetricsModuleTest** classes on how to assemble a basic 
implementation using Prometheus.
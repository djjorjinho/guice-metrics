package com.dll.metrics;

import com.dll.metrics.annotations.Tag;
import com.dll.metrics.handler.MetricEventHandler;
import com.dll.metrics.handler.PublishedEvent;
import com.dll.metrics.traits.Counter;
import com.dll.metrics.traits.Gauge;
import com.dll.metrics.traits.Timer;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.multibindings.Multibinder;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Histogram;
import io.prometheus.client.Summary;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

import static com.google.inject.multibindings.Multibinder.newSetBinder;
import static com.google.inject.name.Names.named;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

public class PrometheusMetricsModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new MetricsModule());
        bind(MetricEventHandler.class).to(PrometheusMetricEventHandler.class);
        bind(CollectorRegistry.class).toInstance(CollectorRegistry.defaultRegistry);
        bind(Integer.class).annotatedWith(named("com.dll.metrics.buffer.size")).toInstance(4096);

        Multibinder<Timer> timerMultibinder = newSetBinder(binder(), Timer.class);
        timerMultibinder.addBinding().to(PromSummary.class);
        timerMultibinder.addBinding().to(PromHistogram.class);

        Multibinder<Counter> counterMultibinder = newSetBinder(binder(), Counter.class);
        counterMultibinder.addBinding().to(PromCounter.class);

        Multibinder<Gauge> gaugeMultibinder = newSetBinder(binder(), Gauge.class);
        gaugeMultibinder.addBinding().to(PromGauge.class);
    }

    @Singleton
    public static class PrometheusMetricEventHandler extends MetricEventHandler {
        private final Injector injector;

        @Inject
        public PrometheusMetricEventHandler(Injector injector) {
            this.injector = injector;
        }

        @Override
        public void onEvent(PublishedEvent event, long sequence, boolean endOfBatch) throws Exception {
            if (timers.containsKey(event.name)) {
                timers.get(event.name).observe((Long) event.value, getTags(event));
            }
            if (counters.containsKey(event.name)) {
                counters.get(event.name).increment((Integer) event.value, getTags(event));
            }
            if (gauges.containsKey(event.name)) {
                gauges.get(event.name).observe(((Number) event.value).doubleValue(), getTags(event));
            }
        }

        private Map<String, String> getTags(PublishedEvent event) {
            Map<String, String> tags = stream(event.tags).collect(toMap(Tag::key, Tag::value));
            Map<String, String> extra = injector.getInstance(event.tagProcessor).process(event.arguments, event.throwable);
            if (extra != null) {
                tags.putAll(extra);
            }
            return tags;
        }
    }

    @Singleton
    public static class PromSummary implements Timer {

        public static final String NAME = "some_summary";
        private final Summary metric;
        private static final String[] LABELS = {"label1", "label2", "label3"};

        @Inject
        public PromSummary(
            CollectorRegistry registry
        ) {
            metric = Summary.build()
                    .name(NAME)
                    .help("something to measure")
                    .labelNames(LABELS)
                    .quantile(0.50, 0.05)
                    .quantile(0.95, 0.005)
                    .quantile(0.99, 0.001)
                    .register(registry);
        }

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public void observe(long duration, Map<String, String> tags) {
            String[] values = stream(LABELS)
                    .map(s -> {
                        String value = tags.get(s);
                        if (value == null)
                            throw new IllegalStateException(format("Metric '%s' is missing the tag '%s'", NAME, s));
                        return value;
                    })
                    .toArray(String[]::new);
            metric.labels(values).observe(duration);
        }
    }

    @Singleton
    public static class PromHistogram implements Timer {

        public static final String NAME = "some_histogram";
        private final Histogram metric;
        private static final String[] LABELS = {};

        @Inject
        public PromHistogram(
            CollectorRegistry registry
        ) {
            metric = Histogram.build()
                    .name(NAME)
                    .help("something to measure")
                    .labelNames(LABELS)
                    .buckets(0.15, 0.35, 0.75)
                    .register(registry);
        }

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public void observe(long duration, Map<String, String> tags) {
            String[] values = stream(LABELS)
                    .map(s -> {
                        String value = tags.get(s);
                        if (value == null)
                            throw new IllegalStateException(format("Metric '%s' is missing the tag '%s'", NAME, s));
                        return value;
                    })
                    .toArray(String[]::new);
            metric.labels(values).observe(duration);
        }
    }

    @Singleton
    public static class PromCounter implements Counter {

        public static final String NAME = "some_counter";
        private final io.prometheus.client.Counter metric;
        private static final String[] LABELS = {};

        @Inject
        public PromCounter(
            CollectorRegistry registry
        ) {
            metric = io.prometheus.client.Counter.build()
                    .name(NAME)
                    .help("something to measure")
                    .labelNames(LABELS)
                    .register(registry);
        }

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public void increment(Map<String, String> tags) {
            increment(1, tags);
        }

        @Override
        public void increment(double count, Map<String, String> tags) {
            String[] values = stream(LABELS)
                .map(s -> {
                    String value = tags.get(s);
                    if (value == null)
                        throw new IllegalStateException(format("Metric '%s' is missing the tag '%s'", NAME, s));
                    return value;
                })
                .toArray(String[]::new);
            metric.labels(values).inc(count);
        }
    }

    @Singleton
    public static class PromGauge implements Gauge {

        public static final String NAME = "some_gauge";
        private final io.prometheus.client.Gauge metric;
        private static final String[] LABELS = {};

        @Inject
        public PromGauge(
            CollectorRegistry registry
        ) {
            metric = io.prometheus.client.Gauge.build()
                .name(NAME)
                .help("something to measure")
                .labelNames(LABELS)
                .register(registry);
        }

        @Override
        public String getName() {
            return NAME;
        }

        @Override
        public void observe(double value, Map<String, String> tags) {
            String[] values = stream(LABELS)
                .map(s -> {
                    String tagValue = tags.get(s);
                    if (tagValue == null)
                        throw new IllegalStateException(format("Metric '%s' is missing the tag '%s'", NAME, s));
                    return tagValue;
                })
                .toArray(String[]::new);
            metric.labels(values).set(value);
        }
    }

}
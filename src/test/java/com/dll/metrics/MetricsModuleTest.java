package com.dll.metrics;

import com.dll.metrics.PrometheusMetricsModule.PromCounter;
import com.dll.metrics.PrometheusMetricsModule.PromGauge;
import com.dll.metrics.PrometheusMetricsModule.PromHistogram;
import com.dll.metrics.annotations.Counted;
import com.dll.metrics.annotations.Gauged;
import com.dll.metrics.annotations.Tag;
import com.dll.metrics.annotations.Timed;
import com.dll.metrics.traits.TagProcessor;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import io.prometheus.client.Collector.MetricFamilySamples;
import io.prometheus.client.CollectorRegistry;
import org.junit.Test;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.Map;

import static com.dll.metrics.PrometheusMetricsModule.PromSummary;
import static org.junit.Assert.assertTrue;


public class MetricsModuleTest {

    @Test
    public void testInjector() throws Exception {
        Injector injector = Guice.createInjector(Stage.PRODUCTION, new PrometheusMetricsModule());
        TestSubject subject = injector.getInstance(TestSubject.class);
        subject.doSummary();
        subject.doSummary2();
        try {
            subject.doSummary3("y");
        } catch(Exception ex) {
        }
        subject.doHistogram();
        subject.doCounter();
        subject.doGauge();
        subject.doGauge2();
        subject.doGauge3();
        subject.doGauge4();

        Enumeration<MetricFamilySamples> samples = injector.getInstance(CollectorRegistry.class).metricFamilySamples();
        while (samples.hasMoreElements()) {
            MetricFamilySamples sample = samples.nextElement();
            assertTrue(sample.name, sample.samples.size() > 0);
        }
    }

    public static class TestSubject {

        @Timed(value = PromSummary.NAME, tags = @Tag(key = "label1", value = "x"))
        public void doSummary() throws InterruptedException {
            Thread.sleep(100);
        }

        @Timed(value = PromSummary.NAME, tags = {@Tag(key = "label1", value = "x"),@Tag(key = "label2", value = "x"), @Tag(key = "label3", value = "x")})
        public void doSummary2() throws InterruptedException {
            Thread.sleep(100);
        }

        @Timed(value = PromSummary.NAME, tags = @Tag(key = "label1", value = "x"), tagProcessor = OpTagProcessor.class)
        public void doSummary3(String tag2) throws Exception {
            Thread.sleep(100);
            throw new Exception(tag2);
        }

        @Timed(PromHistogram.NAME)
        public void doHistogram() throws InterruptedException {
            Thread.sleep(200);
        }

        @Counted(PromCounter.NAME)
        public void doCounter() {

        }

        @Gauged(PromGauge.NAME)
        public BigDecimal doGauge() {
            return BigDecimal.ONE;
        }

        @Gauged(PromGauge.NAME)
        public String doGauge2() {
            return "";
        }

        @Gauged(PromGauge.NAME)
        public Integer doGauge3() {
            return 1;
        }

        @Gauged(PromGauge.NAME)
        public Float doGauge4() {
            return 1.1F;
        }

        @Singleton
        public static class OpTagProcessor implements TagProcessor {
            @Nullable
            public Map<String, String> process(Object[] methodArgs, Throwable throwable) {
                return ImmutableMap.of("label2", methodArgs[0].toString(), "label3", String.valueOf(throwable != null));
            }
        }

    }
}
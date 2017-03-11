package com.dll.metrics.handler;

import com.dll.metrics.traits.Counter;
import com.dll.metrics.traits.Gauge;
import com.dll.metrics.traits.Timer;
import com.lmax.disruptor.EventHandler;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public abstract class MetricEventHandler implements EventHandler<PublishedEvent> {

    protected Map<String, Timer> timers = new HashMap<>();
    protected Map<String, Counter> counters = new HashMap<>();
    protected Map<String, Gauge> gauges = new HashMap<>();

    @Inject
    public void setTimers(@Nullable Set<Timer> timers) {
        this.timers = ofNullable(timers).map(t -> t.stream().collect(toMap(Timer::getName, identity()))).orElseGet(HashMap::new);
    }

    @Inject
    public void setCounters(@Nullable Set<Counter> counters) {
        this.counters = ofNullable(counters).map(c -> c.stream().collect(toMap(Counter::getName, identity()))).orElseGet(HashMap::new);
    }

    @Inject
    public void setGauges(@Nullable Set<Gauge> gauges) {
        this.gauges = ofNullable(gauges).map(g -> g.stream().collect(toMap(Gauge::getName, identity()))).orElseGet(HashMap::new);
    }

}

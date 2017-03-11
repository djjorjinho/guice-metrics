package com.dll.metrics;

import com.dll.metrics.annotations.Counted;
import com.dll.metrics.annotations.Gauged;
import com.dll.metrics.annotations.Timed;
import com.dll.metrics.handler.MetricEventHandler;
import com.dll.metrics.handler.PublishedEvent;
import com.dll.metrics.impl.CountedInterceptor;
import com.dll.metrics.impl.GaugedInterceptor;
import com.dll.metrics.impl.MetricPublisherImpl;
import com.dll.metrics.impl.TimedInterceptor;
import com.dll.metrics.traits.MetricPublisher;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.matcher.Matchers;
import com.lmax.disruptor.IgnoreExceptionHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;

import javax.inject.Named;
import javax.inject.Singleton;

import static com.google.inject.matcher.Matchers.annotatedWith;

public class MetricsModule extends AbstractModule {
    protected void configure() {
        bindInterceptor(Matchers.any(), annotatedWith(Timed.class), getTimedInterceptor());
        bindInterceptor(Matchers.any(), annotatedWith(Counted.class), getCountedInterceptor());
        bindInterceptor(Matchers.any(), annotatedWith(Gauged.class), getGaugedInterceptor());
        bind(MetricPublisher.class).to(MetricPublisherImpl.class);
    }

    private TimedInterceptor getTimedInterceptor() {
        TimedInterceptor interceptor = new TimedInterceptor();
        requestInjection(interceptor);
        return interceptor;
    }

    private CountedInterceptor getCountedInterceptor() {
        CountedInterceptor interceptor = new CountedInterceptor();
        requestInjection(interceptor);
        return interceptor;
    }

    private GaugedInterceptor getGaugedInterceptor() {
        GaugedInterceptor interceptor = new GaugedInterceptor();
        requestInjection(interceptor);
        return interceptor;
    }

    @Provides
    @Singleton
    @Named("com.dll.metrics.executor")
    public Disruptor<PublishedEvent> provideMetricsDisruptor(
            MetricEventHandler eventHandler,
            @Named("com.dll.metrics.buffer.size") Integer ringBufferSize
    ) {
        Disruptor<PublishedEvent> disruptor = new Disruptor<>(
            PublishedEvent::new,
            ringBufferSize,
            DaemonThreadFactory.INSTANCE
        );
        disruptor.handleEventsWith(eventHandler);
        disruptor.handleExceptionsFor(eventHandler).with(new IgnoreExceptionHandler());
        disruptor.start();
        return disruptor;
    }

}

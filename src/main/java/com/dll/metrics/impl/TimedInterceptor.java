package com.dll.metrics.impl;

import com.dll.metrics.annotations.Timed;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class TimedInterceptor extends Interceptor implements MethodInterceptor {

    public Object invoke(MethodInvocation invocation) throws Throwable {
        Throwable throwable = null;
        Object o = null;
        long start = System.currentTimeMillis();
        try {
            o =  invocation.proceed();
        } catch (Throwable ex) {
            throwable = ex;
        }
        long end = System.currentTimeMillis();

        Timed annotation = invocation.getMethod().getAnnotation(Timed.class);
        processor.publish(
                end - start,
                annotation.value(),
                annotation.tags(),
                annotation.tagProcessor(),
                invocation.getArguments(),
                throwable);

        if (throwable != null) {
            throw throwable;
        }

        return o;
    }

}

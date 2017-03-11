package com.dll.metrics.impl;

import com.dll.metrics.annotations.Gauged;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class GaugedInterceptor extends Interceptor implements MethodInterceptor {

    public Object invoke(MethodInvocation invocation) throws Throwable {
        Throwable throwable = null;
        Object o = null;
        try {
            o =  invocation.proceed();
        } catch (Throwable ex) {
            throwable = ex;
        }

        Gauged annotation = invocation.getMethod().getAnnotation(Gauged.class);
        processor.publish(
                o,
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

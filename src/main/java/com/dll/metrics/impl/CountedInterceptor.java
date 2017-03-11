package com.dll.metrics.impl;

import com.dll.metrics.annotations.Counted;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class CountedInterceptor extends Interceptor implements MethodInterceptor {

    public Object invoke(MethodInvocation invocation) throws Throwable {
        Throwable throwable = null;
        Object o = null;
        try {
            o =  invocation.proceed();
        } catch (Throwable ex) {
            throwable = ex;
        }

        Counted annotation = invocation.getMethod().getAnnotation(Counted.class);
        processor.publish(
                1,
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

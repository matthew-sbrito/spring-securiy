package com.techsoft.api.common.helper;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

@Component
public class StaticContextAccessor {

    private static final Map<Class, DynamicInvocationHandler> classHandlers = new HashMap<>();
    private static ApplicationContext applicationContext;

    @Autowired
    public StaticContextAccessor(ApplicationContext applicationContext) {
        StaticContextAccessor.applicationContext = applicationContext;
    }

    public static <T> T getBean(Class<T> clazz) {
        if (applicationContext == null) {
            return getProxy(clazz);
        }
        return applicationContext.getBean(clazz);
    }

    private static <T> T getProxy(Class<T> clazz) {
        DynamicInvocationHandler<T> invocationHandler = new DynamicInvocationHandler<>();
        classHandlers.put(clazz, invocationHandler);
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[]{clazz},
                invocationHandler
        );
    }

    @PostConstruct
    private void init() {
        classHandlers.forEach((clazz, invocationHandler) -> {
            Object bean = applicationContext.getBean(clazz);
            invocationHandler.setActualBean(bean);
        });
    }

    static class DynamicInvocationHandler<T> implements InvocationHandler {

        private T actualBean;

        public void setActualBean(T actualBean) {
            this.actualBean = actualBean;
        }

        @Override
        @SneakyThrows
        public Object invoke(Object proxy, Method method, Object[] args) {
            if (actualBean == null) {
                throw new RuntimeException("Not initialized yet! :(");
            }
            return method.invoke(actualBean, args);
        }
    }
}
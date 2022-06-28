package com.bubble.status.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContext implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        SpringContext.context = context;
    }

    public ApplicationContext getApplicationContext() {
        return context;
    }

    // Generic method to return a beanClass
    public static <T> T getBean(Class<T> beanClass)
    {
        return context.getBean(beanClass);
    }
}

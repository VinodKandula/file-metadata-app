package com.filemetadata.common.exception;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author Vinod Kandula
 */
@Configuration
@Getter
public class CommonExceptionConfig {
    @Value("${spring.application.name}")
    private String appName;

    private static ApplicationContext context;

    private static CommonExceptionProperty commonExceptionProperty;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        context = applicationContext;
        commonExceptionProperty = context.getBean(CommonExceptionProperty.class);
    }

    public static CommonExceptionProperty getFusionExceptionProperty() {
        return commonExceptionProperty;
    }

    @Autowired(required = false)
    @Qualifier(value = "exceptionInterceptors")
    private List<CommonExceptionInterceptor> exceptionInterceptors;

}

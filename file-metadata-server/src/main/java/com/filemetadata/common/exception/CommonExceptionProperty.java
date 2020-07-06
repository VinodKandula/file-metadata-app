package com.filemetadata.common.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Optional;

/**
 * Common Exception properties reader
 *
 * @author Vinod Kandula
 */
@Setter
@Getter
@ConfigurationProperties
@Component
@PropertySource(value = "classpath:exception-common.properties")
@PropertySource(value = "classpath:exception.properties", ignoreResourceNotFound = true)
public class CommonExceptionProperty {

    private Map<String, String> errorCodeMap;

    public String getValue(@NotNull String code) {
        return Optional.ofNullable(errorCodeMap).
                map(c -> c.get(code)).
                orElseThrow(() -> new IllegalArgumentException("No value found in exception-common or application.properties for error code : " + code));
    }
}


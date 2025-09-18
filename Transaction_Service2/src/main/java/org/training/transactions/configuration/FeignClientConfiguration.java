package org.training.transactions.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.training.transactions.configuration.FeignClientErrorDecoder;

/**
 * Per-Feign-client configuration class.
 *
 * Use it on a Feign client like:
 *   @FeignClient(name = "remote", configuration = FeignClientConfiguration.class)
 */
@Configuration
public class FeignClientConfiguration {

    private final ObjectMapper objectMapper;

    public FeignClientConfiguration(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignClientErrorDecoder(objectMapper);
    }

    /**
     * Forwards Authorization header from incoming request to outgoing feign requests.
     * Useful when your controller receives a bearer/basic header and you need to call an upstream
     * service using the same credentials.
     *
     * If you don't want header forwarding, remove this bean.
     */
    @Bean
    public RequestInterceptor forwardAuthHeaderInterceptor() {
        return template -> {
            // Obtain the current HTTP request (may be null if called outside an HTTP request)
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return;
            String auth = attrs.getRequest().getHeader("Authorization");
            if (auth != null && !auth.isBlank()) {
                template.header("Authorization", auth);
            }
        };
    }
}

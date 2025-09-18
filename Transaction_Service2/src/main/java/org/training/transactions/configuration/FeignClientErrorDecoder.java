package org.training.transactions.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.training.transactions.exception.ErrorResponse;
import org.training.transactions.exception.GlobalErrorCode;
import org.training.transactions.exception.GlobalException;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * Feign ErrorDecoder that attempts to parse the response body into ErrorResponse.
 * If parsing succeeds we create GlobalException with provided code/message, otherwise
 * we create a GlobalException with the raw body text.
 */
public class FeignClientErrorDecoder implements ErrorDecoder {

    private static final Logger log = LoggerFactory.getLogger(FeignClientErrorDecoder.class);
    private final ErrorDecoder defaultDecoder = new ErrorDecoder.Default();
    private final ObjectMapper mapper;

    public FeignClientErrorDecoder(ObjectMapper mapper) {
        // clone or configure mapper to be safe
        this.mapper = mapper.copy()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response == null || response.body() == null) {
            return defaultDecoder.decode(methodKey, response);
        }

        String bodyString;
        try (Reader reader = response.body().asReader(StandardCharsets.UTF_8)) {
            StringBuilder sb = new StringBuilder();
            char[] buffer = new char[1024];
            int r;
            while ((r = reader.read(buffer)) != -1) {
                sb.append(buffer, 0, r);
            }
            bodyString = sb.toString();
        } catch (IOException e) {
            log.error("Unable to read feign response body", e);
            return defaultDecoder.decode(methodKey, response);
        }

        if (bodyString == null || bodyString.trim().isEmpty()) {
            return defaultDecoder.decode(methodKey, response);
        }

        try {
            ErrorResponse err = mapper.readValue(bodyString, ErrorResponse.class);
            String code = err.getErrorCode() != null ? err.getErrorCode() : GlobalErrorCode.INTERNAL;
            String msg  = err.getMessage()   != null ? err.getMessage()   : bodyString;
            return new GlobalException(msg, code);
        } catch (Exception ex) {
            // Could not parse ErrorResponse — fallback to raw body
            log.debug("Feign body is not ErrorResponse JSON; using raw body", ex);
            return new GlobalException(bodyString, GlobalErrorCode.INTERNAL);
        }
    }
}

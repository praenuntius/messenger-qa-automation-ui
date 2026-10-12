package org.foxtrot.api;

import io.restassured.filter.FilterContext;
import io.restassured.filter.OrderedFilter;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmartLoggingFilter implements OrderedFilter {

    private static final Logger log = LoggerFactory.getLogger(SmartLoggingFilter.class);

    @Override
    public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
        Response response;
        try {
            response = ctx.next(requestSpec, responseSpec);
        } catch (Exception e) {
            log.error("❌ API request failed before response\nURL: {}\nMethod: {}\nError: {}",
                    requestSpec.getURI(),
                    requestSpec.getMethod(),
                    e.getMessage(),
                    e);
            throw e;
        }

        if (response == null) {
            log.error("❌ API: empty response (network timeout / connection reset?)\nURL: {}\nMethod: {}\nHeaders: {}\nRequest Body: {}",
                    requestSpec.getURI(),
                    requestSpec.getMethod(),
                    formatHeaders(requestSpec),
                    formatBody(requestSpec));
            throw new RuntimeException("API returned empty response - network timeout/connection reset");
        }

        if (response.getStatusCode() >= 400) {
            log.error("❌ API Error: {} {}\nURL: {}\nHeaders: {}\nRequest Body: {}\nResponse Body: {}",
                    response.getStatusCode(),
                    response.getStatusLine(),
                    requestSpec.getURI(),
                    formatHeaders(requestSpec),
                    formatBody(requestSpec),
                    response.getBody().asString());
        } else {
            log.info("✅ API Success: {} {}", response.getStatusCode(), requestSpec.getURI());
        }

        return response;
    }

    private static String formatHeaders(FilterableRequestSpecification requestSpec) {
        return requestSpec.getHeaders().asList().stream()
                .map(h -> h.getName() + ": " + h.getValue())
                .collect(java.util.stream.Collectors.joining(", "));
    }

    private static String formatBody(FilterableRequestSpecification requestSpec) {
        return requestSpec.getBody() != null ? requestSpec.getBody().toString() : "empty";
    }

    @Override
    public int getOrder() {
        return 0;
    }
}

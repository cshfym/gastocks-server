package com.gastocks.server.filters

import javax.servlet.*
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import javax.servlet.http.HttpServletRequest

@Slf4j
@Component
class LoggingFilter implements Filter {

    @Override
    void init(FilterConfig filterConfig) throws ServletException { }

    @Override
    void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        if (request && request instanceof HttpServletRequest) {
            def path = ((HttpServletRequest)request).requestURL.toString()
            log.debug("Request at path: [${path}]")
        }


        filterChain.doFilter( request, response )
    }

    @Override
    void destroy() { }
}

package nile.org.in.config;

import nile.org.in.filter.RateLimiterFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimiterConfig {
    @Autowired
    private RateLimiterFilter rateLimitingFilter;

    @Bean
    public FilterRegistrationBean<RateLimiterFilter> rateLimitingFilterRegistration() {
        FilterRegistrationBean<RateLimiterFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(rateLimitingFilter);
        registrationBean.addUrlPatterns("/*"); // Apply filter to all URL patterns
        return registrationBean;
    }
}

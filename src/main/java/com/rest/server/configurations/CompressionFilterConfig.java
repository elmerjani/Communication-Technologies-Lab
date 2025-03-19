package com.rest.server.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

@Configuration
public class CompressionFilterConfig {

    @Bean
    public FilterRegistrationBean<CustomCompressionFilter> compressionFilter() {
        FilterRegistrationBean<CustomCompressionFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CustomCompressionFilter());


        registrationBean.addUrlPatterns("/*");

        registrationBean.setOrder(1);
        return registrationBean;
    }
}

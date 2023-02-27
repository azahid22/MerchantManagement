package com.merchant.management.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class MerchantAPIConfig {
    @Bean
    public MyStaticKeyAuthAspect staticKeyAuthAspect() {
        return new MyStaticKeyAuthAspect();
    }
}

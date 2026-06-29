package com.rank.application.config;

import com.rank.domain.sign.factory.SignFactory;
import com.rank.domain.sign.service.SignDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 报名领域服务Bean注册配置
 * 将domain层不含Spring注解的SignDomainService和SignFactory注册为Spring Bean
 */
@Configuration
public class SignDomainServiceConfig {

    @Bean
    public SignDomainService signDomainService() {
        return new SignDomainService();
    }

    @Bean
    public SignFactory signFactory() {
        return new SignFactory();
    }
}

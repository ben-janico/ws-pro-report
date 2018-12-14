package com.aurea.wsproreport.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import lombok.Getter;
import lombok.Setter;

@Configuration
@PropertySource("classpath:application.properties")
public class MailSettingsConfig {

    @Getter
    @Setter
    @Value("${mail.smtp.host}")
    private String host;

    @Getter
    @Setter
    @Value("${mail.smtp.port}")
    private String port;

    @Getter
    @Setter
    @Value("${mail.smtp.auth}")
    private String auth;

    @Getter
    @Setter
    @Value("${mail.smtp.starttls.enable}")
    private String starttls;

    @Getter
    @Setter
    @Value("${mail.auth.username}")
    private String username;

    @Getter
    @Setter
    @Value("${mail.auth.password}")
    private String password;

    @Getter
    @Setter
    @Value("${mail.replyTo}")
    private String replyTo;

    @Getter
    @Setter
    @Value("${mail.cc.list}")
    private String ccList;
}

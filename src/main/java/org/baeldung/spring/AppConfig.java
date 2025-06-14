package org.baeldung.spring;

import org.baeldung.security.ActiveUserStore;
import org.baeldung.service.SecretaireService;
import org.baeldung.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;

@Configuration
public class AppConfig {
    // beans
    @Autowired
    private UserService userService;
    @Autowired
    private SecretaireService secretaireService;
    @Bean
    public Java8TimeDialect java8TimeDialect() {
        return new Java8TimeDialect();
    }

    @Bean
    public ActiveUserStore activeUserStore() {
        return new ActiveUserStore(userService, secretaireService);
    }

}
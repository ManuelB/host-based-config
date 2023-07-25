package de.health.service;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import de.health.service.keycloak.HostBasedConfigResolver;

@SpringBootApplication
public class App {

    /**
     * Set /etc/hosts to
     * 127.0.0.1       localhost
     * 127.0.0.1       localhost-ti
     * 127.0.0.1       localhost-non-ti
     * @param args
     */
  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }

  @Bean
  @ConditionalOnMissingBean(HostBasedConfigResolver.class)
  public KeycloakConfigResolver keycloakConfigResolver() {
    return new HostBasedConfigResolver();
  }

}


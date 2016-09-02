package uk.ac.ebi.eva.server.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@SpringBootApplication
@RestController
@EnableResourceServer
@EnableAuthorizationServer
@ComponentScan(basePackages = {"uk.ac.ebi.eva.core.oauth"})
@EntityScan(basePackages = {"uk.ac.ebi.eva.persistence.entities"})
@EnableJpaRepositories(basePackages = {"uk.ac.ebi.eva.persistence.dao"})
public class AuthApplication {

    private static Logger log = LoggerFactory.getLogger(AuthApplication.class);

    /**
     * Token verification endpoint. This endpoint is not secured.
     * @param user
     * @return
     */
    @RequestMapping("/user")
    public Principal user(Principal user) {
        return user;
    }

	public static void main(String[] args) {
		SpringApplication.run(AuthApplication.class, args);
	}

    /**
     * This bean method exposes a AuthorizationServerConfigurer that configures the oauth server to use
     * our custom database authentication created with Spring-jpa.
     * @return
     */
	@Bean
    public AuthorizationServerConfigurer authorizationServerConfigurer(){
	    return new DatabaseAuthorizationServerConfigurer();
    }
}

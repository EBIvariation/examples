package uk.ac.ebi.eva.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.eva.persistence.repository.CountGroupByRepositoryImpl;

@SpringBootApplication
@RestController
@EntityScan(basePackages = {"uk.ac.ebi.eva.persistence.entities"})
@EnableJpaRepositories(basePackages = {"uk.ac.ebi.eva.persistence.dao","uk.ac.ebi.eva.persistence.repository"}, repositoryBaseClass = CountGroupByRepositoryImpl.class)
public class Application {

    private static Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}

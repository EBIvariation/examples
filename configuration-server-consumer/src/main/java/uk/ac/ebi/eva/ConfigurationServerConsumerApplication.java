package uk.ac.ebi.eva;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ConfigurationServerConsumerApplication {

    @Autowired
    public ConsumerConfiguration consumerConfiguration;

    @RequestMapping("/")
    public String message() {
        return consumerConfiguration.getMessage();
    }

    @RequestMapping("/dba1Name")
    public String getDba1Name() {
        return consumerConfiguration.getDba1name();
    }

    public static void main(String[] args) {
        SpringApplication.run(ConfigurationServerConsumerApplication.class, args);
    }
}

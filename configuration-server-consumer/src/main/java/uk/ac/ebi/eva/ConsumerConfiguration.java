package uk.ac.ebi.eva;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@RefreshScope
public class ConsumerConfiguration {

    @Value("${message}")
    private String message;

    @Value("${dba1.name:This is a default name !}")
    private String dba1name;

    public String getMessage() {
        return message;
    }

    public String getDba1name() {
        return dba1name;
    }
}

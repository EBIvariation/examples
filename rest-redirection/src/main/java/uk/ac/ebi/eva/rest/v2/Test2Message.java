package uk.ac.ebi.eva.rest.v2;

import javax.validation.constraints.NotNull;

/**
 * Created by jorizci on 03/11/16.
 */
public class Test2Message {

    @NotNull
    private String message;

    public String getMessage() {
        return message;
    }
}

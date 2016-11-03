package uk.ac.ebi.eva.rest.v1;

import javax.validation.constraints.NotNull;

/**
 * Created by jorizci on 03/11/16.
 */
public class Test1Message {

    @NotNull
    public Integer number;

    public Integer getNumber() {
        return number;
    }
}

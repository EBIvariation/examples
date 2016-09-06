package uk.ac.ebi.eva.server.rest.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by jorizci on 01/09/16.
 */
@ResponseStatus(value= HttpStatus.UNAUTHORIZED, reason="You don't have the required grants for this resource")
public class UnauthorizedException extends RuntimeException {
}

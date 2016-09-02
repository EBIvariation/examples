package uk.ac.ebi.eva.server.core.oauth.exceptions;

import org.springframework.security.core.AuthenticationException;

public class AuthenticationNameNull extends AuthenticationException {

    public AuthenticationNameNull(String msg) {
        super(msg);
    }

    public AuthenticationNameNull(String msg, Throwable t) {
        super(msg, t);
    }
}

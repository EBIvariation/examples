package uk.ac.ebi.eva.server.rest;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.UserDeniedAuthorizationException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by jorizci on 01/09/16.
 */
@RestController
public class SecureWithSpecialRole2 {

    @RequestMapping(path = "/securesudo2",method = RequestMethod.GET)
    @ResponseBody
    public String getManifestAttributes(HttpServletRequest request, HttpSession httpSession)  {
        if(request.isUserInRole("GROUP_ADMIN")) {
            return "OK! " + SecurityContextHolder.getContext().getAuthentication().getName();
        }else{
            throw new UserDeniedAuthorizationException("This message is not visible...");
        }

    }

    @ResponseStatus(value= HttpStatus.UNAUTHORIZED, reason = "This message is visible!")
    @ExceptionHandler(UserDeniedAuthorizationException.class)
    public void exceptionHandler(){
        //Do nothing, this is Spring Magic in action!
    }

}
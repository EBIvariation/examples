package uk.ac.ebi.eva.server.rest;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.eva.server.rest.exceptions.UnauthorizedException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by jorizci on 01/09/16.
 */
@RestController
public class SecureWithSpecialRole {

    @RequestMapping(path = "/securesudo",method = RequestMethod.GET)
    @ResponseBody
    public String getManifestAttributes(HttpServletRequest request, HttpSession httpSession) {
        // DO note that this only covers roles that start by ROLE_****
        if(request.isUserInRole("ADMIN")) {
            return "OK! " + SecurityContextHolder.getContext().getAuthentication().getName();
        }else{
            throw new UnauthorizedException();
        }

    }

}

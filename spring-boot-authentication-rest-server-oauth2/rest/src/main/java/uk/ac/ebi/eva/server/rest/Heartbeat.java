package uk.ac.ebi.eva.server.rest;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by jorizci on 18/08/16.
 */
@RestController
public class Heartbeat {

    @RequestMapping(path = "/heartbeat",method = RequestMethod.GET)
    @ResponseBody
    public String getManifestAttributes(HttpServletRequest request, HttpSession httpSession) {

        return "OK! "+SecurityContextHolder.getContext().getAuthentication().getName();

    }
}

package uk.ac.ebi.eva.rest.v2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.eva.rest.RestBaseExceptionHandling;
import uk.ac.ebi.eva.rest.RestMappings;
import uk.ac.ebi.eva.rest.VersionSwitchController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


/**
 * Created by jorizci on 01/11/16.
 */
@RestController
@RequestMapping(RestMappings.V2_ROOT)
@RestBaseExceptionHandling
public class Test2Controller {

    private final static Logger logger = LoggerFactory.getLogger(VersionSwitchController.class);

    @PostMapping(RestMappings.TEST)
    public void test(@Valid @RequestBody Test2Message message, HttpServletResponse response) {
        logger.debug("V2 service entered with message '" + message.getMessage() + "'");
    }

}

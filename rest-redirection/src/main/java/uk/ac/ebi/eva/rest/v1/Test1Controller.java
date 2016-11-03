package uk.ac.ebi.eva.rest.v1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.eva.rest.RestBaseExceptionHandling;
import uk.ac.ebi.eva.rest.RestMappings;
import uk.ac.ebi.eva.rest.VersionSwitchController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * Created by jorizci on 01/11/16.
 */
@RestController
@RequestMapping(RestMappings.V1_ROOT)
@RestBaseExceptionHandling
public class Test1Controller {

    private final static Logger logger = LoggerFactory.getLogger(VersionSwitchController.class);

    @PostMapping(RestMappings.TEST)
    public void test(@Valid @RequestBody Test1Message message, HttpServletResponse response, ModelMap modelMap, Model model) {
        logger.debug("V1 service entered with number '" + message.getNumber() + "'");
    }
}

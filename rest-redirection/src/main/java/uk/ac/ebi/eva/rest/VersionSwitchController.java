package uk.ac.ebi.eva.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.eva.exceptions.VersionNotSupportedException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jorizci on 01/11/16.
 */
@RestController
@ControllerAdvice()
public class VersionSwitchController {

    private final static Logger logger = LoggerFactory.getLogger(VersionSwitchController.class);
    private static final String API_VERSION_TAG = "api-version";

    @PostMapping(RestMappings.TEST)
    public void testSwitch(@RequestParam(name = API_VERSION_TAG, required = false) String apiVersion, HttpMethod method, HttpServletRequest request,
                           HttpServletResponse response) throws VersionNotSupportedException, ServletException, IOException {

        if (apiVersion == null) {
            apiVersion = request.getHeader(API_VERSION_TAG);
        }
        if (apiVersion == null) {
            apiVersion = getApiVersionInContentType(request);
        }

        logger.debug("Rest endpoint switch: " + apiVersion);
        switch (apiVersion) {
            case "1":
                request.getRequestDispatcher(RestMappings.V1_ROOT + RestMappings.TEST).forward(request, response);
                break;
            case "2":
                request.getRequestDispatcher(RestMappings.V2_ROOT + RestMappings.TEST).forward(request, response);
                break;
            default:
                throw new VersionNotSupportedException();
        }
    }

    public String getApiVersionInContentType(HttpServletRequest request) {
        String contentType = request.getContentType();

        Pattern pattern = Pattern.compile("api-version=(\\S\\S*)[; ]?");
        Matcher matcher = pattern.matcher(contentType);
        if (!matcher.find()) {
            return null;
        }
        return matcher.group(1);
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    VndErrors versionTagNotFound(NullPointerException exception) {
        return new VndErrors("error", "No version was defined");
    }

    @ExceptionHandler(VersionNotSupportedException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    VndErrors versionNotSupportedExceptionHandler(VersionNotSupportedException exception) {
        return new VndErrors("error", exception.getMessage());
    }

}
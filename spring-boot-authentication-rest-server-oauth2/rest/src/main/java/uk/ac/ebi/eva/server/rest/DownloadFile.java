package uk.ac.ebi.eva.server.rest;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.eva.server.utils.MultipartFileStreamer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;

/**
 * Created by jorizci on 13/09/16.
 */
@RestController
public class DownloadFile {


    @RequestMapping(path = "/getFile",method = RequestMethod.GET)
    @ResponseBody
    public void getFile(HttpServletRequest request, HttpServletResponse response, HttpSession httpSession) throws IOException {

        MultipartFileStreamer.stream(request,response);
    }

}

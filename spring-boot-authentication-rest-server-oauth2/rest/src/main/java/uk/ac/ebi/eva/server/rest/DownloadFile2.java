package uk.ac.ebi.eva.server.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.eva.server.rest.another.ByteGenerator;
import uk.ac.ebi.eva.server.rest.another.ControlledStreaming;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Created by jorizci on 16/09/16.
 */
@RestController
public class DownloadFile2 {

    private final Logger logger = LoggerFactory.getLogger(DownloadFile2.class);

    @RequestMapping(path = "/getFile2",method = RequestMethod.GET)
    @ResponseBody
    public Resource getFile(@RequestParam int maxbytes, HttpServletRequest request, HttpServletResponse response, HttpSession httpSession) throws IOException {

        ByteGenerator byteGenerator = new ByteGenerator() {

            @Override
            public String getFilename() {
                return "test.txt";
            }

            @Override
            public String getContentType() {
                return MediaType.APPLICATION_OCTET_STREAM;
            }

            @Override
            public void generate(OutputStream outputStream) throws IOException {
                int linesize = 20;
                for(int i=0;i<maxbytes;i++){
                    byte[] tag = createByteTag(i,linesize,maxbytes);
                    for(int j=0;i<maxbytes && j<(linesize-1);i++,j++){
                        outputStream.write(tag[j]);
                    }
                    if(i<maxbytes) {
                        outputStream.write(13);
                    }
                }
            }

            @Override
            public String getETag() {
                return "generatedfile";
            }

            @Override
            public long getLastModified() {
                return 0;
            }

            @Override
            public Long getSize() {
                return null;
            }

            @Override
            public Long getExpires() {
                return null;
            }

            private byte[] createByteTag(int i,int linesize,int maxbytes) {
                String maxbytesString = ""+maxbytes;
                String currentBytes = ""+i;
                String string = new String();
                while(string.length()+currentBytes.length() < maxbytesString.length()){
                    string += "0";
                }
                string+=currentBytes +" bytes";
                while(string.length()<linesize-1){
                    string+=".";
                }
                return string.getBytes(StandardCharsets.UTF_8);
            }
        };

        return new InputStreamResource(ControlledStreaming.stream(request, response, byteGenerator));
    }
}

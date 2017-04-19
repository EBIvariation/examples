package uk.ac.ebi.eva.server.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.eva.server.utils.exceptions.PreconditionFailedException;
import uk.ac.ebi.eva.server.utils.exceptions.RangeRequestNotSatisfiable;
import uk.ac.ebi.eva.server.utils.exceptions.ResourceNotModified;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static org.springframework.http.HttpHeaders.*;

/**
 * Created by jorizci on 13/09/16.
 */
public class MultipartFileStreamer {

    //private static final int DEFAULT_BUFFER_SIZE = 5140; // 5KB.
    private static final int DEFAULT_BUFFER_SIZE = 9; // 10B.
    private static final long DEFAULT_EXPIRE_TIME = 604800000L; // ms 1week

    private static final String RANGE_REGEX = "^bytes=\\d*-\\d*(,\\d*-\\d*)*$";
    private static final Logger logger = LoggerFactory.getLogger(MultipartFileStreamer.class);
    private static final int RANGE_SUBSTRING_START = 6;
    private static final java.lang.String RANGE_SEPARATOR = ",";
    private static final String RANGE_MIN_MAX_SEPARATOR = "-";

    public static void stream(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.debug("Stream");

        StreamResource resource = new StreamResource("kiwi", null, APPLICATION_OCTET_STREAM, 0L) {
            @Override
            public void generateContent(OutputStream outputStream) throws IOException {
                for(int i=0;i<10;i++){
                    logger.debug("I'll create ten elements! "+(i+1));
                    for(int j = 0; j<10; j++) {
                        byte[] byteArray = new byte[]{(byte) ("" + j).toCharArray()[0]};
                        outputStream.write(byteArray);
                    }
                    logger.debug("I'm sleepy! Zzzzzzzzzzzzzzzzzzzz");
                    try {
                        Thread.sleep(10*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        long lastModified = (new Date()).getTime();

        try {
            checkHeaders(request, response, resource);
        } catch (ResourceNotModified resourceNotModified) {
            response.setHeader(ETAG, resource.getName()); // Required in 304.
            response.sendError(HttpServletResponse.SC_NOT_MODIFIED); //304
            return;
        } catch (PreconditionFailedException e) {
            response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED); //412
            return;
        } catch (RangeRequestNotSatisfiable rangeRequestNotSatisfiable) {
            if(resource.getSize()!=null)
                response.setHeader(CONTENT_RANGE, "bytes */" + resource.getSize());
            else
                response.setHeader(CONTENT_RANGE, "bytes */*");
            response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE); //416
            return;
        }
    }

    private static void checkHeaders(HttpServletRequest request, HttpServletResponse response, StreamResource resource) throws ResourceNotModified, PreconditionFailedException, RangeRequestNotSatisfiable, IOException {
        checkCachingHeader(request,resource);
        checkResumeHeader(request,resource);
        List<RangeRequest> rangeRequests = getRangeHeader(request,resource);

        response.reset();
        response.setBufferSize(DEFAULT_BUFFER_SIZE);
        response.setHeader(CONTENT_TYPE, resource.getContentType());
        logger.debug("Content-Type : {}", resource.getContentType());
        response.setHeader(CONTENT_DISPOSITION,"inline;filename=\""+resource.getName()+"\"");
        logger.debug("Content-Disposition : {}", "inline");
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("ETag", resource.getName());
        response.setDateHeader("Last-Modified", resource.getLastModified());
        response.setDateHeader("Expires", System.currentTimeMillis() + DEFAULT_EXPIRE_TIME);

        if(rangeRequests.isEmpty()){
            sendResourceFull(response, resource);
        }

    }

    private static void sendResourceFull(HttpServletResponse response, StreamResource resource) throws IOException {
        response.setContentType(resource.getContentType());
        response.setHeader(CONTENT_RANGE, generateContentRangeHeader(0, resource.calculateEndByte(), resource.getSize()));
        if(resource.getSize()!=null) {
            response.setHeader(CONTENT_LENGTH, String.valueOf(resource.getSize()));
        }

        resource.copy(response, DEFAULT_BUFFER_SIZE);
    }

    private static String generateContentRangeHeader(long startByte, Long endByte, Long totalBytes) {
        String endByteString = (endByte!=null) ? endByte.toString() : "";
        String totalBytesString = (totalBytes!=null) ? totalBytes.toString() : "*";
        return "bytes " + startByte + "-" + endByteString + "/" + totalBytesString;
    }

    private static List<RangeRequest> getRangeHeader(HttpServletRequest request, StreamResource resource) throws RangeRequestNotSatisfiable {
        List<RangeRequest> rangeRequests = new ArrayList<>();

        // Range header should match format "bytes=n-n,n-n,n-n...". If not, then return 416.
        String range = request.getHeader(RANGE);
        if (range != null) {
            if (!range.matches(RANGE_REGEX)) {
                throw new RangeRequestNotSatisfiable();
            }

            String ifRange = request.getHeader(IF_RANGE);
            if (ifRange != null && !ifRange.equals(resource.getName())) {
                try {
                    long ifRangeTime = request.getDateHeader(IF_RANGE); // Throws IAE if invalid.
                    if (ifRangeTime != -1) {
                        //ranges.add(full);
                    }
                } catch (IllegalArgumentException ignore) {
                    //Need to get the full file
                    //ranges.add(full);
                }
            }
            for(String part: range.substring(RANGE_SUBSTRING_START).split(RANGE_SEPARATOR)){
                String startString = part.substring(0,part.indexOf(RANGE_MIN_MAX_SEPARATOR));
                String endString = part.substring(part.indexOf(RANGE_MIN_MAX_SEPARATOR+1));
                Long start = (startString.isEmpty())? null : Long.parseLong(startString);
                Long end = (endString.isEmpty())? null : Long.parseLong(endString);

                if((start == null && resource.getSize() == null) || (start!=null && end !=null && start<end) ){
                    //We can't get data from the end of the file if we don't know its total size.
                    throw new RangeRequestNotSatisfiable();
                }
                rangeRequests.add(new RangeRequest(start,end,resource.getSize()));
            }
        }

        return rangeRequests;
    }

    private static void checkResumeHeader(HttpServletRequest request, StreamResource resource) throws PreconditionFailedException {
        logger.debug("Check resume header");
        // If-Match header should contain "*" or ETag. If not, then return 412.
        String ifMatch = request.getHeader(IF_MATCH);
        if (ifMatch != null && !HttpUtils.matches(ifMatch, resource.getName())) {
            throw new PreconditionFailedException();
        }

        // If-Unmodified-Since header should be greater than LastModified. If not, then return 412.
        long ifUnmodifiedSince = request.getDateHeader("If-Unmodified-Since");
        if (ifUnmodifiedSince != -1 && ifUnmodifiedSince + 1000 <= resource.getLastModified()) {
            throw new PreconditionFailedException();
        }
    }

    private static void checkCachingHeader(HttpServletRequest request, StreamResource resource) throws ResourceNotModified {
        logger.debug("Check caching header");
        // If-None-Match header should contain "*" or ETag. If so, then return 304.
        //TODO check ETag?
        String ifNoneMatch = request.getHeader(IF_NONE_MATCH);
        if(ifNoneMatch!=null && HttpUtils.matches(ifNoneMatch,resource.getName())){
            throw new ResourceNotModified();
        }

        // If-Modified-Since header should be greater than LastModified. If so, then return 304.
        // This header is ignored if any If-None-Match header is specified.
        long ifModifiedSince = request.getDateHeader(IF_MODIFIED_SINCE);
        if (ifNoneMatch == null && ifModifiedSince != -1 && ifModifiedSince > resource.getLastModified()) {
            throw new ResourceNotModified();
        }
    }

}

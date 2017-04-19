package uk.ac.ebi.eva.server.rest.another;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.eva.server.utils.HttpUtils;
import uk.ac.ebi.eva.server.utils.RangeRequest;
import uk.ac.ebi.eva.server.utils.exceptions.PreconditionFailedException;
import uk.ac.ebi.eva.server.utils.exceptions.RangeRequestNotSatisfiable;
import uk.ac.ebi.eva.server.utils.exceptions.ResourceNotModified;

import javax.naming.LimitExceededException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpHeaders.IF_MODIFIED_SINCE;

/**
 * Created by jorizci on 16/09/16.
 */
public class ControlledStreaming {

    private final static Logger logger = LoggerFactory.getLogger(ControlledStreaming.class);
    private static final int PIPE_BUFFER = 32768; //32Kb

    private static final String RANGE_REGEX = "^bytes=\\d*-\\d*(,\\d*-\\d*)*$";
    private static final int RANGE_SUBSTRING_START = 6;
    private static final String RANGE_SEPARATOR = ",";
    private static final String RANGE_MIN_MAX_SEPARATOR = "-";
    private static final String MULTIPART_BYTERANGES = "MULTIPART_BYTERANGES";
    private static final String CONTENT_TYPE_MULTIPART_BYTERANGES = "multipart/byteranges; boundary=" + MULTIPART_BYTERANGES;

    public static InputStream stream(HttpServletRequest request, HttpServletResponse response, ByteGenerator byteGenerator) throws IOException {

        try {
            return process(request, response, byteGenerator);
        } catch (ResourceNotModified resourceNotModified) {
            response.setHeader(ETAG, byteGenerator.getETag()); // Required in 304.
            response.sendError(HttpServletResponse.SC_NOT_MODIFIED); //304
            return null;
        } catch (PreconditionFailedException e) {
            response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED); //412
            return null;
        } catch (RangeRequestNotSatisfiable rangeRequestNotSatisfiable) {
            if (byteGenerator.getSize() != null)
                response.setHeader(CONTENT_RANGE, "bytes */" + byteGenerator.getSize());
            else
                response.setHeader(CONTENT_RANGE, "bytes */*");
            response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE); //416
            return null;
        }
    }

    private static InputStream process(HttpServletRequest request, HttpServletResponse response, ByteGenerator byteGenerator) throws ResourceNotModified, PreconditionFailedException, RangeRequestNotSatisfiable, IOException {
        checkCachingHeader(request, byteGenerator);
        checkResumeHeader(request, byteGenerator);
        List<RangeRequest> rangeRequests = getRangeHeader(request, byteGenerator);

        response.reset();
        response.setHeader(CONTENT_DISPOSITION, "attachment; filename=" + byteGenerator.getFilename());
        response.setHeader(ACCEPT_RANGES, "bytes");
        response.setHeader(ETAG, byteGenerator.getFilename());
        response.setDateHeader(LAST_MODIFIED, byteGenerator.getLastModified());
        if (byteGenerator.getExpires() != null) {
            response.setDateHeader(EXPIRES, byteGenerator.getExpires());
        }
        if (rangeRequests.isEmpty()) {
            logger.debug("Send full file");
            RangeRequest fullRange = new RangeRequest(byteGenerator);
            return streamRange(response, byteGenerator, fullRange, false);
        } else {
            if (rangeRequests.size() == 1) {
                RangeRequest rangeRequest = rangeRequests.get(0);
                logger.debug("Send one file range: " + rangeRequest);
                return streamRange(response, byteGenerator, rangeRequests.get(0), true);
            } else {
                logger.debug("Send multiple file ranges: " + rangeRequests);
                return spawnMultipartGeneratorThread(response, byteGenerator, rangeRequests);
            }
        }
    }

    private static InputStream spawnMultipartGeneratorThread(HttpServletResponse response, ByteGenerator byteGenerator, List<RangeRequest> rangeRequests) {
        response.setContentType(CONTENT_TYPE_MULTIPART_BYTERANGES);
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.

        PipeOutputStream pipeOutputStream = new PipeOutputStream(PIPE_BUFFER);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.debug("Data Generation Thread has started.");

                    for (RangeRequest rangeRequest : rangeRequests) {
                        pipeOutputStream.write(System.lineSeparator().getBytes());
                        pipeOutputStream.write(("--" + MULTIPART_BYTERANGES).getBytes());
                        pipeOutputStream.write((CONTENT_TYPE + ": " + byteGenerator.getContentType()).getBytes());
                        pipeOutputStream.write((rangeRequest.getContentRange()).getBytes());

                        try {
                            byteGenerator.generate(new RangedOutputStream(pipeOutputStream, rangeRequest.getStart(), rangeRequest.getEnd()));
                        } catch (WriteLimitReachedException e) {
                            //Do nothing, generation limit reached (expected outcome if the range doesn't ask for full file
                        }
                    }
                    pipeOutputStream.write(System.lineSeparator().getBytes());
                    pipeOutputStream.write(("--" + MULTIPART_BYTERANGES + "--").getBytes());

                    pipeOutputStream.close();
                    logger.debug("Data Generation Thread has ended.");
                } catch (IOException ioException) {
                    //End thread
                    return;
                }
            }
        });
        thread.start();

        InputStream inputStream = pipeOutputStream.getInputStream();
        return inputStream;
    }

    private static InputStream streamRange(HttpServletResponse response, ByteGenerator byteGenerator, RangeRequest rangeRequest, boolean parcialRange) throws IOException {
        response.setContentType(byteGenerator.getContentType());
        response.setHeader(CONTENT_RANGE, rangeRequest.getContentRange());

        if (rangeRequest.getResourceSize() != null) {
            response.setHeader(CONTENT_LENGTH, String.valueOf(rangeRequest.getResourceSize()));
        }
        if (parcialRange) {
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206 required to acknowledge we are sending a range
        }

        //Launch thread with offset and limit
        return spawnGeneratorThread(byteGenerator, rangeRequest.getStart(), rangeRequest.getEnd());
    }

    private static void checkCachingHeader(HttpServletRequest request, ByteGenerator byteGenerator) throws ResourceNotModified {
        logger.debug("Check caching header");
        // If-None-Match header should contain "*" or ETag. If so, then return 304.
        //TODO check ETag?
        String ifNoneMatch = request.getHeader(IF_NONE_MATCH);
        if (ifNoneMatch != null && HttpUtils.matches(ifNoneMatch, byteGenerator.getETag())) {
            throw new ResourceNotModified();
        }

        // If-Modified-Since header should be greater than LastModified. If so, then return 304.
        // This header is ignored if any If-None-Match header is specified.
        long ifModifiedSince = request.getDateHeader(IF_MODIFIED_SINCE);
        if (ifNoneMatch == null && ifModifiedSince != -1 && ifModifiedSince > byteGenerator.getLastModified()) {
            throw new ResourceNotModified();
        }
    }

    private static void checkResumeHeader(HttpServletRequest request, ByteGenerator resource) throws PreconditionFailedException {
        logger.debug("Check resume header");
        // If-Match header should contain "*" or ETag. If not, then return 412.
        String ifMatch = request.getHeader(IF_MATCH);
        logger.debug(IF_MATCH + ": " + ifMatch);
        if (ifMatch != null && !HttpUtils.matches(ifMatch, resource.getFilename())) {
            throw new PreconditionFailedException();
        }

        // If-Unmodified-Since header should be greater than LastModified. If not, then return 412.
        long ifUnmodifiedSince = request.getDateHeader(IF_UNMODIFIED_SINCE);
        logger.debug(IF_UNMODIFIED_SINCE + ": " + ifMatch);
        if (ifUnmodifiedSince != -1 && ifUnmodifiedSince + 1000 <= resource.getLastModified()) {
            throw new PreconditionFailedException();
        }
    }

    private static List<RangeRequest> getRangeHeader(HttpServletRequest request, ByteGenerator byteGenerator) throws RangeRequestNotSatisfiable {
        List<RangeRequest> rangeRequests = new ArrayList<>();

        // Range header should match format "bytes=n-n,n-n,n-n...". If not, then return 416.
        String range = request.getHeader(RANGE);
        logger.debug(RANGE + ": " + range);
        if (range != null) {
            if (!range.matches(RANGE_REGEX)) {
                throw new RangeRequestNotSatisfiable();
            }

            String ifRange = request.getHeader(IF_RANGE);
            if (ifRange != null && !ifRange.equals(byteGenerator.getFilename())) {
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
            for (String part : range.substring(RANGE_SUBSTRING_START).split(RANGE_SEPARATOR)) {
                //Get start and end of the range, start and end can be null.
                Long start = (part.indexOf(RANGE_MIN_MAX_SEPARATOR) > 0) ? Long.parseLong(part.substring(0, part.indexOf(RANGE_MIN_MAX_SEPARATOR))) : null;
                Long end = (part.indexOf(RANGE_MIN_MAX_SEPARATOR) + 1 < part.length()) ? Long.parseLong(part.substring(part.indexOf(RANGE_MIN_MAX_SEPARATOR + 1))) : null;

                if ((start == null && byteGenerator.getSize() == null) || (start != null && end != null && start < end)) {
                    //We can't get data from the end of the file if we don't know its total size.
                    throw new RangeRequestNotSatisfiable();
                }
                rangeRequests.add(new RangeRequest(start, end, byteGenerator.getSize()));
            }
        }

        return rangeRequests;
    }

    private static InputStream spawnGeneratorThread(ByteGenerator byteGenerator, Long start, Long end) throws IOException {
        PipeOutputStream pipeOutputStream = new PipeOutputStream(PIPE_BUFFER);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.debug("Data Generation Thread has started.");
                    try {
                        byteGenerator.generate(new RangedOutputStream(pipeOutputStream, start, end));
                    } catch (WriteLimitReachedException e) {
                        //Do nothing, generation limit reached (expected outcome if the range doesn't ask for full file
                    }
                    pipeOutputStream.close();
                    logger.debug("Data Generation Thread has ended.");
                } catch (IOException ioException) {
                    //End thread
                    return;
                }
            }
        });
        thread.start();

        return pipeOutputStream.getInputStream();
    }

}

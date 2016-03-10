package uk.ac.ebi.variation.streamingws;

import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartMediaTypes;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.util.Scanner;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("/download")
public class StreamingServer {


    private File file;
    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Path("/{filename}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFileByFilename(@PathParam("filename") String fileName) {
        file = new File(getClass().getClassLoader().getResource(fileName).getFile());
        if (file.exists()) {
            StreamingOutput stream = new StreamingOutput() {

                @Override
                public void write(OutputStream outputStream) throws IOException, WebApplicationException {
                    Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream));

                    Scanner scanner = new Scanner(file);
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        writer.write(line + "\n");
                    }
                    writer.flush();
                    scanner.close();
                }
            };
            return Response.ok(stream).build();
        } else {
            return Response.serverError().build();
        }
    }




    // Multipart example: produces just one multipart file containing both files content, is not what we want
//    @GET
//    @Path("/multidata")
//    @Produces(MultiPartMediaTypes.MULTIPART_PARALLEL)
//    public Response getMultiData() {
//        final File file1 = new File(getClass().getClassLoader().getResource("ALL.chrY.phase3_integrated.20130502.genotypes.vcf").getFile());
//        final File file2 = new File(getClass().getClassLoader().getResource("egahh60-file.csv").getFile());
//        if (file1.exists() && file2.exists()) {
//            MultiPart multiPart = new MultiPart();
//
//            StreamingOutput stream1 = new StreamingOutput() {
//
//                @Override
//                public void write(OutputStream outputStream) throws IOException, WebApplicationException {
//                    Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream));
//
//                    Scanner scanner = new Scanner(file1);
//                    while (scanner.hasNextLine()) {
//                        String line = scanner.nextLine();
//                        writer.write(line + "\n");
//                    }
//                    writer.flush();
//                    scanner.close();
//                }
//            };
//
//            StreamingOutput stream2 = new StreamingOutput() {
//
//                @Override
//                public void write(OutputStream outputStream) throws IOException, WebApplicationException {
//                    Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream));
//
//                    Scanner scanner = new Scanner(file2);
//                    while (scanner.hasNextLine()) {
//                        String line = scanner.nextLine();
//                        writer.write(line + "\n");
//                    }
//                    writer.flush();
//                    scanner.close();
//                }
//            };
//            multiPart.bodyPart(stream1, MediaType.APPLICATION_OCTET_STREAM_TYPE);
//            multiPart.bodyPart(stream2, MediaType.APPLICATION_OCTET_STREAM_TYPE);
//            return Response.ok(multiPart).build();
//        } else {
//            return Response.status(Response.Status.NOT_FOUND).build();
//        }
//    }

}

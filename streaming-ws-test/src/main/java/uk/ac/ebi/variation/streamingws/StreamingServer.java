package uk.ac.ebi.variation.streamingws;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("/download")
public class StreamingServer {


    private File file;

    @GET
    @Path("/{filename}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFileByFilename(@PathParam("filename") String fileName) {
        file = new File(getClass().getClassLoader().getResource(fileName).getFile());
        if (file.exists()) {
            StreamingOutput stream = getStreamingOutput();
            return Response.ok(stream).build();
        } else {
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/vcfs.zip")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadZipFile() {
        final File file1 = new File(getClass().getClassLoader().getResource("test.vcf").getFile());
        final File file2 = new File(getClass().getClassLoader().getResource("test2.vcf").getFile());

        if (file1.exists() && file2.exists()) {
            StreamingOutput stream = new StreamingOutput() {
                @Override
                public void write(OutputStream outputStream) throws IOException {
                    byte[] buffer = new byte[1024];

                    ZipOutputStream zos = new ZipOutputStream(outputStream);

                    addFileContentToZipOutputStream(buffer, zos, file1);
                    addFileContentToZipOutputStream(buffer, zos, file2);

                    zos.close();
                }
            };

            return Response.ok(stream).build();
        } else {
            return Response.serverError().build();
        }
    }

    private void addFileContentToZipOutputStream(byte[] buffer, ZipOutputStream zos, File file) throws IOException {
        // add the first file to the zip
        ZipEntry zipEntry = new ZipEntry(file.getName());
        zos.putNextEntry(zipEntry);
        FileInputStream fis = new FileInputStream(file);

        int len;
        while ((len = fis.read(buffer)) > 0) {
            zos.write(buffer, 0, len);
        }

        fis.close();
        zos.closeEntry();
    }

//    /**
//     * Method handling HTTP GET requests. The returned object will be sent
//     * to the client as "text/plain" media type.
//     *
//     * @return String that will be returned as a text/plain response.
//     */
//    @GET
//    @Path("/tar")
//    @Produces(MediaType.APPLICATION_OCTET_STREAM)
//    public Response downloadTarFile() {
//        final File file1 = new File(getClass().getClassLoader().getResource("test.vcf").getFile());
//        File file2 = new File(getClass().getClassLoader().getResource("test2.vcf").getFile());
//
//        if (file1.exists() && file2.exists()) {
//            ByteOutputStream outputStream = new ByteOutputStream(65536);
//            //final TarArchiveOutputStream tarStream = new TarArchiveOutputStream(outputStream);
//            StreamingOutput stream = new StreamingOutput() {
//                @Override
//                public void write(OutputStream outputStream) throws IOException, WebApplicationException {
//                    TarArchiveOutputStream tarStream = new TarArchiveOutputStream(outputStream);
//                    TarArchiveEntry entry = new TarArchiveEntry(file1.getName());
//                    Writer writer = new BufferedWriter(new OutputStreamWriter(tarStream));
//                    tarStream.putArchiveEntry(entry);
//                    Scanner scanner = new Scanner(file1);
//                    try {
//                        while (scanner.hasNextLine()) {
//                            String line = scanner.nextLine();
//                            entry.setSize(entry.getSize() + line.length() + 1);
//                            writer.write(line + "\n");
//                        }
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    tarStream.closeArchiveEntry();
//                    writer.flush();
//                    scanner.close();
//
//                }
//            };
////            file = file1;
////            StreamingOutput stream = getStreamingOutput();
////            TarArchiveOutputStream tarStream = new TarArchiveOutputStream(stream.);
////            StreamingOutput stream = getStreamingOutput();
//            return Response.ok(stream).build();
//        } else {
//            return Response.serverError().build();
//        }
//    }

    private StreamingOutput getStreamingOutput() {
        return new StreamingOutput() {
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

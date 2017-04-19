package uk.ac.ebi.eva.server.rest.another;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public interface ByteGenerator {

    public String getFilename();

    public String getContentType();

    public void generate(OutputStream outputStream) throws IOException;

    String getETag();

    long getLastModified();

    Long getSize();

    Long getExpires();
}

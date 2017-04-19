package uk.ac.ebi.eva.server.utils;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by jorizci on 14/09/16.
 */
public abstract class StreamResource {

    private final String name;
    private final Long size;
    private final String contentType;
    private final long lastModified;

    public StreamResource(String name, Long size, String contentType, long lastModified) {
        this.name = name;
        this.size = size;
        this.contentType = contentType;
        this.lastModified = lastModified;
    }

    public String getName() {
        return name;
    }

    public Long getSize() {
        return size;
    }

    public String getContentType() {
        return contentType;
    }

    public long getLastModified() {
        return lastModified;
    }

    public Long calculateEndByte() {
        return (getSize() == null) ? null : getSize() - 1;
    }

    public void copy(HttpServletResponse response, int bufferSize) throws IOException {
        //This test now generates all the content in a temporal file at the beginning
        TemporalFilePipeline temporalFilePipeline = new TemporalFilePipeline();
        temporalFilePipeline.transfer(new DataGenerator() {
            @Override
            public void generate(OutputStream outputStream) throws IOException {
                generateContent(outputStream);
            }
        }, response.getOutputStream());
    }

    public abstract void generateContent(OutputStream outputStream) throws IOException;
}

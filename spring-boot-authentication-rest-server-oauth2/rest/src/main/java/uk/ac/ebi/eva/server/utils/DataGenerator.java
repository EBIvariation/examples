package uk.ac.ebi.eva.server.utils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by jorizci on 16/09/16.
 */
public interface DataGenerator {

    void generate(OutputStream outputStream) throws IOException;

}

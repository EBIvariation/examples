package uk.ac.ebi.eva.server.rest.another;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by jorizci on 22/09/16.
 */
public class RangedOutputStream extends OutputStream {

    private final OutputStream outputStream;
    private final Long start;
    private final Long end;
    private long counter;

    public RangedOutputStream(OutputStream outputStream, Long start, Long end) {
        this.outputStream = outputStream;
        this.start = start;
        this.end = end;
        counter++;
    }

    @Override
    public void write(int i) throws IOException {
        if(start!=null && counter<=start){
            counter++;
            return;
        }else{
            if(end == null || counter<=end){
                counter++;
                outputStream.write(i);
            }else{
                throw new WriteLimitReachedException();
            }
        }
    }
}

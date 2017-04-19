package uk.ac.ebi.eva.server.rest.another;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by jorizci on 19/09/16.
 */
public class PipeOutputStream extends OutputStream {
    Logger logger = LoggerFactory.getLogger(PipeOutputStream.class);

    private static final long DEFAULT_PIPE_TIMEOUT = 60000;
    private final byte[] buffer;
    private final int bufferSize;
    private final long pipeTimeout;
    private int startIndex;
    private int endIndex;
    private int readAvailability;
    private long totalWritten;

    private final Lock lock;
    private boolean writerClosed;

    public PipeOutputStream(int bufferSize){
        this(bufferSize, DEFAULT_PIPE_TIMEOUT);
    }

    public PipeOutputStream(int bufferSize, long pipeTimeout) {
        buffer = new byte[bufferSize];
        this.bufferSize = bufferSize;
        this.pipeTimeout = pipeTimeout;

        startIndex = 0;
        endIndex = 0;
        readAvailability = 0;
        totalWritten = 0;
        lock = new ReentrantLock();
        writerClosed = false;
    }

    public long getTotalWritten() {
        return totalWritten;
    }

    @Override
    public void write(int value) throws IOException {
        synchronized (lock) {
            while (!writerClosed && getWriteAvailability() < 1) {
                try {
                    lock.wait(pipeTimeout);
                } catch (InterruptedException e) {
                    throw new IOException("Interrupted in write operation, value '" + value + "'");
                }
            }

            if (writerClosed) {
                throw new IOException("Cannot write writerClosed stream " + this);
            }

            totalWritten++;
            readAvailability++;
            buffer[endIndex] = (byte) value;
            endIndex = modBufferSize(endIndex + 1);

            //Notify that if read operation is waiting it can resume reading.
            lock.notifyAll();
        }
    }

    private int modBufferSize(int value) {
        //we want to have positions bet
        return  value % (bufferSize);
    }

    private int getWriteAvailability() {
        return bufferSize-readAvailability;
    }

    private int getReadAvailability() {
        return readAvailability;
    }

    @Override
    public void close() throws IOException {
        synchronized (lock) {
            this.writerClosed = true;
            lock.notifyAll();
        }
    }

    public InputStream getInputStream() {
        if (writerClosed) {
            //Return an empty input stream.
            return new InputStream() {
                @Override
                public int read() throws IOException {
                    return -1;
                }
            };
        }

        return new InputStream() {

            public boolean readerClose;

            {
                this.readerClose = false;
            }

            @Override
            public int read() throws IOException {
                synchronized (lock) {
                    // Wait
                    while (!readerClose && !writerClosed && getReadAvailability() < 1 ) {
                        try {
                            lock.wait(pipeTimeout);
                        } catch (InterruptedException e) {
                            throw new IOException("Interrupted while reading " + this);
                        }
                    }
                    if (readerClose) {
                        throw new IOException("Input reader closed while reading.");
                    }
                    if (getReadAvailability() == 0) {
                        if (!writerClosed) {
                            throw new IllegalStateException("Buffer is empty and not writerClosed.");
                        }
                        return -1;
                    }

                    readAvailability--;
                    byte value = buffer[startIndex];
                    startIndex = modBufferSize(startIndex + 1);

                    lock.notifyAll();
                    return value;
                }
            }

            @Override
            public void close() throws IOException {
                synchronized (lock) {
                    readerClose = true;
                    lock.notifyAll();
                }
            }
        };
    }
}

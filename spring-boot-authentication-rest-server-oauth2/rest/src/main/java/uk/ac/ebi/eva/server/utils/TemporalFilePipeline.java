package uk.ac.ebi.eva.server.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;
import uk.ac.ebi.eva.server.utils.exceptions.TimeoutExpirationAtInitialization;

import java.io.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by jorizci on 15/09/16.
 */
public class TemporalFilePipeline {
    private static final String STREAM_PREFIX = "streaming-resource";
    private static final String STREAM_POSTFIX = ".tmp";
    private static final int DEFAULT_BUFFER_SIZE = 9; //9bytes;
    private static final long DEFAULT_TIMEOUT = 30 * 1000; //30 seconds;
    private static final Logger logger = LoggerFactory.getLogger(TemporalFilePipeline.class);

    private String prefix;
    private String sufix;

    private int bufferSize;
    private final long timeout;

    private boolean generationFinished;

    private final Lock lock;

    public TemporalFilePipeline() throws IOException {
        this(STREAM_PREFIX, STREAM_POSTFIX);
    }

    public TemporalFilePipeline(String prefix, String sufix) throws IOException {
        this.prefix = prefix;
        this.sufix = sufix;
        this.generationFinished = false;
        this.bufferSize = DEFAULT_BUFFER_SIZE;
        this.lock = new ReentrantLock();
        this.timeout = DEFAULT_TIMEOUT;
    }

    private static File createTempFile(String prefix, String sufix) throws IOException {
        File file = File.createTempFile(prefix, sufix);
        file.deleteOnExit();
        return file;
    }

    public void transfer(DataGenerator dataGenerator, OutputStream destinyOutputStream) throws IOException {
        File file = createTempFile(prefix, sufix);
        InputStream inputstream = new FileInputStream(file);
        OutputStream outputstream = new ControlledFileOutputStream(file);

        byte[] buffer = new byte[bufferSize];

        long totalBytes = 0;

        //Generate a new thread with the generator method
        Thread thread = spawnGenerateMethod(dataGenerator, outputstream);

        try {
            //Wait until generation thread has spawned some data
            int availableBytes = waitForWrite(inputstream);

            while (availableBytes > 0) {
                logger.debug("availablebytes: " + availableBytes + " " + bufferSize);
                int bytesToTransfer = Math.min(bufferSize, availableBytes);
                inputstream.read(buffer, 0, bytesToTransfer);
                destinyOutputStream.write(buffer, 0, bytesToTransfer);
                destinyOutputStream.flush();


                availableBytes = waitForWrite(inputstream);
            }
        } catch (IOException e) {
            thread.interrupt();
        }
    }

    private int waitForWrite(InputStream inputstream) throws IOException {
        synchronized (lock) {
            int availableBytes = inputstream.available();
            while (availableBytes < bufferSize && !isGenerationFinished()) {
                try {
                    lock.wait(timeout);
                    return availableBytes = inputstream.available();
                } catch (InterruptedException e) {
                    throw new TimeoutExpirationAtInitialization();
                }
            }
            return availableBytes;
        }
    }

    private Thread spawnGenerateMethod(DataGenerator dataGenerator, OutputStream outputstream) {
        Thread generateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.debug("Generating thread has started!!!");
                    dataGenerator.generate(outputstream);
                    logger.debug("Generating thread has ended!!!");
                    setGenerationFinished(true);
                } catch (InterruptedIOException e) {
                    //End thread
                    return;
                } catch (IOException e) {
                    //TODO signal discard everything
                }
            }
        });

        // Reset file generation finished and spawn generation process
        setGenerationFinished(false);
        generateThread.start();
        return generateThread;
    }

    public final int getBufferSize() {
        return bufferSize;
    }

    public final void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    private synchronized boolean isGenerationFinished() {
        return generationFinished;
    }

    private synchronized void setGenerationFinished(boolean generationFinished) {
        synchronized (lock) {
            this.generationFinished = generationFinished;
            lock.notify();
        }
    }

    private class ControlledFileOutputStream extends FileOutputStream {

        public ControlledFileOutputStream(File file) throws FileNotFoundException {
            super(file);
        }

        @Override
        public void write(int i) throws IOException {
            synchronized (lock) {
                super.write(i);
                lock.notify();
            }
        }

        @Override
        public void write(byte[] bytes) throws IOException {
            synchronized (lock) {
                super.write(bytes);
                lock.notify();
            }
        }

        @Override
        public void write(byte[] bytes, int i, int i1) throws IOException {
            synchronized (lock) {
                super.write(bytes, i, i1);
                lock.notify();
            }
        }
    }
}

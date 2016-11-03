package uk.ac.ebi.eva.exceptions;

/**
 * Created by jorizci on 03/11/16.
 */
public class VersionNotSupportedException extends Exception{

    private static final String VERSION_NOT_SUPPORTED = "Version not supported";

    public VersionNotSupportedException() {
        super(VERSION_NOT_SUPPORTED);
    }

    public VersionNotSupportedException(String s) {
        super(s);
    }

    public VersionNotSupportedException(String s, Throwable throwable) {
        super(s, throwable);
    }

}

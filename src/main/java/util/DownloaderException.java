package util;

public class DownloaderException extends Exception {

    public DownloaderException(String error) {
        super(error);
    }

    public DownloaderException(Exception ex) {
        super(ex);
    }
}

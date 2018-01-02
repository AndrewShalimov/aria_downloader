package util;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;

public class DownloaderTests {
    private ClassLoader classLoader;

    @Before
    public void init() {
        classLoader = getClass().getClassLoader();
    }

    @Test
    public void invalid_location_test() {
        String location = "https://111eusastagingvs.customerfocus.com/view.prod222/0.383671_124082!b=()!s=300.jpg?t=1512469268";
        File file = null;
        try {
            file = new Downloader().getFile(location);
        } catch (DownloaderException e) {
            assert e.getMessage().contains("Exception:");
        }
        assert file == null;

        location = "httpeees://111eusastagingvs.customerfocus.com/view.prod222/0.383671_124082!b=()!s=300.jpg?t=1512469268";
        file = null;
        try {
            file = new Downloader().getFile(location);
        } catch (DownloaderException e) {
            assert e.getMessage().contains("not complete");
        }
        assert file == null;

        location = "";
        file = null;
        try {
            file = new Downloader().getFile(location);
        } catch (DownloaderException e) {
            assert e.getMessage().contains("can't download");
        }
        assert file == null;

        location = "http://dfgsl,dfg;sld,fgsd;lfg,dlf";
        file = null;
        try {
            file = new Downloader().getFile(location);
        } catch (DownloaderException e) {
            assert e.getMessage().contains("not complete");
        }
        assert file == null;
    }


    @Test
    public void valid_location_test() throws Exception {
        File localFile = new File(classLoader.getResource("image.jpg").getFile());
        URL url = localFile.toURI().toURL();
        System.out.println(url.toString());
    }

}

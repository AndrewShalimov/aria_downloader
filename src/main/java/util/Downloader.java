package util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Downloader {

    private final static Logger logger = LoggerFactory.getLogger(Downloader.class);
    private final static String ariaFilename = "aria2c";
    private static boolean AVAILABLE;

    static {
        String result = "";
        try {
            result = run(Arrays.asList(ariaFilename));
            AVAILABLE = result.contains(ariaFilename);
        } catch (Throwable e) {
            logger.warn("Error first try. aria2c is not extracted.");
        }
        if (!AVAILABLE) {
            try {
                extractFromJar();
                result = run(Arrays.asList(ariaFilename));
                AVAILABLE = result.contains(ariaFilename);
            } catch (Throwable e) {
                logger.error(isEmpty(e.getMessage()) ? e.toString() : e.getMessage());
                AVAILABLE = false;
            }
        }
    }

    private static File extract(String srcPath, File dstFile) throws IOException {
        InputStream link = (Downloader.class.getResourceAsStream(srcPath));
        Files.copy(link, dstFile.getAbsoluteFile().toPath());
        return dstFile;
    }

    private static void extractFromJar() throws IOException {
        String jarPath = "/META-INF/aria_exec/";

        if (SystemUtils.IS_OS_WINDOWS) {
            File aria_exe = new File("aria2c.exe");
            if (!aria_exe.exists()) {
                extract(jarPath + "windows_64/aria2c.exe", aria_exe);
            }
        }

        if (SystemUtils.IS_OS_LINUX) {
            File aria_exe = new File("aria2с");
            if (!aria_exe.exists()) {
                extract(jarPath + "linux_64/aria2с", aria_exe);
            }
        }
    }

    public byte[] getBytes(String remoteLocation) throws DownloaderException {
        File file = getFile(remoteLocation);
        if (file == null) {
            return null;
        }
        if (file.length() == 0) {
            return null;
        }
        byte[] data;
        Path path = file.toPath();
        try {
            data = Files.readAllBytes(path);
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
        FileUtils.deleteQuietly(file);
        return data;
    }

    public File getFile(String remoteLocation) throws DownloaderException {
        if (isEmpty(remoteLocation)) {
            String error = "remoteLocation is empty, can't download '" + remoteLocation + "'";
            logger.debug(error);
            throw new DownloaderException(error);
        }
        if (!AVAILABLE) {
            String error = "aria2c is not available, can't download '" + remoteLocation + "'";
            logger.debug(error);
            throw new DownloaderException(error);
        }
        File dstFile = null;
        try {
            dstFile = File.createTempFile("temp_aria2c_", ".temp");
        } catch (IOException e) {
            String error = isEmpty(e.getMessage()) ? e.toString() : e.getMessage();
            logger.debug(error);
            throw new DownloaderException(error);
        }

        String resultString = "";
        try {
            List<String> args = new ArrayList<String>();
            args.add(ariaFilename);
            args.add(remoteLocation);
            args.add("-o");
            args.add(dstFile.getName());
            args.add("-d");
            args.add(dstFile.getParent());
            args.add("--allow-overwrite=true");
            resultString = run(args);
        } catch (Exception e) {
            e.printStackTrace();
            String error = "aria2c is not available, can't download '" + remoteLocation + "'";
            logger.debug(error);
            throw new DownloaderException(error);
        }
        if (resultString.contains("Exception:") || resultString.contains("error occurred")) {
            logger.debug(resultString);
            throw new DownloaderException(resultString);
        }
        return dstFile;
    }

    static String run(List<String> cmd) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(cmd);
        StringBuilder sb = new StringBuilder();
        builder.redirectErrorStream(true);
        Process p = builder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = null;
        while ((line = reader.readLine()) != null) {
            if (sb != null) {
                sb.append("\n");
                sb.append(line);
            }
        }
        p.waitFor();
        return sb.toString();
    }

    public static boolean isEmpty(String string) {
        return null == string || string.length() == 0;
    }
}

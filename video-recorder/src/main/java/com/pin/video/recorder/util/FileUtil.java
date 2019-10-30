package com.pin.video.recorder.util;

import java.io.File;

public class FileUtil {

    public static boolean deleteFile(String path) {
        boolean result = false;
        File file = new File(path);
        if (file.exists()) {
            result = file.delete();
        }
        return result;
    }
}

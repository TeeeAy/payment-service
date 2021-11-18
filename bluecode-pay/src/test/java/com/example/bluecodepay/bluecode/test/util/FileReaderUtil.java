package com.example.bluecodepay.bluecode.test.util;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;

public final class FileReaderUtil {

    public static String readFromFile(String path) throws IOException {
        Resource resource = new ClassPathResource(path);
        return FileUtils.readFileToString(new File(String.valueOf(resource.getFile())), "UTF-8");
    }
}


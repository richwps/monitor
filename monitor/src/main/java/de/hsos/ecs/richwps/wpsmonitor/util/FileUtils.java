/*
 * Copyright 2014 Florian Vogelpohl <floriantobias@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.hsos.ecs.richwps.wpsmonitor.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Utils to read files and check some circumstances.
 * 
 * @author Florian Vogelpohl <floriantobias@gmail.com>
 */
public class FileUtils {

    public static String loadFile(final Path filePath) throws IOException {
        if (!isExists(filePath) || !isFile(filePath)) {
            throw new IllegalArgumentException("The given path doesn't point to an existing file");
        }

        StringBuilder strBuilder = new StringBuilder();
        try (Scanner scanner = new Scanner(filePath, StandardCharsets.UTF_8.name())) {
            while (scanner.hasNextLine()) {
                strBuilder.append(scanner.nextLine());
                strBuilder.append('\n');
            }
        }

        return strBuilder.toString();
    }

    public static Set<Path> scanDirectoryForFiles(final Path path) {
        if (!isExists(path) || !isDirectory(path)) {
            throw new IllegalArgumentException("The given path doesn't point to an existing directory.");
        }

        Set<Path> files = new HashSet<>();

        for (File fileEntry : path.toFile().listFiles()) {
            if (fileEntry.exists() && fileEntry.isFile()) {
                files.add(fileEntry.toPath());
            }
        }

        return files;
    }

    public static Boolean isExists(final Path path) {
        return path.toFile().exists();
    }

    public static Boolean isFile(final Path path) {
        return path.toFile().isFile();
    }

    public static Boolean isDirectory(final Path path) {
        return path.toFile().isDirectory();
    }

    private FileUtils() {
        
    }
}

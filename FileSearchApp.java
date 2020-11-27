package com.example.filesearch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.SocketOption;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.spec.RSAOtherPrimeInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileSearchApp {
    String path;
    String regex;
    String zipFileName;
    Pattern pattern;
    List<File> zipFiles = new ArrayList<File>();


    public static void main(String[] args) {
        FileSearchApp app = new FileSearchApp();
        switch (Math.min(args.length, 3)) {
            case 0:
                System.out.println("USAGE:FileSearchApp path [regex] [zipfile]");
                return;
            case 3:
                app.setZipFileName(args[2]);
            case 2:
                app.setRegex(args[1]);
            case 1:
                app.setPath(args[0]);

        }
        try {
            app.walkDirectoryjava8(app.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPath() {
        return path;
    }

    public String getRegex() {
        return regex;
    }

    public String getZipFileName() {
        return zipFileName;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setRegex(String regex) {
        this.regex = regex;
        this.pattern = pattern.compile(regex);
    }

    public void setZipFileName(String zipFileName) {
        this.zipFileName = zipFileName;
    }

    public void walkDirectoryjava8(String path) throws IOException {
        Files.walk(Paths.get(path)).forEach(f -> processFile(f.toFile()));
        ZipFiles();
        //getting the files of a directory
    }

    private void processFile(File file) {
        try {
            if (searchFile(file)) {
                addFileTozip(file);
            }
        } catch (IOException | UncheckedIOException e) {
            System.out.println("Error processing files: " + file + ": " + e);
        }
        System.out.println("process File:" + file);
    }

    private void walkDirectory(String path) {
        System.out.println("walkDirectory" + path);
        try {
            searchFile(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        addFileTozip(null);

    }

    private void addFileTozip(File file) {
        if (getZipFileName() != null) {
            zipFiles.add(file);
        }
        //System.out.println("SearchFile:"+file);
    }

    private boolean searchFile(File file) throws IOException {
        return Files.lines(file.toPath(), StandardCharsets.UTF_8)
                .anyMatch(t -> searchText(t));
    }

    private boolean searchText(String text) {
        return (this.getRegex() == null) ? true : this.pattern.matcher(text).matches();
    }

    public String getRelativeFilename(File file,File baseDir){
        String fileName = file.getAbsolutePath().substring(
                baseDir.getAbsolutePath().length()
        );
        fileName = fileName.replace('\\','/');
        while (fileName.startsWith("/")){
            fileName = fileName.substring(1);
        }
        return fileName;
    }

    public void ZipFiles() throws IOException{
        try(ZipOutputStream out =
                new ZipOutputStream(new FileOutputStream(getZipFileName()))){
            File baseDir = new File(getPath());

            for (File file : zipFiles){
                String fileName = getRelativeFilename(file,baseDir);

                ZipEntry zipEntry = new ZipEntry(fileName);
                zipEntry.setTime(file.lastModified());
                out.putNextEntry(zipEntry);

                Files.copy(file.toPath(),out);
                out.closeEntry();
            }
        }
    }


}

package com.carmarket.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;

import static com.carmarket.utils.Directory.ARTICLES_DIRECTORY;

public class FileWriterReader {

    private final String fileName = "Article_" + UUID.randomUUID() + ".txt";

    public String getFileName() {
        return fileName;
    }

    public void saveNewFile(String text) {
        try {
            FileWriter myWriter = new FileWriter(ARTICLES_DIRECTORY + this.fileName);
            myWriter.write(text);
            myWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String readArticleFile(String articleName) {
        String article = "";
        try (Scanner myReader = new Scanner(new File(ARTICLES_DIRECTORY + articleName))) {
            while (myReader.hasNextLine()) {
                article = myReader.nextLine();
            }
            return article;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}


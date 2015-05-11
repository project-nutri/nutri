package com.nutri.preparation.utils;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 29.09.14
 * Time: 12:10
 * To change this template use File | Settings | File Templates.
 */
public final class FileUtils {
    private  FileUtils(){}

    public static StringBuilder readFileToString(String filePath) {
        StringBuilder fileString = new StringBuilder();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
            //Cp1251
            Reader reader = new InputStreamReader(inputStream, "Cp1251");
            int data = reader.read();
            while (data != -1) {
                char theChar = (char) data;
                fileString.append(theChar);
                data = reader.read();
            }

            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return fileString;
    }
}

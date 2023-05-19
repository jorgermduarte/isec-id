package pt.jorgeduarte.domain.services;

import java.io.*;

public class XQueryFileReaderService {
    public static String getXQueryFromFile(String filePath) {
        StringBuilder xQuery = new StringBuilder();
        try {
            InputStream in = XQueryFileReaderService.class.getResourceAsStream(filePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                xQuery.append(line).append("\n");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return xQuery.toString();
    }
}
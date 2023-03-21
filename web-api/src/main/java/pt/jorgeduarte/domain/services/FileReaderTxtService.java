package pt.jorgeduarte.domain.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
@Service
public class FileReaderTxtService {
    public static String read(String name) throws FileNotFoundException {
        String[] fileParts = name.split("\\.");
        if (fileParts.length != 2) {
            throw new IllegalArgumentException("File name is not valid");
        }
        String fileType = fileParts[1];

        String content = "";
        if (fileType.equalsIgnoreCase("txt")) {
            content = readFileByLines(name).toString();
        }
        return content;
    }

    public static List<String> readFileByLines(String name) throws FileNotFoundException {
        var lines = new ArrayList<String>();

        try {
            ClassLoader classLoader = FileReaderTxtService.class.getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream("files/" + name);

            Scanner scanner = new Scanner(inputStream);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                lines.add(line);
            }
            scanner.close();

        } catch (Exception e){
            throw new FileNotFoundException("File not found: " + name);
        }

        return lines;
    }

}

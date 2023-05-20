package pt.jorgeduarte.domain.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@AllArgsConstructor
@Service
public class FileReaderTxtService {

    private static final String OUTPUT_DIR_NAME = "output";
    private static final Path OUTPUT_DIR = Paths.get(OUTPUT_DIR_NAME);

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

    public void saveTextFile(String fileName, String content) {
        Path filePath = OUTPUT_DIR.resolve(fileName);

        File outputDir = new File(OUTPUT_DIR_NAME);
        if (!outputDir.exists()) {
            outputDir.mkdir();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile()))) {
            writer.write(content);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to file " + fileName, e);
        }
    }
}

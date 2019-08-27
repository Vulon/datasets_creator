package Utils;

import database.Entity.DetectedImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CSVFileTool {



    public static void writeToCSVFile(List<DetectedImage> detectedImages, File outputFile) throws FileNotFoundException {
        ArrayList<String[]> strings = new ArrayList<>(detectedImages.size() + 1);
        strings.add(new String[]{
                "Path", "Object_x1", "Object_y1", "Object_x2", "Object_y2"
        });
        for(DetectedImage image : detectedImages){
            String[] string = {image.getPath(), Integer.toString(image.getObject_x1()),
                    Integer.toString(image.getObject_y1()), Integer.toString(image.getObject_x2())
            , Integer.toString(image.getObject_y2())};
            strings.add(string);
        }
        try (PrintWriter pw = new PrintWriter(outputFile)) {
            strings.stream()
                    .map(CSVFileTool::convertToCSV)
                    .forEach(pw::println);
        }
    }

    private static String convertToCSV(String[] data) {
        return Stream.of(data)
                .map(CSVFileTool::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    private static String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
}

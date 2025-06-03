package com.example.bpa;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ConvertJsonToExcel {

    @Test
    void convertJsonToExcel() {
        try
        {
            final Path path = Path.of("C:\\Users\\KHON Piseth\\Documents\\errors_report.json");
            // Sample JSON input (replace with your JSON file path or string)
            String jsonString = "[{\"name\":\"John\",\"age\":30,\"city\":\"New York\"}," +
                    "{\"name\":\"Alice\",\"age\":25,\"city\":\"London\"}]";

            // Parse JSON
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(path.toFile());

            // Create Excel workbook and sheet
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Data");

            // Create header row
            if (jsonNode.isArray() && jsonNode.size() > 0)
            {
                JsonNode firstObject = jsonNode.get(0);
                Row headerRow = sheet.createRow(0);
                int cellIndex = 0;

                // Get headers from first JSON object
                Iterator<String> fieldNames = firstObject.fieldNames();
                while (fieldNames.hasNext())
                {
                    Cell cell = headerRow.createCell(cellIndex++);
                    cell.setCellValue(fieldNames.next());
                }

                // Fill data rows
                int rowIndex = 1;
                for (JsonNode node : jsonNode)
                {
                    Row row = sheet.createRow(rowIndex++);
                    cellIndex = 0;
                    for (String fieldName : getFieldNames(firstObject))
                    {
                        Cell cell = row.createCell(cellIndex++);
                        JsonNode value = node.get(fieldName);
                        if (value != null)
                        {
                            if (value.isTextual())
                            {
                                cell.setCellValue(value.asText());
                            } else if (value.isNumber())
                            {
                                cell.setCellValue(value.asDouble());
                            } else if (value.isBoolean())
                            {
                                cell.setCellValue(value.asBoolean());
                            } else
                            {
                                cell.setCellValue(value.toString());
                            }
                        }
                    }
                }
            }

            // Auto-size columns
            for (int i = 0; i < jsonNode.get(0).size(); i++)
            {
                sheet.autoSizeColumn(i);
            }

            // Write to Excel file
            try (FileOutputStream fileOut = new FileOutputStream("files_error.xlsx"))
            {
                workbook.write(fileOut);
            }

            // Close workbook
            workbook.close();

            System.out.println("Excel file created successfully!");

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // Helper method to get field names in order
    private static List<String> getFieldNames(JsonNode node) {
        List<String> fieldNames = new ArrayList<>();
        Iterator<String> iterator = node.fieldNames();
        while (iterator.hasNext())
        {
            fieldNames.add(iterator.next());
        }
        return fieldNames;
    }
}

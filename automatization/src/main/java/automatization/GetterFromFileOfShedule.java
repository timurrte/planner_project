package automatization;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class GetterFromFileOfShedule {
    private final String pathToFile;
    private final Map<String, List<String>> teachers;

    public GetterFromFileOfShedule(String pathToFile) {
        this.pathToFile = pathToFile;
        this.teachers = new HashMap<>();
    }

    public Map<String, List<String>> getData() throws IOException {
        try (FileInputStream fin = new FileInputStream(pathToFile);
             Workbook workbook = new XSSFWorkbook(fin)) {

            DataFormatter formatter = new DataFormatter();

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                if (sheet == null || sheet.getPhysicalNumberOfRows() == 0) continue;

                parseSheet(sheet, formatter);
            }
        }
        return teachers;
    }

    private void parseSheet(Sheet sheet, DataFormatter formatter) {
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) return;

        String currentDay = "";
        
        // Зчитуємо розклад занять починаючи з 4 рядку
        for (int rowIndex = 3; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) continue;

            // Отримуємо дату
            Cell dayCell = row.getCell(0);
            if (dayCell != null && !formatter.formatCellValue(dayCell).isEmpty()) {
                currentDay = formatter.formatCellValue(dayCell);
            }

            // Отримуємо час пари
            String time = formatter.formatCellValue(row.getCell(1));

            for (int colIndex = 2; colIndex < row.getLastCellNum(); colIndex++) {
                Cell cell = row.getCell(colIndex);
                if (cell == null) continue;

                String cellValue = formatter.formatCellValue(cell);
                if (cellValue.isEmpty()) continue;

                // Визначення чи пара Чисельник/Знаменник
                String partIndicator = getSplitCellIndicator(row, sheet, colIndex, formatter);

                // Визначаємо викладача
                String teacher = getTeacherName(headerRow, colIndex, formatter);
                if (teacher != null && !teacher.isEmpty()) {
                    String combinedValue = String.format("%s, %s, %s %s", currentDay, time, cellValue, partIndicator);
                    // Якщо ключ в ХешМапі відсутній то створюємо пустий ArrayList
                    // Додаємо до масиву дані про заняття з розкладу
                    teachers.computeIfAbsent(teacher, k -> new ArrayList<>()).add(combinedValue);
                }
            }
        }
    }
    
    /**
    * Return name of the teacher that is bound to this column.
    *
    * @param  headerRow рядок в файлі, який зберігає ім'я викладача
    * @param  columnIndex номер колонки
    * @return  ім'я викладача
    */
    private String getTeacherName(Row headerRow, int columnIndex, DataFormatter formatter) {
        // Зчитуємо 1 рядок, який зберігає ім'я викладача
    	Cell headerCell = headerRow.getCell(columnIndex);
        if (headerCell != null) {
            return formatter.formatCellValue(headerCell);
        }
        return "";
    }

    /**
    * Повертає рядок, який зберігає значення Чисельник/Знаменник
    *
    * @param  currentRow комірка в файлі
    * @return чисельник/знаменник
    */
    private String getSplitCellIndicator(Row currentRow) {

        int rowIndex = currentRow.getRowNum();

        if (rowIndex % 2 != 0) {
            return "Чисельник";
        } else {
            return "Знаменник";
        }
    }
}

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
    private final Map<String, List<Assignment>> groups;

    public GetterFromFileOfShedule(String pathToFile) {
        this.pathToFile = pathToFile;
        this.groups = new HashMap<>();
    }

    public Map<String, List<Assignment>> getData() throws IOException {
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
        Row headerRow = sheet.getRow(6);
        if (headerRow == null) return;

        String currentDay = "";
        
        // Зчитуємо розклад занять починаючи з 9 рядку
        for (int rowIndex = 8; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
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
                Numerator partIndicator = getSplitCellIndicator(row);

                // Визначаємо викладача
                String group = getGroup(headerRow, colIndex, formatter);
                if (group != null && !group.isEmpty()) {
                	Assignment assignment = new Assignment(currentDay, time, cellValue, partIndicator);
                    // Якщо ключ в ХешМапі відсутній то створюємо пустий ArrayList
                    // Додаємо до масиву дані про заняття з розкладу
                    teachers.computeIfAbsent(group, k -> new ArrayList<>()).add(assignment);
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
    private String getGroup(Row row, int columnIndex, DataFormatter formatter) {
        // Зчитуємо 1 рядок, який зберігає ім'я викладача
    	Cell headerCell = row.getCell(columnIndex);
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
    private Numerator getSplitCellIndicator(Row currentRow) {

        int rowIndex = currentRow.getRowNum();

        if (rowIndex % 2 != 0) {
            return Numerator.Chyselnik;
        } else {
            return Numerator.Znamennyk;
        }
    }
}

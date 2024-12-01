package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class Main {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/sks";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";
    private static final String[] DAYS = {"Понеділок", "Вівторок", "Середа", "Четвер", "П'ятниця"};
    private static final int LESSON_ROWS = 2; // Each lesson takes 2 rows
    private static final int LESSONS_PER_DAY = 7;
    private static final int ROWS_PER_DAY = LESSON_ROWS * LESSONS_PER_DAY;

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Query the database
            String query = "SELECT * FROM Schedule ORDER BY teacher_name, day, time_of_lesson";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // Organize data by teacher
            Map<String, Map<String, Map<Integer, String[]>>> scheduleData = new LinkedHashMap<>();
            while (resultSet.next()) {
                String teacher = resultSet.getString("teacher_name");
                String day = getFullDayName(resultSet.getString("day"));
                int lessonNumber = Integer.parseInt(resultSet.getString("time_of_lesson"));
                String subject = resultSet.getString("subject");
                String form = resultSet.getString("form_of_studying");
                String group = resultSet.getString("group_of_students");
                String classroom = resultSet.getString("classroom_number");
                String weekType = resultSet.getString("week_type");

                // Combine details into a single string
                String lessonInfo = subject + " (" + form + "), Room: " + classroom;

                // Aggregate data by teacher -> day -> lesson
                Map<Integer, String[]> lessonDataMap = scheduleData
                        .computeIfAbsent(teacher, k -> new LinkedHashMap<>())
                        .computeIfAbsent(day, k -> new HashMap<>());

                // Retrieve or initialize the String[] for the current lesson
                String[] weekData = lessonDataMap.computeIfAbsent(lessonNumber, k -> new String[2]);

                // Process group information for the appropriate week type
                if (weekType.equals("чисельник")) {
                    if (weekData[0] == null) {
                        weekData[0] = lessonInfo + ", Group: " + group;
                    } else {
                        weekData[0] = appendGroupInfo(weekData[0], lessonInfo, group);
                    }
                } else if (weekType.equals("знаменник")) {
                    if (weekData[1] == null) {
                        weekData[1] = lessonInfo + ", Group: " + group;
                    } else {
                        weekData[1] = appendGroupInfo(weekData[1], lessonInfo, group);
                    }
                }
            }

            // Create Excel Workbook
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Teacher Schedule");

            CellStyle thickBorderStyle = workbook.createCellStyle();
            thickBorderStyle.setBorderTop(BorderStyle.THICK);
            thickBorderStyle.setBorderBottom(BorderStyle.THICK);
            thickBorderStyle.setAlignment(HorizontalAlignment.CENTER);
            thickBorderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            thickBorderStyle.setWrapText(true);

            CellStyle thinBorderStyle = workbook.createCellStyle();
            thinBorderStyle.setBorderTop(BorderStyle.THIN);
            thinBorderStyle.setBorderBottom(BorderStyle.THIN);
            thinBorderStyle.setAlignment(HorizontalAlignment.CENTER);
            thinBorderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            thinBorderStyle.setWrapText(true);


            // Create cell styles
            CellStyle boldCenterStyle = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            boldCenterStyle.setFont(boldFont);
            boldCenterStyle.setAlignment(HorizontalAlignment.CENTER);
            boldCenterStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle rotatedBoldStyle = workbook.createCellStyle();
            rotatedBoldStyle.cloneStyleFrom(boldCenterStyle);
            rotatedBoldStyle.setRotation((short) 90); // Rotate text vertically

            CellStyle centerStyle = workbook.createCellStyle();
            centerStyle.setAlignment(HorizontalAlignment.CENTER);
            centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            centerStyle.setWrapText(true);

            CellStyle boldBorderStyle = workbook.createCellStyle();
            boldBorderStyle.cloneStyleFrom(centerStyle);
            boldBorderStyle.setBorderTop(BorderStyle.THICK);
            boldBorderStyle.setBorderBottom(BorderStyle.THICK);
            boldBorderStyle.setBorderLeft(BorderStyle.THICK);
            boldBorderStyle.setBorderRight(BorderStyle.THICK);

            // Create header row with teacher names
            Row headerRow = sheet.createRow(0);
            Cell dayHeaderCell = headerRow.createCell(0);
            dayHeaderCell.setCellValue("Day");
            dayHeaderCell.setCellStyle(rotatedBoldStyle);

            Cell lessonHeaderCell = headerRow.createCell(1);
            lessonHeaderCell.setCellValue("Lesson");
            lessonHeaderCell.setCellStyle(rotatedBoldStyle);

            int colIndex = 2;
            for (String teacher : scheduleData.keySet()) {
                Cell teacherCell = headerRow.createCell(colIndex++);
                teacherCell.setCellValue(teacher);
                teacherCell.setCellStyle(boldCenterStyle);
            }


            // Populate the schedule
            int currentRow = 1;
            for (String day : DAYS) {
                // Merge cells for the day label
                sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow + ROWS_PER_DAY - 1, 0, 0));
                Row dayRow = sheet.getRow(currentRow);
                if (dayRow == null) {
                    dayRow = sheet.createRow(currentRow);
                }

                Cell dayCell = dayRow.createCell(0);
                dayCell.setCellValue(day);
                dayCell.setCellStyle(rotatedBoldStyle); // Bold for day names with border

                for (int lesson = 1; lesson <= LESSONS_PER_DAY; lesson++) {
                    int lessonStartRow = currentRow + (lesson - 1) * LESSON_ROWS;

                    // Merge cells for the lesson number (numerator and denominator rows)
                    sheet.addMergedRegion(new CellRangeAddress(lessonStartRow, lessonStartRow + 1, 1, 1));

                    // Create or retrieve the row for the lesson start
                    Row lessonRow = sheet.getRow(lessonStartRow);
                    if (lessonRow == null) {
                        lessonRow = sheet.createRow(lessonStartRow);
                    }

                    // Set lesson number in the merged cell
                    Cell lessonCell = lessonRow.createCell(1);
                    lessonCell.setCellValue(lesson);
                    lessonCell.setCellStyle(centerStyle); // Centered style for lesson numbers

                    // Process teacher schedule data for this lesson
                    int colIndex2 = 2; // Start from the third column
                    for (String teacher : scheduleData.keySet()) {
                        Map<String, Map<Integer, String[]>> daysForTeacher = scheduleData.get(teacher);
                        Map<Integer, String[]> lessonsForDay = daysForTeacher.getOrDefault(day, Collections.emptyMap());

                        String[] data = lessonsForDay.getOrDefault(lesson, new String[2]);

                        // Populate numerator (чисельник)
                        if (data[0] != null) {
                            Row numeratorRow = sheet.getRow(lessonStartRow);
                            if (numeratorRow == null) numeratorRow = sheet.createRow(lessonStartRow);
                            Cell numeratorCell = numeratorRow.createCell(colIndex2);
                            numeratorCell.setCellValue(data[0]);
                            numeratorCell.setCellStyle(centerStyle);
                        }

                        // Populate denominator (знаменник)
                        if (data[1] != null) {
                            Row denominatorRow = sheet.getRow(lessonStartRow + 1);
                            if (denominatorRow == null) denominatorRow = sheet.createRow(lessonStartRow + 1);
                            Cell denominatorCell = denominatorRow.createCell(colIndex2);
                            denominatorCell.setCellValue(data[1]);
                            denominatorCell.setCellStyle(centerStyle);
                        }
                        colIndex2++;
                    }
                }
                currentRow += ROWS_PER_DAY; // Move to the next day
            }

            // Auto-size columns for readability
            for (int i = 0; i <= headerRow.getLastCellNum(); i++) {
                sheet.autoSizeColumn(i);
            }

            setUniformRowHeight(sheet, 30.0f);

            // Write to Excel file
            try (FileOutputStream fos = new FileOutputStream("TeacherSchedule.xlsx")) {
                workbook.write(fos);
            }

            System.out.println("Excel file 'TeacherSchedule.xlsx' created successfully!");

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private static String appendGroupInfo(String existingData, String lessonKey, String groupInfo) {
        if (existingData.contains(lessonKey)) {
            int groupIndex = existingData.indexOf("Group: ") + 7;
            String existingGroups = existingData.substring(groupIndex);
            if (!existingGroups.contains(groupInfo)) {
                return existingData + ", " + groupInfo;
            }
            return existingData;
        }
        return existingData + "\n" + lessonKey + ", Group: " + groupInfo;
    }

    private static String getFullDayName(String shortName) {
        return switch (shortName) {
            case "M" -> "Понеділок";
            case "T" -> "Вівторок";
            case "W" -> "Середа";
            case "S" -> "Четвер";
            case "F" -> "П'ятниця";
            default -> "Unknown";
        };
    }

    private static void setUniformRowHeight(Sheet sheet, float rowHeightInPoints) {
        for (Row row : sheet) {
            row.setHeightInPoints(rowHeightInPoints);
        }
    }
}

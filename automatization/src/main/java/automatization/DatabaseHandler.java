package automatization;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DatabaseHandler {
    private final String url;
    private final String user;
    private final String password;

    public DatabaseHandler(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public void insertTimetableIntoDatabase(Map<String, List<Assignment>> timetable) throws SQLException {
        String insertQuery = "INSERT INTO assignments (day, time_of_lesson, subject, teacher, classroom, group_of_students, week_type) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = con.prepareStatement(insertQuery)) {

            for (List<Assignment> assignments : timetable.values()) {
                for (Assignment assignment : assignments) {
                    pstmt.setString(1, assignment.getDay());
                    pstmt.setString(2, assignment.getTime_of_lesson());
                    pstmt.setString(3, assignment.getSubject());
                    pstmt.setString(4, assignment.getTeacher());
                    pstmt.setString(5, assignment.getClassroom());
                    pstmt.setString(6, assignment.getGroup_of_students());
                    pstmt.setString(7, assignment.getWeek_type_of_lesson().toString());

                    pstmt.executeUpdate();
                }
            }

            System.out.println("Данные успешно записаны в базу данных!");
        }
    }
}

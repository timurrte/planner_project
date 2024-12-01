package automatization;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import automatization.enums.FormOfStudying;

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
        String insertQuery = "INSERT INTO Schedule (teacher_name, subject, day, group_of_students,"
        		+ " time_of_lesson, classroom_number,  week_type, form_of_studying) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = con.prepareStatement(insertQuery)) {

            for (List<Assignment> assignments : timetable.values()) {
                for (Assignment assignment : assignments) {
                	Numerator weekType = assignment.getWeek_type_of_lesson();
                	FormOfStudying formOfStudying = assignment.getForm_of_studying();
                	pstmt.setString(1, assignment.getTeacher());
                	pstmt.setString(2, assignment.getSubject());
                    pstmt.setString(3, assignment.getDay().toString());
                    pstmt.setString(4, assignment.getGroup());
                    pstmt.setString(5, assignment.getTime_of_lesson());
                    pstmt.setString(6, assignment.getClassroom());
                    pstmt.setString(7, weekType == Numerator.Znamennyk ? "знаменник"
                    		: weekType == Numerator.Chyselnik ? "чисельник" : "обидва");
                    pstmt.setString(8, formOfStudying == FormOfStudying.ONLINE ? "online" : "offline");
                    pstmt.executeUpdate();
                }
            }

            System.out.println("Дані було успішно записано до бази даних!");
        }
    }
}

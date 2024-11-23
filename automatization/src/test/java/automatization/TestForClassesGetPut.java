package automatization;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.*;

public class TestForClassesGetPut {
  public static void main (String[] args) throws Exception {
	Dotenv env = Dotenv.load();
    String url = "jdbc:mysql://localhost/" + env.get("MYSQL_DATABASE");
    String user = env.get("MYSQL_USER");
    String password = env.get("MYSQL_PASSWORD");

    ArrayList<Assignment> data = new ArrayList<>();
    ScheduleReader getterFromExcel = new ScheduleReader(args[0]);

    Map<String, List<Assignment>> timetable = getterFromExcel.readSchedule();

    DatabaseHandler dbHandler = new DatabaseHandler(url, user, password);
    dbHandler.insertTimetableIntoDatabase(timetable);

    for (String key : timetable.keySet()) {
    	for (Assignment val : timetable.get(key)) {
    		System.out.println(val);
    	}
    }
    
//    Connection con = DriverManager.getConnection("jdbc:mysql://localhost/" + env.get("MYSQL_DATABASE"), env.get("MYSQL_USER"), env.get("MYSQL_PASSWORD"));
//    Statement st = con.createStatement();
//    st.executeUpdate("CREATE TABLE IF NOT EXISTS Testing (some_text VARCHAR(100))");
//    
//    for (String key : timetable.keySet() ) {
//    	String query = "INSERT INTO Testing (some_text) VALUES (?)";
//    	for (Assignment val : timetable.get(key)) {
//        	PreparedStatement statement = con.prepareStatement(query);
//        	String subject = val.getSubject();
//        	statement.setString(1, subject);
//        	statement.execute();
//        	statement.close();
//    	}
//    }
//	st.close();
//    con.close();
  }
}
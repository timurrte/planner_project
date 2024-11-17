package automatization;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.*;

public class TestForClassesGetPut {
  public static void main (String[] args) throws Exception {
	Dotenv env = Dotenv.load();
    ArrayList<String> data = new ArrayList<>();
    GetterFromFileOfShedule getterFromExcel = new GetterFromFileOfShedule(args[0]);
    Map<String, List<String>> timetable = getterFromExcel.getData();
    Connection con = DriverManager.getConnection("jdbc:mysql://localhost/" + env.get("MYSQL_DATABASE"), env.get("MYSQL_USER"), env.get("MYSQL_PASSWORD"));
    Statement st = con.createStatement();
    st.executeUpdate("CREATE TABLE IF NOT EXISTS Testing (some_text VARCHAR(100))");
	st.close();
    for(int i = 0; i < data.size(); i++) {
    	String query = "INSERT INTO Testing (some_text) VALUES (?)";
    	PreparedStatement statement = con.prepareStatement(query);
    	statement.setString(1, timetable.get(statement) data.get(i));
    	statement.execute();
    	statement.close();
    }
    
    for (String key : timetable.keySet() ) {
    	System.out.println(key + timetable.get(key));
    }
    con.close();
  }
}
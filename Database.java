/**
 * 
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 * @author navya
 *
 */
public class Database 
{
	public static Connection getConnection(String database) throws SQLException
	{ //String url="jdbc:mysql://localhost:3306/DBMS_CS631"+database+"?autoReconnect=true&useSSL=false";
		String url= "jdbc:mysql://127.0.0.1:3306/"+database+"?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=GMT";
		String userId="root";
		String password="";
		Connection connection;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection=DriverManager.getConnection(url,userId,password);
			System.out.println("Connection to Database is succesfull");
			return connection;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
		
	}
}

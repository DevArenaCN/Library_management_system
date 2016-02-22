import java.sql.*;
import java.io.*;
import java.util.*;
public class login {

  static final String jdbcURL = "jdbc:oracle:thin:@ora.csc.ncsu.edu:1521:orcl";
  
public static String login(int[] Para)
{
	Scanner typein = new Scanner(System.in); 
	int userType = 0;
	boolean b = true;
	String userName = null;
	String passWord = null;
	String userID = null;
	System.out.println("Please input user type, 1 for Student, 2 for Faculty, press enter to confirm: \n");
	while(b)
	{
		try
		{
			userType = typein.nextInt();
			b=false;
		}

		catch(Exception e ){
			System.out.println("Wrong input, please input the choice again:");
			typein.nextLine();
		}

	}

	Boolean loginStat = true;
	while(loginStat)
	{
    
		System.out.println("Please input your username:");
		userName = typein.next();
		System.out.println("Please input your password:");
		passWord =typein.next();
     
		try
		{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			String user = "ychen71";  // For example, "jsmith"
			String passwd = "200099159";  // Your 9 digit student ID number
			Statement loginSearch = null;
			ResultSet rs = null;
			String loginSearchQuery = "SELECT * FROM sAccount WHERE sAccount.userid =\'"+userName+"\'";
			String loginSearchQuery2 = "SELECT * FROM fAccount WHERE fAccount.userid =\'"+userName+"\'";
			//System.out.println(loginSearchQuery);
			Connection conn = null;
			String usernameDB = null;
			String passwordDB = null;
			if(userType == 1)
			{
				try 
				{

					conn = DriverManager.getConnection(jdbcURL, user, passwd);
					loginSearch = conn.createStatement();
					rs = loginSearch.executeQuery(loginSearchQuery);
					while(rs.next())
					{
						usernameDB = rs.getString("userid");
						passwordDB = rs.getString("password");
						userID = rs.getString("sid");
						Para[0] = rs.getInt("otmark");
						//System.out.println("usernameDB :"+usernameDB +"\npasswordDB :"+passwordDB+"\nstudentID :"+studentId +"\n");
						//System.out.println("input username is:"+userName);
						//System.out.println("input password is:"+passWord);
					}
				}
				
				finally 
				{
					close(rs);
					close(loginSearch);
					close(conn);
				}
			}
			
			else if(userType ==2)
			{
				try 
				{
					conn = DriverManager.getConnection(jdbcURL, user, passwd);
					loginSearch = conn.createStatement();
					rs = loginSearch.executeQuery(loginSearchQuery2);
					while(rs.next())
					{
						usernameDB = rs.getString("userid");
						passwordDB = rs.getString("password");
						userID = rs.getString("fid");
						Para[0] = rs.getInt("otmark");
						//System.out.println("usernameDB :"+usernameDB +"\npasswordDB :"+passwordDB+"\nFacultyID :"+facultyId +"\n");
						//System.out.println("input username is:"+userName);
						//System.out.println("input password is:"+passWord);
					}
				}
				finally
				{
					close(rs);
					close(loginSearch);
					close(conn);
				}
			}
			
			if (passWord.equals(passwordDB))
            {
				System.out.println("Login Success!");
				loginStat = false;
            }
            else
            {
            	System.out.println("Login failed!");
            	loginStat = false;
            }
		}
		catch(Throwable oops)
		{
			oops.printStackTrace();
		}
	}

	return userID;
}

static void close(Connection conn) 
{
	if(conn != null) 
	{
		try { conn.close(); } catch(Throwable whatever) {}
	}
}

static void close(Statement st) 
{
	if(st != null) 
	{
		try { st.close(); } catch(Throwable whatever) {}
	}
}

static void close(ResultSet rs)
{
	if(rs != null) 
	{
		try { rs.close(); } catch(Throwable whatever) {}
	}
}

} 



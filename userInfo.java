import java.sql.*;
import java.io.*;
import java.util.Scanner;

public class userInfo {
	
	static final String jdbcURL = "jdbc:oracle:thin:@ora.csc.ncsu.edu:1521:orcl";

    public static void Profile(String UserID) 
    {
        Scanner menuSelect = new Scanner(System.in);
        Boolean b= true;

        try 
        {

            // Load the driver. This creates an instance of the driver
        	// and calls the registerDriver method to make Oracle Thin
        	// driver available to clients.

        	Class.forName("oracle.jdbc.driver.OracleDriver");

        	String user = "ychen71";    // For example, "jsmith"
        	String passwd = "200099159";    // Your 9 digit student ID number
        	Statement searchUser = null;
        	String userID = UserID;
        	ResultSet rs = null;
        	String searchUserQuery = "SELECT * FROM Student WHERE Student.sid =\'"+userID+"\'";
        	Connection conn = null; 
        	Integer menuChose = 0;
        	Integer menu_2chose = 0;
        
            try 
            {

            	// Get a connection from the first driver in the
            	// DriverManager list that recognizes the URL jdbcURL

            	conn = DriverManager.getConnection(jdbcURL, user, passwd);

            	//Display User info

            	searchUser = conn.createStatement();
            	rs = searchUser.executeQuery(searchUserQuery);
            	while(rs.next())
            	{
            		String sid = rs.getString("sid");
            		String sname = rs.getString ("sname");
            		Integer phoneNum = rs.getInt("phone_num");
            		Integer altPhone = rs.getInt("alt_phone");
            		String homeAddr = rs.getString("home_addr");
            		Date orclDate = rs.getDate("dob");
            		String sex = rs.getString("sex");
            		String snation = rs.getString("snation");
            		String stuClassfi = rs.getString("stu_classification");
            		String sdegree = rs.getString("sdegree");
            		String stuCat = rs.getString("stu_category");
            		String sdepart = rs.getString("sdepart");
            		System.out.println("SID:"+sid+"\n"+"Student Name:"+sname+"\n"+"Phone Number:"+phoneNum+"\n"+"Alternate Phone Number:"+altPhone+"\n"+"Home Address"+homeAddr+"\n"+"Date of Birth:"+orclDate+"\n"+"Sex:"+sex+"\n"+"Student Nationality:"+snation+"\n"+"Student Classification:"+stuClassfi+"\n"+"Student Degree:"+sdegree+"\n"+"Student Catagory:"+stuCat+"\n"+"Student Department:"+sdepart); 
            	}
            	System.out.println("please choose:\n1.modify user info\n2.back");
            	while(b)
            	{
            		menuChose = menuSelect.nextInt();
            		b=false;
            	}
            	if(menuChose == 1)
            	{
                
            		boolean b1 = true;
            		while(b1)
            		{
            			System.out.println("Please choose which one you want to modify:\n1.Change Name\n2.Change Phone Number\n3.Chaneg Alternate Phone Number\n4.Change Home Address\n5.Change Date of Birth\n6.Change Sex\n7.Change Nationality\n8.Change Classification\n9.Change Student Degree\n10.Change Department\n0.Back");
            			menu_2chose = menuSelect.nextInt();
            			System.out.println(menu_2chose);

            			//change name mode
			            if(menu_2chose == 1)  
			            {
			                System.out.println("Change name mode");
			                menuSelect.nextLine();
			                System.out.println("Please input changed name: ");
			                String updateName = menuSelect.nextLine();
			                String updateQuery = "UPDATE Student SET sname= \'"+updateName+"\' WHERE sid=\'"+userID+"\'";
			                searchUser.executeUpdate(updateQuery);
			            }
	
			            //change phone number mode
			            else if(menu_2chose == 2)
			            {
			                System.out.println("Change phone number mode");
			                System.out.println("Please input changed phone number: ");
			                Integer updatePhone = menuSelect.nextInt();
			                String updateQuery = "UPDATE Student SET phone_num= \'"+updatePhone+"\' WHERE sid=\'"+userID+"\'";
			                searchUser.executeUpdate(updateQuery);
			
			            }
	
			            //change alt phone number
			            else if(menu_2chose == 3)
			            {
			                System.out.println("Change alt phone number mode");
			                System.out.println("Please input changed phone number: ");
			                Integer updateAltPhone = menuSelect.nextInt();
			                String updateQuery = "UPDATE Student SET alt_phone= \'"+updateAltPhone+"\' WHERE sid=\'"+userID+"\'";
			                searchUser.executeUpdate(updateQuery);
			
			            }
	
			            //change home address mode
			            else if(menu_2chose == 4){
			                System.out.println("Change home address mode");
			                menuSelect.nextLine();
			                System.out.println("Please input changed home address: ");
			                String updateAddr = menuSelect.nextLine();
			                String updateQuery = "UPDATE Student SET home_addr= \'"+updateAddr+"\' WHERE sid=\'"+userID+"\'";
			                searchUser.executeUpdate(updateQuery);
			
			            }
	
			            //change DOB mode
			            else if(menu_2chose == 5){
			                System.out.println("Change DOB mode");
			                System.out.println("Please input changed date of birth as follow format:DD/MM/YY, for example 02/FEB/1993 \n");
			                String updateDob = menuSelect.next();
			                String updateQuery = "UPDATE Student SET dob= \'"+updateDob+"\' WHERE sid=\'"+userID+"\'";
			                searchUser.executeUpdate(updateQuery);	
			            }
	
			            //change sex mode
			            else if(menu_2chose == 6){
			                System.out.println("Change sex mode");
			                System.out.println("Please input changed sex: ");
			                String updateSex = menuSelect.next();
			                String updateQuery = "UPDATE Student SET sex= \'"+updateSex+"\' WHERE sid=\'"+userID+"\'";
			                searchUser.executeUpdate(updateQuery);
			
			            }
	
			            //change nationality mode
			            else if(menu_2chose == 7){
			                System.out.println("change nationality mode");
			                System.out.println("Please input changed nationality: ");
			                String updateNation = menuSelect.next();
			                String updateQuery = "UPDATE Student SET snation= \'"+updateNation+"\' WHERE sid=\'"+userID+"\'";
			                searchUser.executeUpdate(updateQuery);
			            }
	
			            //change classificaion mode
			            else if(menu_2chose == 8){
			                System.out.println("change Classification mode");
			                System.out.println("Please input changed student classification: ");
			                String updateStuClass = menuSelect.next();
			                String updateQuery = "UPDATE Student SET stu_classification= \'"+updateStuClass+"\' WHERE sid=\'"+userID+"\'";
			                searchUser.executeUpdate(updateQuery);	
			            }
	            
			            //change degree mode
			            else if(menu_2chose == 9){
			                System.out.println("Change degree mode");
			                System.out.println("Please input changed student degree: ");
			                String updateDegree = menuSelect.next();
			                String updateQuery = "UPDATE Student SET sname= \'"+updateDegree+"\' WHERE sid=\'"+userID+"\'";
			                searchUser.executeUpdate(updateQuery);
			            }
	            
			            //change department mode
			            else if(menu_2chose == 10) {
			                System.out.println("Change department mode");
			                System.out.println("Please input changed department: ");
			                String updateDept = menuSelect.next();
			                String updateQuery = "UPDATE Student SET sdepart= \'"+updateDept+"\' WHERE sid=\'"+userID+"\'";
			                searchUser.executeUpdate(updateQuery);
			            }
	            
			            //back
			            else if(menu_2chose == 0)
			            {
			            	System.out.println("back");
			            	b1=false;
			            }
            		}
            	}
            }
            
            finally 
            {
                close(rs);
                close(searchUser);
                close(conn);
            }
        }
        
        catch(Throwable oops)
        {
             oops.printStackTrace();
        }

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

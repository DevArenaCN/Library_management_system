//check all waiting list table


import java.sql.*;
import java.text.DateFormat;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

public class showroomborrow {

    static final String jdbcURL 
	= "jdbc:oracle:thin:@ora.csc.ncsu.edu:1521:orcl";

    public static void showrm(String userId) {
        try {

            // Load the driver. This creates an instance of the driver
	    // and calls the registerDriver method to make Oracle Thin
	    // driver available to clients.

            Class.forName("oracle.jdbc.driver.OracleDriver");

	    String user = "ychen71";	// For example, "jsmith"
	    String passwd = "200099159";	// Your 9 digit student ID number


            Connection conn = null;
            Statement stmt = null;
            ResultSet rs = null;

            try {

		// Get a connection from the first driver in the
		// DriverManager list that recognizes the URL jdbcURL

		conn = DriverManager.getConnection(jdbcURL, user, passwd);

		// Create a statement object that will be sending your
		// SQL statements to the DBMS

		stmt = conn.createStatement();
        //================================================================student===========================================================================
		if(userId.charAt(0) == 'S')
		{
		String sid = userId;
        System.out.println("********************Borrowed StudyRoom************************");

        rs = stmt.executeQuery("select * from StudyRoom,SRBorrow where SRBorrow.fsid ='"+sid+"' and StudyRoom.srNum = SRBorrow.srNum ");

           
        while (rs.next()) 
        {

            String room_num = rs.getString("srNum");
            int roomfloor = rs.getInt("srFl");
            String peoplenum = rs.getString("srCapacity");
            String location = rs.getString("srLocation");
            Date s1=rs.getTimestamp("stime");
            Date s2=rs.getTimestamp("etime");
            int mark= rs.getInt("otmark");

            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm");
            Calendar checkin=Calendar.getInstance();
            checkin.setTime(s1);
            checkin.add(Calendar.HOUR_OF_DAY, 1);

            System.out.println("Student ID: "+sid);
            System.out.println("Room Reserved: "+room_num);
            System.out.println("Floor: "+roomfloor);
            System.out.println("Capacity: "+peoplenum);
            System.out.println("Location: "+location);
            System.out.println("Reserved Period: "+s1.toString()+"--"+s2.toString());
            if ( mark==1)
                System.out.println("***Failed to check in by"+df.format(checkin.getTime())+"***");
        } 
		}


                            

     //==================================================================faculty=========================================================================
		else
		{
		String fid = userId;
        System.out.println("********************Borrowed StudyRoom************************");

        rs = stmt.executeQuery("select * from StudyRoom,SRBorrow where SRBorrow.fsid ='"+fid+"' and StudyRoom.srNum = SRBorrow.srNum ");

           
        while (rs.next()) 
        {

            String room_num = rs.getString("srNum");
            int roomfloor = rs.getInt("srFl");
            String peoplenum = rs.getString("srCapacity");
            String location = rs.getString("srLocation");
            Date s1=rs.getTimestamp("stime");
            Date s2=rs.getTimestamp("etime");
            int mark= rs.getInt("otmark");

            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm");
            Calendar checkin=Calendar.getInstance();
            checkin.setTime(s1);
            checkin.add(Calendar.HOUR_OF_DAY, 1);

            System.out.println("Student ID: "+fid);
            System.out.println("Room Reserved: "+room_num);
            System.out.println("Floor: "+roomfloor);
            System.out.println("Capacity: "+peoplenum);
            System.out.println("Location: "+location);
            System.out.println("Reserved Period: "+s1.toString()+"--"+s2.toString());
            if ( mark==1)
                System.out.println("***Failed to check in by"+df.format(checkin.getTime())+"***");
        } 


        System.out.println("********************Borrowed Conference Room************************");

        rs = stmt.executeQuery("select * from ConferenceRoom,CRBorrow where CRBorrow.fid ='"+fid+"' and ConferenceRoom.crNum = CRBorrow.crNum ");

           
        while (rs.next()) 
        {

            String room_num = rs.getString("crNum");
            int roomfloor = rs.getInt("crFl");
            String peoplenum = rs.getString("crCapacity");
            Date s1=rs.getTimestamp("stime");
            Date s2=rs.getTimestamp("etime");
            int mark= rs.getInt("otmark");

            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm");
            Calendar checkin=Calendar.getInstance();
            checkin.setTime(s1);
            checkin.add(Calendar.HOUR_OF_DAY, 1);

            System.out.println("Faculty ID: "+fid);
            System.out.println("Room Reserved: "+room_num);
            System.out.println("Floor: "+roomfloor);
            System.out.println("Capacity: "+peoplenum);
            System.out.println("Reserved Period: "+s1.toString()+"--"+s2.toString());
            if ( mark==1)
                System.out.println("***Failed to check in by"+df.format(checkin.getTime())+"***");
        } 
		}





            } finally {
                close(rs);
                close(stmt);
                close(conn);
            }
        } catch(Throwable oops) {
            oops.printStackTrace();
        }
    }

    static void close(Connection conn) {
        if(conn != null) {
            try { conn.close(); } catch(Throwable whatever) {}
        }
    }

    static void close(Statement st) {
        if(st != null) {
            try { st.close(); } catch(Throwable whatever) {}
        }
    }

    static void close(ResultSet rs) {
        if(rs != null) {
            try { rs.close(); } catch(Throwable whatever) {}
        }
    }
}


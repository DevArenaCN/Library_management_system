/* UNDO: after read check_out_date and eturn date, 
if check_out_date is not between 9-12 Friday, can't borrow
if return date after Thuesday 18, can't borrow(date.getDay())

else people can borrow check whether the cam_id avaliable
if not avaliable 
    add to table waiting list
else 
    add to table borrow 
end
*/


import java.io.*;
import java.sql.*;
import java.util.Scanner;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.text.ParseException;

public class fcamera {

    static final String jdbcURL 
	= "jdbc:oracle:thin:@ora.csc.ncsu.edu:1521:orcl";

    public static void fcamer(String userId) throws ParseException {


    	SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy");
        SimpleDateFormat edf = new SimpleDateFormat("MM/dd/yy HH");
   	   	

        try {

            // Load the driver. This creates an instance of the driver
	    // and calls the registerDriver method to make Oracle Thin
	    // driver available to clients.

            Class.forName("oracle.jdbc.driver.OracleDriver");

	    	String user = "ychen71";	// For example, "jsmith"
	    	String passwd = "200099159";	// Your 9 digit student ID number
            //String available_cam = "CREATE VIEW CAMERA_AVAILABLE AS SELECT * FROM CAMERA WHERE NOT EXIST (SELECT fCam_Borrow.cam_id,sCam_Borrow.cam_id FROM fCam_Borrow, sCam_Borrow WHERE fCam_Borrow.cam_id = CAMERA.cam_id AND sCam_Borrow.cam_id = CAMERA.cam_id)";
            
            //get fid from main function
            String fid = userId;
            Connection conn = null;
            Statement searchcam = null;
            Statement stmt = null;
            ResultSet rs = null;
            ResultSet r = null;
            PreparedStatement pstmt = null;
            String camid = null;

            try {

		// Get a connection from the first driver in the
		// DriverManager list that recognizes the URL jdbcURL

		conn = DriverManager.getConnection(jdbcURL, user, passwd);

		// Create a statement object that will be sending your
		// SQL statements to the DBMS

		searchcam = conn.createStatement();
        stmt = conn.createStatement();
       // searchcam2 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
        
        //Update OTMark
        String selectot = "select * from fCamBorrow";
        rs = searchcam.executeQuery(selectot);
        while(rs.next())
        {
            Date checkotmark = rs.getTimestamp("eTime");
            String checkottemp = edf.format(checkotmark);
            Date checkot = edf.parse(checkottemp);


            Date nowt = new Date();
            String nowti = edf.format(nowt);
            Date nowtime = edf.parse(nowti);

            //String camid = rs.getString("cam_id");
            System.out.println(nowti);
            System.out.println(checkottemp);
            camid = rs.getString("cam_id");
            if(nowtime.after(checkot))
            {
                //camid = rs.getString("cam_id");
                String updateot = "update fCamBorrow set OTMARK=1 where cam_id = \'"+camid+"\'";
                stmt.executeUpdate(updateot);
                //Integer newot = 1; 
                //rs2.updateInt("OTMARK", 1);
                //rs2.updateRow();
                System.out.println("----------");
            }
            
            else
            {
                   String updateot = "update fCamBorrow set OTMARK=0 where cam_id = \'"+camid+"\'";
                stmt.executeUpdate(updateot);
                System.out.println("========");
            }
            
        }
        //String updateot = "update sCamBorrow set OtMark=1 where cam_id = \'"+camid+"\'";
        //System.out.println(updateot);
        //searchcam.executeQuery(updateot);

        //list all the avaliable camera
        String selectcamall = "select * from Camera";
        rs = searchcam.executeQuery(selectcamall);
        System.out.println("list all the camera");
        while(rs.next())
        {
            String cam_id = rs.getString("cam_id");
            String make = rs.getString("make");
            String model = rs.getString("model");
            String lensConfig = rs.getString("lensConfig");
            String memory = rs.getString("memory");
            String camLocation = rs.getString("camLocation");
            System.out.println(cam_id+" "+make+" "+model+" "+lensConfig+" "+memory+" "+camLocation);

        }

		System.out.println("Do you want to borrow a camera? yes/no");
        Scanner x = new Scanner(System.in);
        String y = x.nextLine();

        //judge id they want to borrow a camera
        if (y.equals("yes"))
    {   
        System.out.println("input the camare id you want to borrow ");
        Scanner idselect = new Scanner(System.in);
        String id = idselect.nextLine();

        System.out.println("Please input check out date: MM/dd/yy 08; For Example ");
        //you can just borrow for integer hours
        String check_out = x.nextLine();
        Date check_out_date = edf.parse(check_out);
        Integer weak1 = check_out_date.getDay();
        String check_out_d = edf.format(check_out_date);

        // judge if today is Friday. Only Friday people can borrow camera
        if (weak1==5)
        {
            System.out.println(id);
            String selectcam = "Select * from sCamBorrow,fCamBorrow Where sCamBorrow.cam_id= \'"+ id+"\' AND sCamBorrow.etime > to_date(\'"+ check_out_d +"\','mm/dd/yy HH24') AND fCamBorrow.cam_id= \'"+ id+"\' AND fCamBorrow.etime > to_date(\'"+ check_out_d +"\','mm/dd/yy HH24') ";            
            rs = searchcam.executeQuery(selectcam);
//if no record in sCamBorrow you can borrow it
            if(!rs.next())

            {
                
                Calendar checkin = Calendar.getInstance();
                checkin.setTime(check_out_date);
                checkin.add(Calendar.HOUR,154);
                Date check_in_date = checkin.getTime(); //without 6 pm
                //String check_in_date2 = edf.format(check_in_date);
                //Date check_in_date3 = edf.parse(check_in_date2);

                //Calendar checkin2 = Calendar.getInstance();
                //checkin2.setTime(check_in_date3);
                //checkin2.add(Calendar.HOUR_OF_DAY,18);// add 6 pm
                //Date check_in_date4 = checkin2.getTime();


                String check_in_d = edf.format(check_in_date);

                String borrowinsert = "INSERT INTO fCamBorrow VALUES( ?,?,to_date(\'"+ check_out_d +"\','mm/dd/yy HH24'),to_date(\'"+ check_in_d +"\','mm/dd/yy HH24'),0)";
                pstmt = conn.prepareStatement(borrowinsert);
                pstmt.setString(1,fid);
                pstmt.setString(2,id);
                pstmt.executeQuery();
                System.out.println("Success");
                
            }
            else
            {
                System.out.println("The camera have been borrowed, add in to waiting list");
                //the camera has been borrow, add waiting list
                //String addwl = "INSERT INTO sCamWL VALUES("+fid+" "+id+" "+",to_date('"+ check_out_d +"','dd/MON/yyyy'))";
                String addwl = "INSERT INTO fCamWL VALUES( ?,?,to_date('"+ check_out_d +"','mm/dd/yy HH24'))";
                pstmt = conn.prepareStatement(addwl);
                pstmt.setString(1,fid);
                pstmt.setString(2,id);
                pstmt.executeQuery();
                System.out.println("ADD INTO WAITING LIST");

            }



        }

        else 
        {
        System.out.println("It's not Friday, you can't borrow camera");
         //exit or back to the previous page
        }
    }
        else 
    {
        //exit or back to the previous page
        System.out.println("Bye");
    }

		

            } finally {
                close(rs);
                close(searchcam);
                close(conn);
            }
        } catch(Throwable oops) {
            oops.printStackTrace();
        }
    
    /*
    else 
    {
    	System.out.println("Today is not Friday, you can't borrow");
    }
    */
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

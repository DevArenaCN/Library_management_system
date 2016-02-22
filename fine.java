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

public class fine {

    static final String jdbcURL 
	= "jdbc:oracle:thin:@ora.csc.ncsu.edu:1521:orcl";

    public static void start(String userId) throws ParseException {


    	SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy");
        SimpleDateFormat edf = new SimpleDateFormat("MM/dd/yy HH");
   	   	

        try {

            // Load the driver. This creates an instance of the driver
	    // and calls the registerDriver method to make Oracle Thin
	    // driver available to clients.

            Class.forName("oracle.jdbc.driver.OracleDriver");

	    	String user = "ychen71";	// For example, "jsmith"
	    	String passwd = "200099159";	// Your 9 digit student ID number
            //String available_cam = "CREATE VIEW CAMERA_AVAILABLE AS SELECT * FROM CAMERA WHERE NOT EXIST (SELECT fCam_Borrow.cam_id,sCam_Borrow.cam_id FROM fCam_Borrow, sCam_Borrow WHERE fCam_Borrow.cam_id = CAMERA.cam_id AND sCam_Borrow.cam_id = CAMERA.cam_id)";
            
            //get sid from main function
            String sid = userId;
            Connection conn = null;
            Statement searchcam = null;
            Statement stmt = null;
            ResultSet rs = null;
            ResultSet r = null;
            PreparedStatement pstmt = null;
            String camid = null;
            long bfine = 0;
            long cfine = 0;

            try {

		// Get a connection from the first driver in the
		// DriverManager list that recognizes the URL jdbcURL

		conn = DriverManager.getConnection(jdbcURL, user, passwd);

		// Create a statement object that will be sending your
		// SQL statements to the DBMS

		searchcam = conn.createStatement();
        stmt = conn.createStatement();
        Scanner x = new Scanner(System.in);

        System.out.println("--------------------menu-------------------");
        System.out.println("choose function");
        System.out.println("1.check fine");
        System.out.println("2.clear fine");
        int i = x.nextInt();
        if(i==1)
        {        

       // searchcam2 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
        
        //camera fine
        String selectot = "select * from sCamBorrow where sid='"+sid+"' and OtMark=1";
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
            //System.out.println(nowti);
            //System.out.println(checkottemp);

            Long ccfine = nowtime.getTime() - checkot.getTime();
            cfine = ccfine/1000/60/60;
            //System.out.println("cfine = "+cfine);
            
        }



        //book fine
        String selectot2 = "select * from sBookBorrow where sid='"+sid+"' and OtMark=1";
        rs = searchcam.executeQuery(selectot2);
        while(rs.next())
        {
            Date checkotmark2 = rs.getDate("eTime");
            String checkottemp2 = df.format(checkotmark2);
            Date checkot2 = df.parse(checkottemp2);


            Date nowt2 = new Date();
            String nowti2 = df.format(nowt2);
            Date nowtime2 = df.parse(nowti2);

            //String camid = rs.getString("cam_id");
            //System.out.println(nowti2);
            //System.out.println(checkottemp2);

            Long bbfine = nowtime2.getTime() - checkot2.getTime();
            bfine = bbfine/(1000*60*60*12);
            //System.out.println("bfine = "+bfine);

             
            
        }
        long fine = (bfine+cfine);
        System.out.println("You total fine is "+fine);

        String judge = "select * from sfine where sid ='"+sid+"'";
        rs = searchcam.executeQuery(judge);
        if(rs.next())
        {
            String updatesfine="update sfine set bookfine ='"+bfine+"' ,camfine ='" +cfine+"' where sid ='"+sid+"'"; 
            stmt.executeUpdate(updatesfine); 
        }
        else
        {
            String insertsfine="insert into sfine values('"+sid+"' ,'"+bfine+"' ,'"+cfine+" ')";
            stmt.executeQuery(insertsfine); 
        }

    }
    else if(i==2)
    {
        String deletefine = "update sfine set bookfine = 0,camfine = 0 where sid ='"+sid+"'"; 
        stmt.executeUpdate(deletefine); 
    }
    else
    {
        System.out.println("Wrong Input");
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

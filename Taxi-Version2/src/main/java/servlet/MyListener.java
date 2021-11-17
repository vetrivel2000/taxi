package servlet;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import databasemanagement.DataBase;
import logical.LogicalLayer;
import pojo.History;

/**
 * Application Lifecycle Listener implementation class MyListener
 *
 */
@WebListener
public class MyListener implements ServletContextListener{
 
	DataBase dataBase = new DataBase();
	LogicalLayer logical = new LogicalLayer();
	int bookingId=0;
	String pickupPoint="",dropPoint="";
	int distance=0;
    public void contextDestroyed(ServletContextEvent arg0) { 
    	System.out.println("Server Shutdown");
    }
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ArrayList<History> timings= new ArrayList<>();
		long currentMilliSec=System.currentTimeMillis();
		try {
			timings=dataBase.getTimings("Booked",currentMilliSec);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		for(int i=0;i<timings.size();i++)
		{
			long milliSec=timings.get(i).getTime();
			bookingId=timings.get(i).getBookingId();
			pickupPoint=timings.get(i).getPickupPoint();
			dropPoint=timings.get(i).getDropPoint();
			distance=Math.abs(((int)dropPoint.charAt(0)-(int)pickupPoint.charAt(0)));
			DateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	        Date result = new Date(milliSec);
	        String date=simple.format(result);
	        logical.changeStatus(bookingId, date, distance);
		}
	}
}

package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import logical.LogicalLayer;
import pojo.History;
import pojo.Taxi;
import pojo.User;
public class TaxiServlet extends HttpServlet {
	int bookedTaxiId=0;
	static int bookingId=0,distance=0;
	static String pickupdate="";
	LogicalLayer logical= new LogicalLayer();
//	public void init() throws ServletException
//	{
//		logical.changeStatus(bookingId, pickupdate, distance);
//	}
	public void doPost(HttpServletRequest req,HttpServletResponse res) throws ServletException,IOException
	{ 
		String choice = req.getParameter("users");
		if(choice.equals("SubmitNewUser"))
		{
			String name=req.getParameter("name");
			String mobile=req.getParameter("mobile");
			String password=req.getParameter("password");
			String usertype=req.getParameter("usertype");
			if(!(usertype.equalsIgnoreCase("driver")))
			{	
			User user=logical.getUserObject(name, mobile,password,usertype);
			User object=null;
			PrintWriter pw=res.getWriter();
			try {
				object=logical.setUser(user);
				pw.println("User Successfully added");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				pw.print("User already added");
			}
			}
			else
			{
				Taxi taxi=logical.getTaxiObject(8,"A",0,mobile,password);
				Taxi object=null;
				PrintWriter pw=res.getWriter();
				try {
					object=logical.setTaxi(taxi);
					pw.println("Taxi succesfully added");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					pw.print("Taxi already added");
				}
			}
		}
		else if(choice.equals("AddWallet"))
		{
			String mobile=(String)req.getSession().getAttribute("MobileNumber");
			int amount=Integer.parseInt(req.getParameter("amount"));
			Taxi object=null;
			PrintWriter pw=res.getWriter();
			try {
				logical.addToWallet(mobile, amount);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pw.print("Amount added successfully");
		}
		else if(choice.equals("BookTaxi"))
		{
			int userId=(int)req.getSession().getAttribute("UserId");
			String date=req.getParameter("date");
			String[] dates=date.split("T");
			pickupdate=dates[0]+" "+dates[1];
			System.out.print(pickupdate);
			long pickupTime=logical.dateToMilliSecond(pickupdate);
			char pickupPoint=req.getParameter("pickuppoint").charAt(0);
			char dropPoint=req.getParameter("droppoint").charAt(0);
			String couponcode=req.getParameter("couponcode");
			ArrayList<Taxi> taxis=logical.getTaxi();
			ArrayList<Taxi> freeTaxis=logical.getFreeTaxis(taxis, pickupPoint, pickupTime);
			JSONArray array= new JSONArray();
			distance=Math.abs(((int)dropPoint-(int)pickupPoint));
			int earnings=distance*10;
			int wallet=logical.getWallet(userId);
			if(freeTaxis.size()==0)
			{
				array.put("No Taxis available");
			}
			else if(wallet<earnings)
			{
				array.put("You need to add money to wallet");
			}
			else
			{
				freeTaxis.sort(Comparator.comparingInt(Taxi::getTotalEarnings));
			     try {
					if((logical.getCoupon(userId).equals(couponcode)) || couponcode=="")
					 {
						try {
							bookedTaxiId = logical.BookTaxi(userId, pickupPoint, dropPoint, pickupTime, freeTaxis,couponcode);
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					int savings=0;
					try {
						savings = logical.getSavings(userId);
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if(savings>0)
					{
						array.put("You are getting offered of"+savings+"./rs ");
					}
					int tripCount=0;
					try {
					    tripCount=logical.getTripCount(userId);
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if(tripCount==2)
					{
						String couponCode=logical.generateCoupon();
						array.put("Congrats! You Completed 10 trips!! Here is your coupon for next one travel!!");
						array.put(couponCode);
						try {
							logical.setCoupon(couponCode, userId);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						logical.updateTripCount(userId,0);
					}
					else
					{
						try {
							logical.setTripCount(userId);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					logical.updateSavings(0,userId);
					array.put("Taxi-"+bookedTaxiId+" is alloted");
					History history=logical.getHistoryObject(userId, bookedTaxiId,pickupTime,String.valueOf(pickupPoint), String.valueOf(dropPoint));
					try {
						History historyObject=logical.setHistory(history);
						logical.updateHistory("Booked", historyObject.getBookingId());
						bookingId=historyObject.getBookingId();
						logical.changeStatus(bookingId, pickupdate,distance);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
				else
				{
					array.put("Invalid Coupon!");
				}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			res.getWriter().print(array.toString());
            res.getWriter().close();
		}
		else if(choice.equals("GetUserHistory"))
		{
			int userId=Integer.parseInt(req.getParameter("userid"));
			ArrayList<History> userHistory=logical.getUserHistory(userId);
			JSONArray array= new JSONArray();
			for(History history:userHistory)
			{
				JSONObject user1 = new JSONObject(history);
				array.put(user1);
			}
			res.setContentType("application/json");
	        res.getWriter().print(array.toString());
	        res.getWriter().close();
		}
		else if(choice.equals("TakeUserHistory"))
		{
			int userId=(int)req.getSession().getAttribute("UserId");
			ArrayList<History> userHistory=logical.getUserHistory(userId);
			JSONArray array= new JSONArray();
			for(History history:userHistory)
			{
				JSONObject user1 = new JSONObject(history);
				array.put(user1);
			}
			res.setContentType("application/json");
	        res.getWriter().print(array.toString());
	        res.getWriter().close();
		}
		else if(choice.equals("GetTaxiHistory"))
		{
			int taxiId=Integer.parseInt(req.getParameter("taxiid"));
			ArrayList<History> taxiHistory=logical.getTaxiHistory(taxiId);
			JSONArray array= new JSONArray();
			for(History history:taxiHistory)
			{
				JSONObject user1 = new JSONObject(history);
				array.put(user1);
			}
			res.setContentType("application/json");
	        res.getWriter().print(array.toString());
	        res.getWriter().close();
		}
		else if(choice.equals("TakeTaxiHistory"))
		{
			int taxiId=(int)req.getSession().getAttribute("TaxiId");
			ArrayList<History> taxiHistory=logical.getTaxiHistory(taxiId);
			JSONArray array= new JSONArray();
			for(History history:taxiHistory)
			{
				JSONObject user1 = new JSONObject(history);
				array.put(user1);
			}
			res.setContentType("application/json");
	        res.getWriter().print(array.toString());
	        res.getWriter().close();
		}
		else if(choice.equals("GetEarningsOfTaxi"))
		{
			int taxiId=(int)req.getSession().getAttribute("TaxiId");
			int earnings=0;
			try {
				earnings=logical.getEarnings(taxiId);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			res.getWriter().print("Your Total Earnings are:"+earnings);
			res.getWriter().close();
		}
		else if(choice.equals("ShowTaxis"))
		{
			String mobileNumber=(String)req.getSession().getAttribute("MobileNumber");
			ArrayList<History> bookedTaxis=null;
			try {
				bookedTaxis=logical.getBookedTaxi(mobileNumber);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JSONArray array= new JSONArray();
			for(History history:bookedTaxis)
			{
				JSONObject user1 = new JSONObject(history);
				array.put(user1);
			}
			res.setContentType("application/json");
	        res.getWriter().print(array.toString());
	        res.getWriter().close();
		}
		else if(choice.equals("CancelTaxi"))
		{
			String mobileNumber=(String)req.getSession().getAttribute("MobileNumber");
			int bookingId=Integer.parseInt(req.getParameter("bookingid"));
			int refund=0;
			try {
				refund=logical.cancelTaxi(bookingId, mobileNumber);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			res.getWriter().print("Taxi Cancelled Successfully "+refund+"Rs/. refunded to your wallet!");
		}
		else if(choice.equals("StoreFeedback"))
		{
			String feedback=req.getParameter("feedbacktext");
			int userId=(int)req.getSession().getAttribute("UserId");
			try {
				logical.addFeedback(feedback, userId, bookedTaxiId);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			res.getWriter().print("Thank you for your valuabale feedback!");
			res.getWriter().close();
		}
		else if(choice.equals("LoginSubmit"))
		{
			String mobileNumber=req.getParameter("mobilenumber");
			String password=req.getParameter("password");
			String encryptedPassword=logical.encryptPassword(password);
			String userType=null;
			int userId=0,taxiId=0;
			try {
				userType=logical.getUserType(mobileNumber,encryptedPassword);
				taxiId=logical.getTaxiId(mobileNumber,encryptedPassword);
				userId=logical.getUserId(mobileNumber);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(userType!=null && taxiId==0)
			{
					req.getSession().setAttribute("MobileNumber", mobileNumber);
					req.getSession().setAttribute("UserId", userId);
					res.getWriter().print(userType);
					res.getWriter().close();
			}
			else if(userType==null && taxiId!=0)
			{
				req.getSession().setAttribute("TaxiId", taxiId);
				res.getWriter().print("Taxi-"+taxiId);
				res.getWriter().close();
			}
			else
			{
				res.getWriter().print("Invalid Credentials");
				res.getWriter().close();
			}
		}
	}
	public void doGet(HttpServletRequest req,HttpServletResponse res) throws ServletException,IOException
	{
		String choice = req.getParameter("users");
		ArrayList<User> userList=null;
		ArrayList<Taxi> taxiList=null;
		if(choice.equals("User"))
		{
			userList = logical.getUser();
			JSONArray array= new JSONArray();
			for(User user:userList)
			{
				JSONObject user1 = new JSONObject(user);
				array.put(user1);
			}
			res.setContentType("application/json");
	        res.getWriter().print(array.toString());
	        res.getWriter().close();
		}
		else if(choice.equals("Taxi"))
		{
			taxiList=logical.getTaxi();
			JSONArray array= new JSONArray();
			for(Taxi taxi:taxiList)
			{
				JSONObject taxi1 = new JSONObject(taxi);
				array.put(taxi1);
			}
			res.setContentType("application/json");
	        res.getWriter().print(array.toString());
	        res.getWriter().close();
		}
	}
}
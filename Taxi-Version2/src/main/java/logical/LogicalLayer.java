package logical;

import databasemanagement.DataBase;
import pojo.History;
import pojo.Taxi;
import pojo.User;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class LogicalLayer {
    DataBase dataBase= null;
    Calendar calendar=Calendar.getInstance();
    Date now = calendar.getTime();
    public LogicalLayer(){
    	dataBase= new DataBase();
    }
    public String encryptPassword(String password)
    {
    	 char[] word=password.toCharArray();
         char[] encrypt= new char[password.length()];
         for (int i = 0; i < word.length ; i++) {
             encrypt[i]= (char) (word[i]+1);
         }
         String encrypted= new String(encrypt);
         return encrypted;
    }
    public User getUserObject(String name,String mobileNumber,String password,String usertype)
    {
    	String encryptedPassword=encryptPassword(password);
    	User user = new User();
    	user.setUserName(name);
    	user.setMobileNumber(mobileNumber);
    	user.setWallet(0);
    	user.setPassword(encryptedPassword);
    	user.setNewUser(true);
    	user.setSavings(0);
    	user.setCoupon("Empty");
    	user.setTripCount(0);
    	user.setUsertype(usertype);
    	return user;
    }
    public Taxi getTaxiObject(int freeTime,String currentPoint,int earnings,String mobileNumber,String password)
    {
    	String encryptedPassword=encryptPassword(password);
    	Taxi taxi=new Taxi();
    	taxi.setCurrentPoint(currentPoint);
    	taxi.setFreeTime(freeTime);
    	taxi.setTotalEarnings(earnings);
    	taxi.setMobileNumber(mobileNumber);
    	taxi.setPreviousPoint("A");
    	taxi.setPreviousTime(8);
    	taxi.setPassword(encryptedPassword);
    	return taxi;
    }
    public History getHistoryObject(int userId,int taxiId,long time,String pickupPoint,String dropPoint)
    {
    	String mobileNumber=dataBase.getTaxiNumber(taxiId);
    	String userName= dataBase.getUserName(userId);
    	String userNumber=dataBase.getUserNumber(userId);
    	History history = new History();
    	history.setTaxiNumber(mobileNumber);
    	history.setUserNumber(userNumber);
    	history.setUserName(userName);
    	history.setUserId(userId);
    	history.setDropPoint(dropPoint);
    	history.setPickupPoint(pickupPoint);
    	history.setTaxiId(taxiId);
    	history.setTime(time);
    	return history;
    }
    public long dateToMilliSecond(String dateString)
    {
    	 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd hh:mm");
    	 Date date=null;
    	 try {
			date = sdf.parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	 return date.getTime();
    }
    public void changeStatus(int bookingId,String date,int distance)
    {
    	String[] dateString=date.split(" ");
        String resultDate=dateString[0];
        String resultTime=dateString[1];
    	String[] dates=resultDate.split("-");
    	int year=Integer.parseInt(dates[0]);
    	int month=Integer.parseInt(dates[1]);
    	int day=Integer.parseInt(dates[2]);
    	
    	String times[]=resultTime.split(":");
    	int hour=Integer.parseInt(times[0]);
    	int minute=Integer.parseInt(times[1]);
    	Timer timer= new Timer();
    	TimerTask task= new TimerTask() {
			@Override
			public void run() {
				try {
					dataBase.updateStatus("Travelling", bookingId);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
    	};
    	calendar.set(Calendar.YEAR,year);
    	calendar.set(Calendar.MONTH,month-1);
    	calendar.set(Calendar.DAY_OF_MONTH,day);
    	calendar.set(Calendar.HOUR_OF_DAY,hour);
    	calendar.set(Calendar.MINUTE,minute);
    	calendar.set(Calendar.SECOND,0);
    	calendar.set(Calendar.MILLISECOND,0);
    	Date taskTime=calendar.getTime();
    	timer.schedule(task, taskTime);	
    	changeStatusAfterDrop(bookingId,distance);
    }
    public void changeStatusAfterDrop(int bookingId,int time)
    {
    	Timer timer= new Timer();
    	TimerTask task= new TimerTask() {
			@Override
			public void run() {
				try {
					dataBase.updateStatus("Dropped", bookingId);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
    	};
    	calendar.add(Calendar.HOUR,time);
//    	System.out.print(calendar.getTime());
    	Date taskTime=calendar.getTime();
    	timer.schedule(task,taskTime);	
    }
    public  ArrayList<Taxi> getFreeTaxis(ArrayList<Taxi> taxis, char pickupPoint, long time)
    {
        ArrayList<Taxi> freeTaxi=new ArrayList<>();
        for (Taxi taxi:taxis) {
        	int distanceBetweenUserAndTaxi=(int)taxi.getCurrentPoint().charAt(0)-(int)pickupPoint;
            if(distanceBetweenUserAndTaxi<0)
            {
            	distanceBetweenUserAndTaxi=-1*distanceBetweenUserAndTaxi;
            }
            if(taxi.getFreeTime()<=time && (distanceBetweenUserAndTaxi<=time-taxi.getFreeTime()))
            {
                freeTaxi.add(taxi);
            }
        }
        return freeTaxi;
    }
    public int BookTaxi(int userId,char pickupPoint,char dropPoint,long pickupTime,ArrayList<Taxi> freeTaxis,String couponcode) throws SQLException
    {
        int min=999;
        int earnings=0;
        int savings=0;
        long nextFreeTime=0;
        String nextPoint = "";
        Taxi bookedTaxi = null;
        for (Taxi taxi:freeTaxis)
        {
            int distanceBetweenUserAndTaxi=(int)taxi.getCurrentPoint().charAt(0)-(int)pickupPoint;
            if(distanceBetweenUserAndTaxi<0)
            {
            	distanceBetweenUserAndTaxi=-1*distanceBetweenUserAndTaxi;
            }
            if(distanceBetweenUserAndTaxi<min)
            {
            	boolean result=dataBase.isNewUser(userId);
                bookedTaxi=taxi;
                int distance=Math.abs(((int)dropPoint-(int)pickupPoint));
                if(result || couponcode.equals(dataBase.getCouponCode(userId)))
                {
                	earnings=(distance)*10;
                	savings=earnings*50/100;
                	earnings=earnings-savings;
                    dataBase.updateSavings(savings,userId);
                    dataBase.updateNewUser(userId);
                    dataBase.storeCoupon("Empty", userId);
                }
                else
                {
                	earnings=(distance)*10;
                }
                long dropTime=pickupTime+distance;
                nextFreeTime=dropTime;
                nextPoint= String.valueOf(dropPoint);
                min=distanceBetweenUserAndTaxi;
            }
        }
        System.out.println(earnings);
        bookedTaxi.setTotalEarnings(bookedTaxi.getTotalEarnings()+earnings);
        bookedTaxi.setFreeTime(nextFreeTime);
        bookedTaxi.setCurrentPoint(nextPoint);
        int userWallet=dataBase.getWallet(userId);
//        System.out.println(userWallet);
        int newWallet=userWallet-earnings;
        System.out.println(newWallet);
        dataBase.updateUser(newWallet, userId);
        dataBase.updateTaxi(bookedTaxi.getTotalEarnings(),bookedTaxi.getCurrentPoint(), bookedTaxi.getFreeTime(), bookedTaxi.getId());
        return bookedTaxi.getId();
    }
    public void addToWallet(String mobileNumber,int amount) throws SQLException{
    	int userId=dataBase.getUserId(mobileNumber);
    	int userWallet=dataBase.getWallet(userId);
    	int newWallet=userWallet+amount;
    	dataBase.updateUser(newWallet, userId);
    }
    public void addFeedback(String feeback,int userId,int taxiId) throws SQLException{
    	dataBase.storeFeedback(feeback, userId, taxiId);
    }
    public String generateCoupon()
    {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                                    + "0123456789"
                                    + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
              int index=(int)(AlphaNumericString.length()*Math.random());
              sb.append(AlphaNumericString.charAt(index));
        }
  
        return sb.toString();
    }
    public int cancelTaxi(int bookingId,String mobileNumber) throws SQLException{
    	int taxiId=dataBase.getBookedTaxiId(bookingId);
    	int userId=dataBase.getUserId(mobileNumber);
    	String timing=dataBase.getPreviousTimings(taxiId);
    	String[] array=timing.split(" ");
    	long time = Long.parseLong(array[0]);
    	String point = array[1];
    	int amount=0;
    	String points=dataBase.getPoints(taxiId, userId);
    	dataBase.updateStatus("Cancelled", bookingId);
    	String[] array1=points.split(" ");
    	System.out.print(points);
    	String pickpoint=array1[0];
    	String droppoint=array1[1];
    	int distance=(int)pickpoint.charAt(0)-(int)droppoint.charAt(0);
    	if(distance<0)
    	{
    		distance=distance*-1;
    	}
    	amount=distance*10;
    	int userWallet=dataBase.getWallet(userId);
    	int newWallet=amount+userWallet;
    	dataBase.updateUser(newWallet, userId);
    	dataBase.updateTaxiAfterCancel(taxiId, time, point);
    	return amount;
    }
    public ArrayList<History> getBookedTaxi(String mobileNumber) throws SQLException
    {
    	int userId=dataBase.getUserId(mobileNumber);
    	return dataBase.getBookedHistory(userId);
    }
    public void updateSavings(int savings,int userId)
    {
    	dataBase.updateSavings(savings, userId);
    }
    public int getSavings(int userId) throws SQLException {
    	return dataBase.getSavings(userId);
    }
    public User setUser(User user) throws SQLException
    {
        int userId=dataBase.createUser(user);
        user.setUserId(userId);
        return user;
    }
    public Taxi setTaxi(Taxi taxi) throws SQLException
    {
        int taxiId=dataBase.createTaxi(taxi);
        taxi.setId(taxiId);
        return taxi;
    }
    public void setCoupon(String coupon,int userId) throws SQLException{
    	dataBase.storeCoupon(coupon, userId);
    }
    public void setTripCount(int userId) throws SQLException
    {
    	int tripCount=dataBase.getTripCount(userId);
    	tripCount++;
    	dataBase.addTripCount(userId, tripCount);
    }
    public void updateTripCount(int userId,int tripCount)
    {
    	try {
			dataBase.addTripCount(userId, tripCount);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public History setHistory(History history) throws SQLException
    {
    	int BookingId=dataBase.createHistory(history);
    	history.setBookingId(BookingId);
    	return history;
    }
    public void updateHistory(String status,int userId) throws SQLException
    {
    	dataBase.updateStatus(status, userId);
    }
    public ArrayList<User> getUser()
    {
    	return dataBase.getUsers();
    }
    public int getWallet(int userId)
    {
    	return dataBase.getWallet(userId);
    }
    public ArrayList<Taxi> getTaxi()
    {
    	return dataBase.getTaxis();
    }
    public String getTaxiNumber(int taxiId)
    {
    	return dataBase.getTaxiNumber(taxiId);
    }
    public ArrayList<History> getUserHistory(int userId){
    	return dataBase.getUserHistory(userId);
    }
    public int getUserId(String mobileNumber) throws SQLException {
    	return dataBase.getUserId(mobileNumber);
    }
    public String getUserType(String mobileNumber,String password) throws SQLException{
    	return dataBase.getUserType(mobileNumber, password);
    }
    public ArrayList<User> searchUser(String sub)
    {
    	return dataBase.searchUser(sub);
    }
    public ArrayList<History> getTaxiHistory(int taxiId){
    	return dataBase.getTaxiHistory(taxiId);
    }
    public int getEarnings(int taxiId) throws SQLException
    {
    	return dataBase.getEarnings(taxiId);
    }
    public String getCoupon(int userId) throws SQLException
    {
    	return dataBase.getCouponCode(userId);
    }
    public int getTaxiId(String mobileNumber,String password) throws SQLException
    {
    	return dataBase.getTaxiId(mobileNumber, password);
    }
    public int getTripCount(int userId) throws SQLException
    {
    	return dataBase.getTripCount(userId);
    }
}

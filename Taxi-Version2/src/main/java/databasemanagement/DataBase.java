package databasemanagement;

import pojo.History;
import pojo.Taxi;
import pojo.User;

import java.sql.*;
import java.util.ArrayList;

public class DataBase {
    private static Connection connection = null;
    public DataBase() {
        try {
        	Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost/db", "root", "Vetri@50");
            System.out.println("DataBase Connected");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connection closed");
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
    public int getUserId(String mobileNumber) throws SQLException
    {
    	int userId = 0;
    	try {
			PreparedStatement statement = connection.prepareStatement("select UserId from Users where MobileNumber="+mobileNumber);
			ResultSet resultSet=statement.executeQuery();
			boolean value=resultSet.next();
			if(value)
			{
				userId=resultSet.getInt("UserId");
			}
//			System.out.println(resultSet.getInt(userId)+"after");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return userId;
    }
    public String getUserNumber(int userId)
    {
    	String mobileNumber="";
    	try {
			PreparedStatement statement = connection.prepareStatement("select MobileNumber from Users where UserId="+userId);
			ResultSet resultSet=statement.executeQuery();
			boolean value=resultSet.next();
			if(value)
			{
				mobileNumber=resultSet.getString("MobileNumber");
			}
//			System.out.println(resultSet.getInt(userId)+"after");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return mobileNumber;
    }
    public String getUserType(String mobileNumber,String password) throws SQLException
    {
    	String type=null;
    	PreparedStatement statement = connection.prepareStatement("select usertype from Users where MobileNumber="+mobileNumber+" and PassWord='"+password+"'");
    	ResultSet resultSet=statement.executeQuery();
    	boolean value=resultSet.next();
    	if(value)
    	{
    		type=resultSet.getString("usertype");
    	}
    	return type;
    }
    public int createUser(User user) throws SQLException
    {
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        int userId;
        try{
            statement = connection.prepareStatement("insert into Users(Name,MobileNumber,Wallet,NewUser,Savings,CouponCode,TripCount,PassWord,usertype) values(?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getUserName());
            statement.setString(2, user.getMobileNumber());
            statement.setInt(3,user.getWallet());
            statement.setBoolean(4, user.isNewUser());
            statement.setInt(5, user.getSavings());
            statement.setString(6,user.getCoupon());
            statement.setInt(7, user.getTripCount());
            statement.setString(8, user.getPassword());
            statement.setString(9, user.getUsertype());
            statement.executeUpdate();
            resultSet=statement.getGeneratedKeys();
            resultSet.next();
            userId =resultSet.getInt(1);
        }
        finally {
            try {
                statement.close();
                resultSet.close();
            }catch(Exception e){}
        }
        return userId;
    }
    public boolean isNewUser(int userId) throws SQLException
    {
    	ResultSet resultSet = null;
        PreparedStatement statement = null;
        boolean result=false;
        try {
        	statement=connection.prepareStatement("select NewUser from users where UserId="+userId);
        	resultSet=statement.executeQuery();
        	resultSet.next();
        	result=resultSet.getBoolean("NewUser");
        }
        finally {
        	statement.close();
        	resultSet.close();
        }
        return result;
    }
    public void updateStatus(String status,int bookingId) throws SQLException 
    {
    	PreparedStatement statement = null;
        try {
        	statement=connection.prepareStatement("update History set Status=? where BookingId=?");
        	statement.setString(1, status);
        	statement.setInt(2, bookingId);
        	statement.executeUpdate();
        }
        finally {
        	statement.close();
        }
    }
    public int getBookedTaxiId(int bookingId)
    {
    	ResultSet resultSet = null;
        PreparedStatement statement = null;
        int result=0;
        try {
        	statement=connection.prepareStatement("select TaxiId from History where BookingId="+bookingId);
        	resultSet=statement.executeQuery();
        	resultSet.next();
        	result=resultSet.getInt("TaxiId");
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        finally {
        	try {
				statement.close();
				resultSet.close();
			} catch (SQLException e) {}
        }
        return result;
    }
    public int getSavings(int userId) throws SQLException
    {
    	ResultSet resultSet = null;
        PreparedStatement statement = null;
        int result=0;
        try {
        	statement=connection.prepareStatement("select Savings from users where UserId="+userId);
        	resultSet=statement.executeQuery();
        	resultSet.next();
        	result=resultSet.getInt("Savings");
        }
        finally {
        	statement.close();
        	resultSet.close();
        }
        return result;
    }
    public void addTripCount(int userId,int count) throws SQLException
    {
        PreparedStatement statement = null;
        try {
        	statement=connection.prepareStatement("update users set TripCount=? where UserId=?");
        	statement.setInt(1, count);
        	statement.setInt(2, userId);
        	statement.executeUpdate();
        }
        finally {
        	statement.close();
        }
    }
    public int getTripCount(int userId) throws SQLException
    {
    	ResultSet resultSet = null;
    	PreparedStatement statement = null;
    	int count=0;
    	try {
    		statement=connection.prepareStatement("select TripCount from users where UserId=?");
    		statement.setInt(1, userId);
    		resultSet=statement.executeQuery();
    		resultSet.next();
    		count=resultSet.getInt("TripCount");
    	}
    	finally {
    		try {
				statement.close();
				resultSet.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	}
    	return count;
    }
    public String getCouponCode(int userId) throws SQLException
    {
    	ResultSet resultSet = null;
    	PreparedStatement statement = null;
    	String coupon="";
    	try {
    		statement=connection.prepareStatement("select CouponCode from users where UserId=?");
    		statement.setInt(1, userId);
    		resultSet=statement.executeQuery();
    		resultSet.next();
    		coupon=resultSet.getString("CouponCode");
    	}
    	finally {
    		try {
				statement.close();
				resultSet.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	}
    	return coupon;
    }
    public void storeCoupon(String coupon,int userId) throws SQLException
    {
        PreparedStatement statement = null;
        try {
        	statement=connection.prepareStatement("update users set CouponCode=? where UserId=?");
        	statement.setString(1, coupon);
        	statement.setInt(2, userId);
        	statement.executeUpdate();
        }
        finally {
        	statement.close();
        }
    }
    public void storeFeedback(String feedback,int userId,int taxiId)
    {
    	PreparedStatement statement = null;
    	try {
    		statement=connection.prepareStatement("update History set FeedBack=? where UserId=? and TaxiId=?");
    		statement.setString(1, feedback);
    		statement.setInt(2, userId);
    		statement.setInt(3, taxiId);
    		statement.executeUpdate();
    	} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	finally {
    		try {
				statement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    public void updateSavings(int savings,int userId)
    {
    	PreparedStatement statement = null;
    	try
        {
           statement=connection.prepareStatement("update Users set Savings=? where UserId=?");
           statement.setInt(1, savings);
           statement.setInt(2, userId);
           statement.executeUpdate();
        }
        catch (Exception e)
        {
           System.out.println(e);
        }
    	finally {
    		try {
    			statement.close();
    		}
    		catch(SQLException e)
    		{
    			e.printStackTrace();
    		}
    	}
    }
    public void updateNewUser(int userId)
    {
    	PreparedStatement statement = null;
    	try
        {
           statement=connection.prepareStatement("update Users set NewUser=? where UserId=?");
           statement.setBoolean(1, false);
           statement.setInt(2, userId);
           statement.executeUpdate();
        }
        catch (Exception e)
        {
           System.out.println(e);
        }
    	finally {
    		try {
    			statement.close();
    		}
    		catch(SQLException e)
    		{
    			e.printStackTrace();
    		}
    	}
    }
    
    public int createHistory(History history) throws SQLException{
        PreparedStatement statement = null;
        ResultSet resultSet=null;
        int bookingId=0;
        try {
        	statement=connection.prepareStatement("insert into History(UserId,TaxiId,Time,PickupPoint,DropPoint,taxinumber,UserName,UserNumber) values(?,?,?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
        	statement.setInt(1,history.getUserId());
        	statement.setInt(2,history.getTaxiId());
        	statement.setLong(3,history.getTime());
        	statement.setString(4,history.getPickupPoint());
        	statement.setString(5,history.getDropPoint());
        	statement.setString(6,history.getTaxiNumber());
        	statement.setString(7,history.getUserName());
        	statement.setString(8,history.getUserNumber());
        	statement.executeUpdate();
            resultSet=statement.getGeneratedKeys();
            resultSet.next();
            bookingId =resultSet.getInt(1);
        }
        finally {
        	statement.close();
        }
        return bookingId;
    }
    public int createTaxi(Taxi taxi) throws SQLException
    {
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        int taxiId;
        try{
            statement = connection.prepareStatement("insert into Taxi(FreeTime,CurrentPoint,Earnings,MobileNumber,PreviousPoint,PreviousTime,PassWord) values(?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, taxi.getFreeTime());
            statement.setString(2,taxi.getCurrentPoint());
            statement.setInt(3,taxi.getTotalEarnings());
            statement.setString(4, taxi.getMobileNumber());
            statement.setString(5, taxi.getPreviousPoint());
            statement.setLong(6, taxi.getPreviousTime());
            statement.setString(7, taxi.getPassword());
            statement.executeUpdate();
            resultSet=statement.getGeneratedKeys();
            resultSet.next();
            taxiId =resultSet.getInt(1);
        }
        finally {
            try {
                statement.close();
                resultSet.close();
            }catch(Exception e){}
        }
        return taxiId;
    }
    public ArrayList<History> getTimings(String status,long milliSec) throws SQLException
    {
    	ArrayList<History> times=new ArrayList<>();
    	PreparedStatement statement = connection.prepareStatement("select * from History where Status='"+status+"' and Time<="+milliSec);
    	ResultSet resultSet=statement.executeQuery();
    	while(resultSet.next())
    	{
    		 History object = new History();
             object.setTaxiId(resultSet.getInt("TaxiId"));
             object.setUserId(resultSet.getInt("UserId"));
             object.setTime(resultSet.getLong("Time"));
             object.setPickupPoint(resultSet.getString("PickupPoint"));
             object.setDropPoint(resultSet.getString("DropPoint"));
             object.setStatus(resultSet.getString("Status"));
             object.setBookingId(resultSet.getInt("BookingId"));
             times.add(object);
    	}
    	return times;
    }
    public int getTaxiId(String mobileNumber,String password) throws SQLException
    {
    	int taxiId=0;
    	PreparedStatement statement = connection.prepareStatement("select TaxiId from Taxi where MobileNumber="+mobileNumber+" and PassWord='"+password+"'");
    	ResultSet resultSet=statement.executeQuery();
    	boolean value=resultSet.next();
    	if(value)
    	{
    		taxiId=resultSet.getInt("TaxiId");
    	}
    	return taxiId;
    }
    public int getEarnings(int taxiId) throws SQLException
    {
    	int earnings=0;
    	PreparedStatement statement = connection.prepareStatement("select Earnings from Taxi where TaxiId="+taxiId);
    	ResultSet resultSet=statement.executeQuery();
    	boolean value=resultSet.next();
    	if(value)
    	{
    		earnings=resultSet.getInt("Earnings");
    	}
    	return earnings;
    }
    public void updateTaxi(int earnings,String dropPoint,long freeTime,int taxiId)
    {
    	PreparedStatement statement = null;
    	 try
         {
            statement=connection.prepareStatement("update Taxi set FreeTime=?,CurrentPoint=?,Earnings=? where TaxiId=?");
            statement.setLong(1,freeTime);
            statement.setString(2, dropPoint);
            statement.setInt(3, earnings);
            statement.setInt(4, taxiId);
            statement.executeUpdate();
         }
         catch (Exception e)
         {
            System.out.println(e);
         }
    	 finally {
    		 try {
				statement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	 }
    }
    public void deleteHistory(int taxiId,int userId)
    {
    	PreparedStatement statement = null;
    	try {
    		statement=connection.prepareStatement("delete from History where UserId=? and taxiId=?");
    		statement.setInt(1, userId);
            statement.setInt(2, taxiId);
            statement.executeUpdate();
    	}
        catch (Exception e)
        {
           System.out.println(e);
        }
    	finally {
    		try {
    			statement.close();
    		}
    		catch(SQLException e)
    		{
    			e.printStackTrace();
    		}
    	}
    }
    public String getPreviousTimings(int taxiId)
    {
    	PreparedStatement statement = null;
    	long time=0;
    	String point="";
    	String output="";
    	try {
    		statement=connection.prepareStatement("select PreviousPoint,PreviousTime from Taxi where TaxiId="+taxiId);
    		ResultSet resultSet=statement.executeQuery();
			resultSet.next();
			time=resultSet.getLong("PreviousTime");
			point=resultSet.getString("PreviousPoint");
			output=time+" "+point;
    	}
    	catch(SQLException e)
    	{
    		System.out.print(e);
    	}
    	finally {
    		try {
				statement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	return output;
    }
    public String getPoints(int taxiId,int userId)
    {
    	PreparedStatement statement = null;
    	String pickpoint="";
    	String droppoint="";
    	String output="";
    	System.out.print(taxiId+" "+userId);
    	try {
    		statement=connection.prepareStatement("select PickupPoint,DropPoint from History where TaxiId=? and UserId=?");
    		statement.setInt(1, taxiId);
    		statement.setInt(2, userId);
    		ResultSet resultSet=statement.executeQuery();
			while(resultSet.next()) {
				pickpoint=resultSet.getString("PickupPoint");
				droppoint=resultSet.getString("DropPoint");
			}
			output=pickpoint+" "+droppoint;
    	}
    	catch(SQLException e)
    	{
    		System.out.print(e);
    	}
    	finally {
    		try {
				statement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	return output;
    }
    public void updateTaxiAfterCancel(int taxiId,long time,String point)
    {
    	PreparedStatement statement = null;
    	try
    	{
    		 statement=connection.prepareStatement("update Taxi set FreeTime=?,CurrentPoint=? where TaxiId=?");
             statement.setLong(1, time);
             statement.setString(2, point);
             statement.setInt(3,taxiId);
             statement.executeUpdate();
    	}
    	catch (Exception e)
        {
           System.out.println(e);
        }
    	finally {
    		try {
    			statement.close();
    		}
    		catch(SQLException e)
    		{
    			e.printStackTrace();
    		}
    	}
    }
    public void updateUser(int earnings,int userId)
    {
    	PreparedStatement statement = null;
    	try
        {
           statement=connection.prepareStatement("update Users set Wallet=? where UserId=?");
           statement.setInt(1, earnings);
           statement.setInt(2, userId);
           statement.executeUpdate();
        }
        catch (Exception e)
        {
           System.out.println(e);
        }
    	finally {
    		try {
    			statement.close();
    		}
    		catch(SQLException e)
    		{
    			e.printStackTrace();
    		}
    	}
    }
    public int getWallet(int userId)
    {
    	int wallet = 0;
    	try {
			PreparedStatement statement = connection.prepareStatement("select Wallet from Users where UserId="+userId);
			ResultSet resultSet=statement.executeQuery();
			resultSet.next();
			wallet=resultSet.getInt("Wallet");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return wallet;
    }
    public String getUserName(int userId)
    {
    	String userName="";
    	try {
			PreparedStatement statement = connection.prepareStatement("select Name from Users where UserId="+userId);
			ResultSet resultSet=statement.executeQuery();
			resultSet.next();
			userName=resultSet.getString("Name");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return userName;
    }
    public ArrayList<User> searchUser(String sub)
    {
        ArrayList<User> userList = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("select * from Users where Name like '%"+sub+"%' or MobileNumber like '%"+sub+"%'");
             ResultSet resultSet = statement.executeQuery()){
            while (resultSet.next()) {
                User object = new User();
                object.setUserId(resultSet.getInt("UserId"));
                object.setUserName(resultSet.getString("Name"));
                object.setMobileNumber(resultSet.getString("MobileNumber"));
                object.setWallet(resultSet.getInt("Wallet"));
                userList.add(object);
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        return userList;
    }
    public ArrayList<User> getUsers()
    {
    	String type="user";
        ArrayList<User> userList = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("select * from Users where usertype='"+type+"'");
             ResultSet resultSet = statement.executeQuery()){
            while (resultSet.next()) {
                User object = new User();
                object.setUserId(resultSet.getInt("UserId"));
                object.setUserName(resultSet.getString("Name"));
                object.setMobileNumber(resultSet.getString("MobileNumber"));
                object.setWallet(resultSet.getInt("Wallet"));
                userList.add(object);
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        return userList;
    }
    public ArrayList<Taxi> getTaxis()
    {
        ArrayList<Taxi> taxiList = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("select * from Taxi");
             ResultSet resultSet = statement.executeQuery()){
            while (resultSet.next()) {
                Taxi object = new Taxi();
                object.setId(resultSet.getInt("TaxiId"));
                object.setCurrentPoint(resultSet.getString("CurrentPoint"));
                object.setFreeTime(resultSet.getLong("FreeTime"));
                object.setMobileNumber(resultSet.getString("MobileNumber"));
                object.setTotalEarnings(resultSet.getInt("Earnings"));
                taxiList.add(object);
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        return taxiList;
    }
    public ArrayList<History> getBookedHistory(int userId)
    {
    	ArrayList<History> userHistory= new ArrayList<>();
    	String status="Booked";
    	try (PreparedStatement statement = connection.prepareStatement("select * from History where UserId="+userId+" and Status='"+status+"'");
                ResultSet resultSet = statement.executeQuery()){
               while (resultSet.next()) {
                   History object = new History();
                   object.setTaxiId(resultSet.getInt("TaxiId"));
                   object.setUserId(resultSet.getInt("UserId"));
                   object.setTime(resultSet.getLong("Time"));
                   object.setPickupPoint(resultSet.getString("PickupPoint"));
                   object.setDropPoint(resultSet.getString("DropPoint"));
                   object.setStatus(resultSet.getString("Status"));
                   object.setBookingId(resultSet.getInt("BookingId"));
                   object.setTaxiNumber(resultSet.getString("taxinumber"));
                   userHistory.add(object);
               }
           }
    	catch(Exception e)
    	{
    		System.out.println(e);
    	}
    	return userHistory;
    }
    public String getTaxiNumber(int taxiId)
    {
    	String mobileNumber="";
    	try {
			PreparedStatement statement = connection.prepareStatement("select MobileNumber from Taxi where TaxiId="+taxiId);
			ResultSet resultSet=statement.executeQuery();
			boolean value=resultSet.next();
			if(value)
			{
				mobileNumber=resultSet.getString("MobileNumber");
			}
//			System.out.println(resultSet.getInt(userId)+"after");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
    	return mobileNumber;
    }
    public ArrayList<History> getUserHistory(int userId)
    {
    	ArrayList<History> userHistory= new ArrayList<>();
    	try (PreparedStatement statement = connection.prepareStatement("select * from History where UserId="+userId);
                ResultSet resultSet = statement.executeQuery()){
               while (resultSet.next()) {
                   History object = new History();
                   object.setTaxiId(resultSet.getInt("TaxiId"));
                   object.setUserId(resultSet.getInt("UserId"));
                   object.setTime(resultSet.getLong("Time"));
                   object.setPickupPoint(resultSet.getString("PickupPoint"));
                   object.setDropPoint(resultSet.getString("DropPoint"));
                   object.setStatus(resultSet.getString("Status"));
                   object.setTaxiNumber(resultSet.getString("taxinumber"));
                   object.setUserName(resultSet.getString("UserName"));
                   object.setUserNumber(resultSet.getString("UserNumber"));
                   userHistory.add(object);
               }
           }
    	catch(Exception e)
    	{
    		System.out.println(e);
    	}
    	return userHistory;
    }
    public ArrayList<History> getTaxiHistory(int taxiId)
    {
    	ArrayList<History> taxiHistory= new ArrayList<>();
    	try (PreparedStatement statement = connection.prepareStatement("select * from History where TaxiId="+taxiId);
                ResultSet resultSet = statement.executeQuery()){
               while (resultSet.next()) {
                   History object = new History();
                   object.setTaxiId(resultSet.getInt("TaxiId"));
                   object.setUserId(resultSet.getInt("UserId"));
                   object.setTime(resultSet.getLong("Time"));
                   object.setPickupPoint(resultSet.getString("PickupPoint"));
                   object.setDropPoint(resultSet.getString("DropPoint"));
                   object.setStatus(resultSet.getString("Status"));
                   object.setTaxiNumber(resultSet.getString("taxinumber"));
                   object.setUserName(resultSet.getString("UserName"));
                   object.setUserNumber(resultSet.getString("UserNumber"));
                   taxiHistory.add(object);
               }
           }
    	catch(Exception e)
    	{
    		System.out.println(e);
    	}
    	return taxiHistory;
    }
}

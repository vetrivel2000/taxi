package pojo;

public class History {
	private int userId;
	private int taxiId;
	private int bookingId;
	private long time;
	private String pickupPoint;
	private String dropPoint;
	private String status;
	private String taxiNumber;
	private String userName;
	private String userNumber;
	
	public String getUserNumber() {
		return userNumber;
	}
	public void setUserNumber(String userNumber) {
		this.userNumber = userNumber;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getTaxiNumber() {
		return taxiNumber;
	}
	public void setTaxiNumber(String taxiNumber) {
		this.taxiNumber = taxiNumber;
	}
	public int getBookingId() {
		return bookingId;
	}
	public void setBookingId(int bookingId) {
		this.bookingId = bookingId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getTaxiId() {
		return taxiId;
	}
	public void setTaxiId(int taxiId) {
		this.taxiId = taxiId;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getPickupPoint() {
		return pickupPoint;
	}
	public void setPickupPoint(String pickupPoint) {
		this.pickupPoint = pickupPoint;
	}
	public String getDropPoint() {
		return dropPoint;
	}
	public void setDropPoint(String dropPoint) {
		this.dropPoint = dropPoint;
	}
	@Override
	public String toString() {
		return "History [userId=" + userId + ", taxiId=" + taxiId + ", bookingId=" + bookingId + ", time=" + time
				+ ", pickupPoint=" + pickupPoint + ", dropPoint=" + dropPoint + ", status=" + status + ", taxiNumber="
				+ taxiNumber + "]";
	}
	
}

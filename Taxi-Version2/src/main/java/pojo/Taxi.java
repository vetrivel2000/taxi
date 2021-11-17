package pojo;

public class Taxi {
    private long freeTime;
    private String currentPoint;
    private int totalEarnings;
    private int id;
    private String mobileNumber;
    private String previousPoint;
    private long previousTime;
    private String password;
    
    public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPreviousPoint() {
		return previousPoint;
	}
    
	public void setPreviousPoint(String previousPoint) {
		this.previousPoint = previousPoint;
	}

	public long getPreviousTime() {
		return previousTime;
	}

	public void setPreviousTime(long previousTime) {
		this.previousTime = previousTime;
	}

	public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getFreeTime() {
        return freeTime;
    }

    public void setFreeTime(long freeTime) {
        this.freeTime = freeTime;
    }

    public String getCurrentPoint() {
        return currentPoint;
    }

    public void setCurrentPoint(String currentPoint) {
        this.currentPoint = currentPoint;
    }

    public int getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(int totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

	@Override
	public String toString() {
		return "Taxi [freeTime=" + freeTime + ", currentPoint=" + currentPoint + ", totalEarnings=" + totalEarnings
				+ ", id=" + id + ", mobileNumber=" + mobileNumber + "]";
	}
    
}

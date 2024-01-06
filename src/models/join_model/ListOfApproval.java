package models.join_model;
import java.sql.Timestamp;

public class ListOfApproval {
	private String userName;
	private Timestamp requestAt;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public Timestamp getRequestAt() {
		return requestAt;
	}
	public void setRequestAt(Timestamp requestAt) {
		this.requestAt = requestAt;
	}
	// shift + alt + s
	public ListOfApproval(String userName, Timestamp requestAt) {
		super();
		this.userName = userName;
		this.requestAt = requestAt;
	}
	
}

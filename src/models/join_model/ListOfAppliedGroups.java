package models.join_model;

import java.sql.Timestamp;

public class ListOfAppliedGroups {
	private String groupName;
	private String status;
	private Timestamp requestAt;
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Timestamp getRequestAt() {
		return requestAt;
	}
	public void setRequestAt(Timestamp requestAt) {
		this.requestAt = requestAt;
	}

	
}

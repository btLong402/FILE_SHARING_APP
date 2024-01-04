package models.group_model;

public class ListOfJoinRequests {
	private String groupName;
	private String requestedUserName;
	private String requestAt;
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getRequestedUserName() {
		return requestedUserName;
	}
	public void setRequestedUserName(String requestedUserName) {
		this.requestedUserName = requestedUserName;
	}
	public String getRequestAt() {
		return requestAt;
	}
	public void setRequestAt(String requestAt) {
		this.requestAt = requestAt;
	}
	
}

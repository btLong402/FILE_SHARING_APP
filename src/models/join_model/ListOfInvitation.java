package models.join_model;

import java.sql.Timestamp;

public class ListOfInvitation {
	private String groupName;
	private String status;
	private Timestamp inviteAt;
	
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
	public Timestamp getInviteAt() {
		return inviteAt;
	}
	public void setInviteAt(Timestamp inviteAt) {
		this.inviteAt = inviteAt;
	}
	// shift + alt + s
	public ListOfInvitation(String groupName, String status, Timestamp inviteAt) {
		super();
		this.groupName = groupName;
		this.status = status;
		this.inviteAt = inviteAt;
	}
	
}

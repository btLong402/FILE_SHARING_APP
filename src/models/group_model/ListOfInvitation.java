package models.group_model;

import java.sql.Timestamp;

public class ListOfInvitation {
	private String groupName;
	private Timestamp invitedAt;
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public Timestamp getInvitedAt() {
		return invitedAt;
	}
	public void setInvitedAt(Timestamp invitedAt) {
		this.invitedAt = invitedAt;
	}
}

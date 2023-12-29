package models.group_model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

public class GroupModel {
	private String groupId;
	private String groupName;
	private LocalDateTime createAt;
	private String createBy;
	
	public GroupModel(String groupName, String createBy) {
		super();
		this.groupName = groupName;
		this.createBy = createBy;
	}
	
	public GroupModel(String groupId, String groupName, LocalDateTime createAt, String createBy) {
		super();
		this.groupId = groupId;
		this.groupName = groupName;
		this.createAt = createAt;
		this.createBy = createBy;
	}
	@Override
	public String toString() {
		return "GroupModel [groupId=" + groupId + ", groupName=" + groupName + ", createAt=" + createAt + ", createBy="
				+ createBy + "]";
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getCreateAt() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss"); 
		return dateFormat.format(createAt);
	}
	public void setCreateAt(LocalDateTime createAt) {
		this.createAt = createAt;
	}
	public String getCreateBy() {
		return createBy;
	}
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
}

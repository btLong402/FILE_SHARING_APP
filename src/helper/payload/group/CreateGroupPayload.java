package helper.payload.group;

import helper.payload.BasePayload;

public class CreateGroupPayload extends BasePayload {
	String groupName;

	public CreateGroupPayload() {
		super();
	}

	public String getGroupName() {
		return groupName;
	}
	@Override
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
}

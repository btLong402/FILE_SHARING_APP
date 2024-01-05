package helper.request.payload.group;

import helper.request.payload.BasePayload;

public class LeaveGroupPayload extends BasePayload {
	String groupName;

	public LeaveGroupPayload() {
		super();
	}

	@Override
	public void setGroupName(String groupName) {
		// TODO Auto-generated method stub
		this.groupName = groupName;
	}

}

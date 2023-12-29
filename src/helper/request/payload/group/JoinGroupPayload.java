package helper.request.payload.group;

import helper.request.payload.BasePayload;

public class JoinGroupPayload extends BasePayload {
	String groupName;

	public JoinGroupPayload() {
		super();
	}

	@Override
	public void setGroupName(String groupName) {
		// TODO Auto-generated method stub
		this.groupName = groupName;
	}
}

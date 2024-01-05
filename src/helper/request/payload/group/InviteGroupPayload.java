package helper.request.payload.group;

import helper.request.payload.BasePayload;

public class InviteGroupPayload extends BasePayload {
	String invitedUserName;
	String groupName;

	public InviteGroupPayload() {
		super();
	}

	@Override
	public void setInvitedName(String invitedName) {
		// TODO Auto-generated method stub
		this.invitedUserName = invitedName;
	}
}

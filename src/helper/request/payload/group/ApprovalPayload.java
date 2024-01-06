package helper.request.payload.group;

import helper.request.payload.BasePayload;

public class ApprovalPayload extends BasePayload{
	String requester;
	String groupName;
	String decision;
	@Override
	public void setGroupName(String groupName) {
		// TODO Auto-generated method stub
		super.setGroupName(groupName);
		this.groupName = groupName;
	}
	
	@Override
	public void setRequester(String requester) {
		this.requester = requester;
	}
	
	@Override
	public void setDecision(String decision) {
		this.decision = decision;
	}
}

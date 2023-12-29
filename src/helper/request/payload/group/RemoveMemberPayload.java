package helper.request.payload.group;

import helper.request.payload.BasePayload;

public class RemoveMemberPayload extends BasePayload{
	String groupName;
	String memberName;
	public RemoveMemberPayload() {
		super();
	}
	@Override
	public void setGroupName(String groupName) {
		// TODO Auto-generated method stub
		this.groupName = groupName;
	}
	@Override
	public void setMemberName(String memberName) {
		// TODO Auto-generated method stub
		this.memberName = memberName;
	}
	
	
}

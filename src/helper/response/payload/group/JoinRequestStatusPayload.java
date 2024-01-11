package helper.response.payload.group;
import java.util.List;

import helper.response.payload.BasePayload;
import models.join_model.JoinRequestStatus;

public class JoinRequestStatusPayload extends BasePayload {
	List<JoinRequestStatus> listOfAppliedGroups;
	public JoinRequestStatusPayload() {
		super();
	}
	@Override
	public void setJoinRequestStatus(List<JoinRequestStatus> listOfJoinRequests) {
		// TODO Auto-generated method stub
		super.setJoinRequestStatus(listOfJoinRequests);
		this.listOfAppliedGroups = listOfJoinRequests;
	}
}

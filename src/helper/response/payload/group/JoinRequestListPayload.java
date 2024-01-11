package helper.response.payload.group;
import java.util.List;

import helper.response.payload.BasePayload;
import models.join_model.JoinRequestList;

public class JoinRequestListPayload extends BasePayload {
	List<JoinRequestList> joinRequestList;
	@Override
	public void setJoinRequestList(List<JoinRequestList> listOfJoinRequests) {
		this.joinRequestList = listOfJoinRequests;
	}
}

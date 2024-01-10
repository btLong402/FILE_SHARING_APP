package helper.response.payload.group;
import java.util.ArrayList;
import java.util.List;

import helper.response.payload.BasePayload;
import models.join_model.ListOfApproval;
import models.join_model.ListOfJoinRequests;

public class JoinRequestListPayload extends BasePayload {
	List<ListOfApproval> listOfJoinRequests;
	@Override
	public void setListOfJoinRequests(List<ListOfApproval> listOfJoinRequests) {
		this.listOfJoinRequests = listOfJoinRequests;
	}
}

package helper.response.payload.group;
import java.util.ArrayList;
import java.util.List;

import helper.response.payload.BasePayload;
import models.join_model.ListOfJoinRequests;

public class JoinRequestListPayload extends BasePayload {
	List<ListOfJoinRequests> listOfJoinRequests = new ArrayList<ListOfJoinRequests>();
	@Override
	public void setListOfJoinRequests(List<ListOfJoinRequests> listOfJoinRequests) {
		this.listOfJoinRequests = listOfJoinRequests;
	}
}

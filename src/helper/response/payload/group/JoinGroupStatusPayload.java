package helper.response.payload.group;
import java.util.List;

import helper.response.payload.BasePayload;
import models.join_model.ListOfJoinRequests;

public class JoinGroupStatusPayload extends BasePayload {
	List<ListOfJoinRequests> listOfAppliedGroups;
	public JoinGroupStatusPayload() {
		super();
	}
	@Override
	public void setListOfJoinRequests(List<ListOfJoinRequests> listOfJoinRequests) {
		// TODO Auto-generated method stub
		super.setListOfJoinRequests(listOfJoinRequests);
		this.listOfAppliedGroups = listOfJoinRequests;
	}
	
}

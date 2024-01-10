package helper.response.payload.group;

import java.util.List;

import helper.response.payload.BasePayload;
import models.join_model.ListOfJoinRequests;

public class ListOfInvitationPayload extends BasePayload {
	List<ListOfJoinRequests> listOfInvitation;
	@Override
	public void setListOfInvitation(List<ListOfJoinRequests> listOfInvitation) {
		this.listOfInvitation = listOfInvitation;
	}
}

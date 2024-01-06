package helper.response.payload.group;

import java.util.ArrayList;
import java.util.List;

import helper.response.payload.BasePayload;
import models.join_model.ListOfInvitation;

public class ListOfInvitationPayload extends BasePayload {
	List<ListOfInvitation> listOfInvitation = new ArrayList<ListOfInvitation>();
	@Override
	public void setListOfInvitation(List<ListOfInvitation> listOfInvitation) {
		this.listOfInvitation = listOfInvitation;
	}
}

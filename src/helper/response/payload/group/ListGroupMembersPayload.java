package helper.response.payload.group;

import java.util.ArrayList;
import java.util.List;

import helper.response.payload.BasePayload;
import models.group_model.ListOfMembers;
public class ListGroupMembersPayload extends BasePayload {
	List<ListOfMembers> listOfMembers = new ArrayList<ListOfMembers>();
	@Override
	public void setListOfMembers(List<ListOfMembers> listOfMembers) {
		this.listOfMembers = listOfMembers;
	}
	
}

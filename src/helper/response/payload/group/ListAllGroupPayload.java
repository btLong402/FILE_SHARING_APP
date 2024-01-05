package helper.response.payload.group;

import java.util.List;

import helper.response.payload.BasePayload;
import models.group_model.GroupModel;

public class ListAllGroupPayload extends BasePayload{
	List<GroupModel> listGroups;

	@Override
	public void setListGroups(List<GroupModel> listGroups) {
		// TODO Auto-generated method stub
		super.setListGroups(listGroups);
		this.listGroups = listGroups;
	}
}

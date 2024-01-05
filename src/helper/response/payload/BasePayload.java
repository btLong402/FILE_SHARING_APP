package helper.response.payload;

import java.util.List;

import models.group_model.GroupModel;
import models.group_model.ListOfAppliedGroups;
import models.group_model.ListOfJoinRequests;

public abstract class BasePayload {
	public void setFileName(String fileName) {}
	public void setFileSize(long fileSize) {}
	public void setListGroups(List<GroupModel> listGroups) {}
	public void setListOfAppliedGroups(List<ListOfAppliedGroups> listOfAppliedGroups) {
	}

	public void setListOfJoinRequests(List<ListOfJoinRequests> listOfJoinRequests) {
		// TODO Auto-generated method stub
	}
}
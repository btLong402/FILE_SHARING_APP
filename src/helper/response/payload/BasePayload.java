package helper.response.payload;

import java.util.List;

import models.group_model.GroupModel;

public abstract class BasePayload {
	public void setFileName(String fileName) {}
	public void setFileSize(long fileSize) {}
	public void setListGroups(List<GroupModel> listGroups) {}
}
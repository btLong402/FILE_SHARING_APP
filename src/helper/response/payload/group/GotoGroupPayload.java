package helper.response.payload.group;

import java.util.ArrayList;
import java.util.List;

import helper.response.payload.BasePayload;

public class GotoGroupPayload extends BasePayload {
	List<String> folderName= new ArrayList<String>();
	List<String> fileName= new ArrayList<String>();
	@Override
	public void setFolderName(List<String> folderName) {
		this.folderName = folderName;
	}
	@Override
	public void setFileName(List<String> fileName) {
		this.fileName = fileName;
	}
}

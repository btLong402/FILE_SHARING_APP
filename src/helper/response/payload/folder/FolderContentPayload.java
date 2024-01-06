package helper.response.payload.folder;

import java.util.ArrayList;
import java.util.List;

import helper.response.payload.BasePayload;
import models.folder_model.FolderContentsModel;


public class FolderContentPayload extends BasePayload{
	List<FolderContentsModel> folderContents;
	@Override
	public void setFolderContents(List<FolderContentsModel> folderContents) {
		this.folderContents = folderContents;
	}
}

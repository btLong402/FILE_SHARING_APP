package helper.response.payload.folder;

import java.util.ArrayList;
import java.util.List;

import helper.response.payload.BasePayload;

public class GotoFolderPayload extends BasePayload {
	List<String> fileName= new ArrayList<String>();
	@Override
	public void setFileName(List<String> fileName) {
		this.fileName = fileName;
	}
}

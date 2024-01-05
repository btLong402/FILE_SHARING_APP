package helper.response.payload.folder;

import java.util.ArrayList;
import java.util.List;

import helper.response.payload.BasePayload;


public class FolderContentPayload extends BasePayload{
	List<String> files = new ArrayList<String>();
	@Override
	public void setFiles(List<String> files) {
		this.files = files;
	}
}

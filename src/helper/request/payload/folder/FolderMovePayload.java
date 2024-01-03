package helper.request.payload.folder;

import helper.request.payload.BasePayload;

public class FolderMovePayload extends BasePayload {
	String folderName;
	String fromGroup;
	String toGroup;

	@Override
	public void setFolderName(String folderName) {
		// TODO Auto-generated method stub
		super.setFolderName(folderName);
		this.folderName = folderName;
	}

	@Override
	public void from(String fromGroup) {
		// TODO Auto-generated method stub
		super.from(fromGroup);
		this.fromGroup = fromGroup;
	}

	@Override
	public void to(String toGroup) {
		// TODO Auto-generated method stub
		super.to(toGroup);
		this.toGroup = toGroup;
	}

}

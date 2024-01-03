package helper.request.payload.file;

import helper.request.payload.BasePayload;

public class FileCopyPayload extends BasePayload {
	String fileName;
	String fromGroup;
	String fromFolder;
	String toGroup;
	String toFolder;

	public FileCopyPayload() {
		super();
	}

	@Override
	public void from(String fromGroup, String fromFolder) {
		// TODO Auto-generated method stub
		this.fromGroup = fromGroup;
		this.fromFolder = fromFolder;
	}

	@Override
	public void to(String toGroup, String toFolder) {
		// TODO Auto-generated method stub
		this.toGroup = toGroup;
		this.toFolder = toFolder;
	}

	@Override
	public void setFileName(String fileName) {
		// TODO Auto-generated method stub
		this.fileName = fileName;
	}
}

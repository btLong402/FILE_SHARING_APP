package helper.response.payload.file;

import helper.response.payload.BasePayload;

public class DownloadFilePayload extends BasePayload{
	String fileName;
	long fileSize;
	@Override
	public void setFileName(String fileName) {
		// TODO Auto-generated method stub
		super.setFileName(fileName);
		this.fileName = fileName;
	}
	@Override
	public void setFileSize(long fileSize) {
		// TODO Auto-generated method stub
		super.setFileSize(fileSize);
		this.fileSize = fileSize;
	}
	
}

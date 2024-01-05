package helper.request.payload;

public abstract class BasePayload {

	public void setUserName(String userName) {
	}

	public void setPassword(String password) {
	}

	public void setFileName(String fileName) {
	}

	public void setGroupName(String groupName) {
	}

	public void setFolderName(String folderName) {
	}

	public void setFileSize(long fileSize) {
	}

	public void setMemberName(String memberName) {
	}

	public void setInvitedName(String invitedName) {
	}

	public void from(String fromGroup) {
	}

	public void from(String fromGroup, String fromFolder) {
	}

	public void to(String toGroup) {
	}

	public void to(String toGroup, String toFolder) {
	}

	public void setNewFileName(String newFileName) {
	}

	public void setNewFolderName(String newFolderName) {
	}
}

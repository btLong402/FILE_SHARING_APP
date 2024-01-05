package controllers.group_controller;

import java.util.List;

import db_access.group.Group_DAL;
import models.group_model.GroupModel;

public class GroupController {
	List<GroupModel> groupList;
	private Group_DAL db = new Group_DAL();

	public boolean createGroup(String userName, String groupName) {
		return db.createGroup(userName, groupName);

	}

	public boolean isAdmin(String userName, String groupName) {
		return db.checkIsAdmin(userName, groupName);
	}

	public List<GroupModel> listAllGroup() {
		return db.listAllGroup();
	}

	public boolean isMember(String userName, String groupName) {
		return db.checkIsMember(userName, groupName);
	}

	public boolean removeMember(String userName, String member, String groupName) {
		if (isAdmin(userName, groupName) && isMember(member, groupName)) {
			return db.removeMember(member, groupName);
		}
		return false;
	}

	public boolean inviteGroup(String userName, String invitedPerson, String groupName) {
		if (isMember(userName, groupName) && !isMember(invitedPerson, groupName)) {
			return db.inviteGroup(invitedPerson, groupName);
		}
		return false;
	}
}

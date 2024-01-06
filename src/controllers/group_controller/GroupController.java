package controllers.group_controller;

import java.util.List;

import db_access.group.Group_DAL;
import models.group_model.GroupModel;
import models.group_model.ListOfMembers;

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

	public boolean removeMember(String member, String groupName) {
		return db.removeMember(member, groupName);
	}

	public boolean leaveGroup(String userName, String groupName) {
		return db.removeMember(userName, groupName);
	}

	public boolean inviteGroup(String invitedPerson, String groupName) {
		return db.inviteGroup(invitedPerson, groupName);
	}

	public List<ListOfMembers> listMember(String groupName) {
		return db.listMember(groupName);
	}
}

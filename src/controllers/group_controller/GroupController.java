package controllers.group_controller;

import java.util.List;

import db_access.group.Group_DAL;
import models.group_model.GroupModel;

public class GroupController {
	List<GroupModel> groupList;
	private Group_DAL db = new Group_DAL();
	public boolean createGroup(String userName, String groupName) {
		if(db.createGroup(userName, groupName)) return true;
		return false;
	}
	public boolean isAdmin(String userName, String groupName) {
		if(db.checkIsAdmin(userName, groupName)) return true;
		return false;
	}
	
	public List<GroupModel> listAllGroup() {
		return db.listAllGroup();
	}
	
	public boolean isMember(String userName, String groupName) {
		if(db.checkIsMember(userName, groupName)) return true;
		return false;
	}
}

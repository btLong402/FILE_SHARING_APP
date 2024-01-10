package controllers.group_controller;

import java.util.List;

import db_access.group.Group_DAL;
import db_access.join.Join_DAL;
import models.group_model.GroupModel;
import models.group_model.ListOfMembers;
import models.join_model.ListOfApproval;
import models.join_model.ListOfJoinRequests;

public class GroupController {
	private Join_DAL j_db = new Join_DAL();
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
	
	public boolean requestJoin(String userName, String groupName) {
		return j_db.joinRequest(userName, groupName);
	}
	
	public boolean inviteTo(String userName, String groupName) {
		return j_db.joinInvitation(userName, groupName);
	}
	
	public boolean accept(String userName, String groupName) {
		return j_db.accept(userName, groupName);
	}
	
	public boolean denied(String userName, String groupName) {
		return j_db.denied(userName, groupName);
	}
	
	public List<ListOfJoinRequests> listRequestStatus(String userName){
		return j_db.joinRequestStatus(userName);
	}
	
	public List<ListOfJoinRequests> listInviteStatus(String userName){
		return j_db.joinRequestStatus(userName);
	}
	
	public List<ListOfApproval> listRequestList(String userName){
		return j_db.joinRequestList(userName);
	}
}

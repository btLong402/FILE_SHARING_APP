package helper.response.payload.group;
import java.util.ArrayList;
import java.util.List;

import helper.response.payload.BasePayload;
import models.group_model.ListOfAppliedGroups;

public class JoinGroupStatusPayload extends BasePayload {
	List<ListOfAppliedGroups> listOfAppliedGroups = new ArrayList<ListOfAppliedGroups>();
	public JoinGroupStatusPayload() {
		super();
	}
	@Override
	public void setListOfAppliedGroups(List<ListOfAppliedGroups> listOfAppliedGroups) {
		this.listOfAppliedGroups = listOfAppliedGroups;
	}
}

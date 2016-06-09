package nl.pts4.model;

import nl.pts4.controller.DatabaseController;

import java.util.List;
import java.util.UUID;

/**
 * Created by guushamm on 5-4-16.
 */
public class ChildAccountModel {
	List<AccountModel> parents;
	private UUID uuid;
	private String uniqueCode;
	private SchoolModel school;

	public ChildAccountModel(UUID uuid, String uniqueCode) {
		this.uuid = uuid;
		this.uniqueCode = uniqueCode;
	}

	public UUID getUuid() {
		return uuid;
	}

	public String getUniqueCode() {
		return uniqueCode;
	}

	public List<AccountModel> getParents() {
		if (parents == null) {
			parents = DatabaseController.getInstance().getParentFromChild(this);
		}
		return parents;

	}
}


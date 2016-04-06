package nl.pts4.model;

import java.util.UUID;

/**
 * Created by guushamm on 5-4-16.
 */
public class ChildModel {
	private UUID uuid;
	private int identifierNumber;
	private String uniqueCode;
	private SchoolModel school;
	private AccountModel parent;

	public ChildModel(UUID uuid, int identifierNumber, String uniqueCode, AccountModel parent) {
		this.uuid = uuid;
		this.identifierNumber = identifierNumber;
		this.uniqueCode = uniqueCode;
		this.parent = parent;
	}

	public void setParent(AccountModel parent) {
		this.parent = parent;
	}
}


package nl.pts4.model;

import java.util.UUID;

/**
 * Created by GuusHamm on 31-3-2016.
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

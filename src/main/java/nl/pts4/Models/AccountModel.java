package nl.pts4.models;

import java.util.UUID;

/**
 * Created by GuusHamm on 16-3-2016.
 */
public class AccountModel {

	private UUID uuid;
	private String oAuthKey;
	private OAuhtProviderEnum oAuhtProvider;
	private String name;
	private String email;
	private String hash;
	private String salt;
	private boolean active;
	private AccountTypeEnum accountTypeEnum;

	public enum AccountTypeEnum {
		customer,
		photographer
	}

	public enum OAuhtProviderEnum {
		google,
		facebook,
		twitter
	}

	public AccountModel(UUID uuid, String oAuthKey, OAuhtProviderEnum oAuhtProvider, String name, String email, String hash, String salt, boolean active, AccountTypeEnum accountTypeEnum) {
		this.uuid = uuid;
		this.oAuthKey = oAuthKey;
		this.oAuhtProvider = oAuhtProvider;
		this.name = name;
		this.email = email;
		this.hash = hash;
		this.salt = salt;
		this.active = active;
		this.accountTypeEnum = accountTypeEnum;
	}


	public UUID getUuid() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public boolean isActive() {
		return active;
	}

	public AccountTypeEnum getAccountTypeEnum() {
		return accountTypeEnum;
	}
}

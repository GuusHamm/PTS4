package nl.pts4.model;

import java.util.UUID;

/**
 * Created by GuusHamm on 16-3-2016.
 */
public class AccountModel {

    public static String DefaultTheme = "bootstrap.simplex.min.css";

    private UUID uuid;
    private String oAuthKey;
    private OAuthProviderEnum oAuhtProvider;
    private String name;
    private String email;
    private String hash;
    private boolean active;
    private AccountTypeEnum accountTypeEnum;
    private String theme;

    public AccountModel(UUID uuid, String oAuthKey, OAuthProviderEnum oAuthProvider, String name, String email, String hash, boolean active, AccountTypeEnum accountTypeEnum) {
        this.uuid = uuid;
        this.oAuthKey = oAuthKey;
        this.oAuhtProvider = oAuthProvider;
        this.name = name;
        this.email = email;
        this.hash = hash;
        this.active = active;
        this.accountTypeEnum = accountTypeEnum;
        this.theme = DefaultTheme;
    }

    public AccountModel(UUID uuid, String oAuthKey, OAuthProviderEnum oAuthProvider, String name, String email, String hash, boolean active, AccountTypeEnum accountTypeEnum, String theme) {
        this.uuid = uuid;
        this.oAuthKey = oAuthKey;
        this.oAuhtProvider = oAuthProvider;
        this.name = name;
        this.email = email;
        this.hash = hash;
        this.active = active;
        this.accountTypeEnum = accountTypeEnum;
        this.theme = theme;
    }

    public AccountModel(UUID uuid, String name, String email, String hash, boolean active, AccountTypeEnum accountTypeEnum) {
        this.uuid = uuid;
        this.oAuthKey = "";
        this.oAuhtProvider = null;
        this.name = name;
        this.email = email;
        this.hash = hash;
        this.active = active;
        this.accountTypeEnum = accountTypeEnum;
        this.theme = DefaultTheme;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public AccountTypeEnum getAccountTypeEnum() {
        return accountTypeEnum;
    }

    public void setAccountTypeEnum(AccountTypeEnum accountTypeEnum) {
        this.accountTypeEnum = accountTypeEnum;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setoAuthKey(String oAuthKey) {
        this.oAuthKey = oAuthKey;
    }

    public void setoAuhtProvider(OAuthProviderEnum oAuhtProvider) {
        this.oAuhtProvider = oAuhtProvider;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public enum AccountTypeEnum {
        customer,
        photographer,
        administrator
    }

    public enum OAuthProviderEnum {
        google,
        facebook,
        twitter
    }
}

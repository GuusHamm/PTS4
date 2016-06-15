package nl.pts4.model;

import nl.pts4.controller.DatabaseController;

import java.util.UUID;

public class LinkModel {

    private String key;
    private String link;
    private AccountModel authorizedUser;

    public LinkModel(String key, String link) {
        this(key, link, null);
    }

    public LinkModel(String key, String link, AccountModel authorizedUser) {
        this.key = key;
        this.link = link;
        this.authorizedUser = authorizedUser;
    }

    public String getKey() {
        return key;
    }

    public String getLink() {
        return link;
    }

    public AccountModel getAuthorizedUser() {
        return authorizedUser;
    }

    public static LinkModel newLinkModel(String redirect, AccountModel authorizedUser) {
        UUID uuid = UUID.randomUUID();
        LinkModel lm = new LinkModel(uuid.toString(), redirect, authorizedUser);
        DatabaseController.getInstance().createLinkModel(lm);
        return lm;
    }
}
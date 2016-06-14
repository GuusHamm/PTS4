package nl.pts4.model;

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
}

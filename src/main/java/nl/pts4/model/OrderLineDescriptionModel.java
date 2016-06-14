package nl.pts4.model;

/**
 * Created by wouter on 8-6-2016.
 */

/**
 * This class is purely used to add data to an email and will not be added to the database, DO NOT USE THIS FOR ANY OTHER PURPOSE!
 */
public class OrderLineDescriptionModel {
    String description;
    String amount;
    String digitalDownloadLink;
    boolean shouldGetDigitalDownload =false;


    public boolean isShouldGetDigitalDownload() {
        return shouldGetDigitalDownload;
    }

    public void setShouldGetDigitalDownload(boolean shouldGetDigitalDownload) {
        this.shouldGetDigitalDownload = shouldGetDigitalDownload;
    }

    public String getDigitalDownloadLink() {
        return digitalDownloadLink;
    }

    public void setDigitalDownloadLink(String digitalDownloadLink) {
        this.digitalDownloadLink = "http://pts4.guushamm.tech/resources/"+digitalDownloadLink;
    }

    public OrderLineDescriptionModel() {
    }

    public OrderLineDescriptionModel(String description) {
        this.description = description;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAmount() {
        return amount;
    }
}

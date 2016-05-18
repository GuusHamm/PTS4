package nl.pts4.model;

import java.util.Date;
import java.util.UUID;

/**
 * Created by GuusHamm on 16-3-2016.
 */
public class PhotoModel {
    private UUID uuid;
    private AccountModel photographer;
    private AccountModel child;
    private SchoolModel school;
    private double price;
    private Date captureDate;
    private String filePath;
    private String filePathLowRes;

//    public PhotoModel(UUID uuid, AccountModel photographer, AccountModel child, SchoolModel school, double price, Date captureDate, String filePath) {
//        this.uuid = uuid;
//        this.photographer = photographer;
//        this.child = child;
//        this.school = school;
//        this.price = price;
//        this.captureDate = captureDate;
//        this.filePath = filePath;
//        this.filePathLowRes = "";
//    }

    public PhotoModel(UUID uuid, AccountModel photographer, AccountModel child, SchoolModel school, double price, Date captureDate, String filePath, String lowResPath) {
        this.uuid = uuid;
        this.photographer = photographer;
        this.child = child;
        this.school = school;
        this.price = price;
        this.captureDate = captureDate;
        this.filePath = filePath;
        this.filePathLowRes = lowResPath;
    }

    public double getPrice() {
        return price;
    }

    public UUID getUuid() {
        return uuid;
    }

    public AccountModel getPhotographer() {
        return photographer;
    }

    public AccountModel getChild() {
        return child;
    }

    public SchoolModel getSchool() {
        return school;
    }

    public Date getCaptureDate() {
        return captureDate;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFilePathLowRes() {return filePathLowRes; }

    public double getRoundedPrice() {
        return (double) price - .01;
    }
}

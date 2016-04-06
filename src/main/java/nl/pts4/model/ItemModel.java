package nl.pts4.model;

import java.util.UUID;

/**
 * Created by GuusHamm on 16-3-2016.
 */
public class ItemModel {
    private int id;
    private double price;
    private String type;
    private String description;
    private String thumbnailPath;

    public ItemModel(int id, double price, String type, String description, String thumbnailPath) {
        this.id = id;
        this.price = price;
        this.type = type;
        this.description = description;
        this.thumbnailPath = thumbnailPath;
    }


    public int getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }
}

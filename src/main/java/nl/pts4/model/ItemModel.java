package nl.pts4.model;

import java.util.UUID;

/**
 * Created by GuusHamm on 16-3-2016.
 */
public class ItemModel {
    private UUID uuid;
    private double price;
    private String type;
    private String description;
    private String thumbnailPath;

    public ItemModel(UUID uuid, double price, String type, String description, String thumbnailPath) {
        this.uuid = uuid;
        this.price = price;
        this.type = type;
        this.description = description;
        this.thumbnailPath = thumbnailPath;
    }

    public ItemModel() {
    }

    public UUID getUuid() {

        return uuid;
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

package nl.pts4.model;

/**
 * Created by GuusHamm on 16-3-2016.
 */
public class EffectModel {
    private int id;
    private String type;
    private String description;
    private int price;

    public EffectModel(int id, String type, String description, int price) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.price = price;
    }

    //region getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    //endregion
}

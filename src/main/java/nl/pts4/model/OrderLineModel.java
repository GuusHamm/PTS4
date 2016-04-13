package nl.pts4.model;

import nl.pts4.controller.DatabaseController;

/**
 * Created by Nekkyou on 16-3-2016.
 */
public class OrderLineModel {
    private int id;
    private int amount;
    private PhotoConfigurationModel photoConfiguration;

    public OrderLineModel(int id, int amount, int photoConfigItemId) {
        this.id = id;
        this.amount = amount;
        this.photoConfiguration = DatabaseController.getInstance().getPhotoConfigurationModelById(photoConfigItemId);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setPhoto(int photoID) {
//        this.photo = photoController.getPhotoConfigItemById(photoID);
    }

    public PhotoConfigurationModel getPhotoConfiguration() {
        return photoConfiguration;
    }
}

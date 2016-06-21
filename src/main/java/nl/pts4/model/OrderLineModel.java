package nl.pts4.model;

import nl.pts4.controller.DatabaseController;

/**
 * Created by Nekkyou on 16-3-2016.
 */
public class OrderLineModel {
    private int id;
    private int amount;
    private PhotoConfigurationModel photoConfiguration;
    private int orderid;

    public OrderLineModel(int id, int amount, int photoConfigItemId) {
        this.id = id;
        this.amount = amount;
        this.photoConfiguration = DatabaseController.getInstance().getPhotoConfigurationModelById(photoConfigItemId);
    }

    public OrderLineModel(int id, int amount, int photoConfiguration, int orderid) {
        this.id = id;
        this.amount = amount;
        this.photoConfiguration = DatabaseController.getInstance().getPhotoConfigurationModelById(photoConfiguration);
        this.orderid = orderid;
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

    public void setPhotoConfiguration(PhotoConfigurationModel photoConfiguration) {
        this.photoConfiguration = photoConfiguration;
    }

    public int getOrderid() {
        return orderid;
    }

    public void setOrderid(int orderid) {
        this.orderid = orderid;
    }

    public PhotoConfigurationModel getPhotoConfiguration() {
        return photoConfiguration;
    }
}

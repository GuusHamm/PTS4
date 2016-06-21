package nl.pts4.model;

import java.util.Date;
import java.util.List;

/**
 * Created by Nekkyou on 16-3-2016.
 */
public class OrderModelWithPhoto extends OrderModel {

    private List<PhotoModel> photo;

    public OrderModelWithPhoto(int id, Date orderDate, AccountModel account) {
        super(id, orderDate, account);
    }

    public OrderModelWithPhoto(int id, Date orderDate, AccountModel account, List<OrderLineModel> orderLineModels) {
        super(id, orderDate, account, orderLineModels);
    }

    public OrderModelWithPhoto(OrderModel om) {
        this(om.getId(), om.getOrderDate(), om.getAccount(), om.getOrderLineModels());
    }

    public OrderModelWithPhoto(OrderModel om, List<PhotoModel> pm) {
        this(om.getId(), om.getOrderDate(), om.getAccount(), om.getOrderLineModels());
        this.photo = pm;
    }

    public List<PhotoModel> getPhoto() {
        return photo;
    }

    public void setPhoto(List<PhotoModel> photo) {
        this.photo = photo;
    }
}
package nl.pts4.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Nekkyou on 16-3-2016.
 */
public class OrderModel {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private int id;
    private Date orderDate;
    //TODO replace with an account
    private AccountModel account;
    private List<OrderLineModel> orderLineModels;

    public OrderModel(int id, Date orderDate, AccountModel account) {
        this.id = id;
        this.orderDate = orderDate;
        this.account = account;
        this.orderLineModels = new ArrayList<>();
    }

    public OrderModel(int id, Date orderDate, AccountModel account, List<OrderLineModel> orderLineModels) {
        this.id = id;
        this.orderDate = orderDate;
        //TODO account pakken met Account id
        this.account = account;
        this.orderLineModels = orderLineModels;
    }

    public AccountModel getAccount() {
        return account;
    }

    public void setAccount(AccountModel account) {
        this.account = account;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<OrderLineModel> getOrderLineModels() {
        return orderLineModels;
    }

    public void setOrderLineModels(List<OrderLineModel> orderLineModels) {
        this.orderLineModels = orderLineModels;
    }


    public int getTotalPrice() {
        int totalPrice = 0;
        if (orderLineModels == null) return 0;
        for (OrderLineModel olm : orderLineModels) {
            if (olm.getPhotoConfiguration() == null) continue;
            totalPrice += olm.getPhotoConfiguration().getTotalPrice();
        }
        return totalPrice;
    }

    public String getOrderDateTemplate() {
        return sdf.format(getOrderDate());
    }
}

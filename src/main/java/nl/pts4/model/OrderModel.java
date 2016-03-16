package nl.pts4.model;

import java.util.Date;

/**
 * Created by Nekkyou on 16-3-2016.
 */
public class OrderModel
{
    private int id;
    private Date orderDate;
    //TODO replace with an account
    private AccountModel account;

    public OrderModel(int id,Date orderDate, AccountModel accountID) {
        this.id = id;
        this.orderDate = orderDate;
        //TODO account pakken met Account id
        this.account = account;
        //        this.account = accountController.getAccount(accountID)
    }

    public AccountModel getAccount()
    {
        return account;
    }

    public void setAccount(AccountModel account)
    {
        this.account = account;
    }

    public Date getOrderDate()
    {
        return orderDate;
    }

    public void setOrderDate(Date orderDate)
    {
        this.orderDate = orderDate;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }
}

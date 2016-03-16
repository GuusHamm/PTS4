package nl.pts4.Models;

import nl.pts4.Account;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Nekkyou on 16-3-2016.
 */
public class OrderModel
{
    private int id;
    private Date orderDate;
    //TODO replace with an account
    private Account account;
    //TODO replace with a PhotoConfiguration object (if we'll make one of those
    private ArrayList<OrderLineModel> orderLines;

    public OrderModel(int id, Date orderDate, int accountID, ArrayList<OrderLineModel> orderLines) {
        this.id = id;
        this.orderDate = orderDate;
        //TODO account pakken met Account id
        this.account = new Account();
        //        this.account = accountController.getAccount(accountID)
        //TODO Get the orderlines correctly
        this.orderLines = orderLines;
    }

    public Account getAccount()
    {
        return account;
    }

    public void setAccount(Account account)
    {
        this.account = account;
    }

    public ArrayList<OrderLineModel> getOrderLines()
    {
        return orderLines;
    }

    public void setOrderLines(ArrayList<OrderLineModel> orderLines)
    {
        this.orderLines = orderLines;
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

package nl.pts4;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Nekkyou on 16-3-2016.
 */
public class Order
{
    private int id;
    private Date orderDate;
    //TODO replace with an account
    private Account account;
    //TODO replace with a PhotoConfiguration object (if we'll make one of those
    private ArrayList<OrderLine> orderLines;

    public Order(int id, Date orderDate, int accountID, ArrayList<OrderLine> orderLines) {
        this.id = id;
        this.orderDate = orderDate;
        //TODO account pakken met Account id
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

    public ArrayList<OrderLine> getOrderLines()
    {
        return orderLines;
    }

    public void setOrderLines(ArrayList<OrderLine> orderLines)
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

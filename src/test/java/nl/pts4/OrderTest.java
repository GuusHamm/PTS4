package nl.pts4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by Nekkyou on 16-3-2016.
 */
public class OrderTest
{
    private Order order;
    private int orderID;
    private Date orderDate;
    private Account account;
    private ArrayList<OrderLine> orderLines;

    @Before
    public void setUp() throws Exception
    {
        orderID = 1;
        orderDate = new Date(1,1, 1970);
        account = new Account();
        orderLines = new ArrayList<>();
        //I'm using 1 here because we use the ID of the account to get the actual account
        order = new Order(orderID, orderDate, 1, orderLines);
    }

    @Test
    public void testGetAccount() throws Exception
    {
        //Check if the accounts are the same by ID, IDs should be unique
        Assert.assertTrue("Order - Accounts ID do not match!", (order.getAccount().getId()) == account.getId());
    }

    @Test
    public void testSetAccount() throws Exception
    {
        Account account2 = new Account();
        order.setAccount(account2);
        //Check if the ID matches the ID of the new Account
        Assert.assertTrue("Order - Accounts ID do not match after seting a new Account!", (order.getAccount().getId()) == account2.getId());
    }

    @Test
    public void testGetOrderLines() throws Exception
    {
        //Check if the Order lines are equal
        Assert.assertTrue("Order - Order lines do not match", (order.getOrderLines().equals(orderLines)));
    }

    @Test
    public void testSetOrderLines() throws Exception
    {
        //Create a new List of orderlines and set it.
        ArrayList<OrderLine> orderLines2 = new ArrayList<>();
        orderLines2.add(new OrderLine(1,1));
        order.setOrderLines(orderLines2);
        //Check if it is indeed set correctly
        Assert.assertTrue("Order - Order lines do not match", (order.getOrderLines().equals(orderLines2)));
    }

    @Test
    public void testGetOrderDate() throws Exception
    {
        Assert.assertTrue("Order - Order data does not match", (order.getOrderDate().compareTo(orderDate) == 0));
    }

    @Test
    public void testSetOrderDate() throws Exception
    {
        //Create a new date
        Date date2 = new Date(2,2,2012);
        order.setOrderDate(date2);

        Assert.assertTrue("Order - Order data does not match", (order.getOrderDate().compareTo(date2) == 0));
    }

    @Test
    public void testGetId() throws Exception
    {
        Assert.assertTrue("Order - Order id does not match", order.getId() == orderID);
    }

    @Test
    public void testSetId() throws Exception
    {
        int orderID2 = 2;
        order.setId(orderID2);

        Assert.assertTrue("Order - Order id does not match", order.getId() == orderID2);

    }
}
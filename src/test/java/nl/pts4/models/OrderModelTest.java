//package nl.pts4.models;
//
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.ArrayList;
//import java.util.Date;
//
///**
// * Created by Nekkyou on 16-3-2016.
// */
//public class OrderModelTest
//{
//    private OrderModel orderModel;
//    private int orderID;
//    private Date orderDate;
//    private AccountModel account;
//    private ArrayList<OrderLineModel> orderLines;
//
//    @Before
//    public void setUp() throws Exception
//    {
//        orderID = 1;
//        orderDate = new Date(1,1, 1970);
//        account = new AccountModel();
//        orderLines = new ArrayList<>();
//        //I'm using 1 here because we use the ID of the account to get the actual account
//        orderModel = new OrderModel(orderID, orderDate, null);
//    }
//
//    @Test
//    public void testGetAccount() throws Exception
//    {
//        //Check if the accounts are the same by ID, IDs should be unique
//        Assert.assertTrue("OrderModel - Accounts ID do not match!", (orderModel.getAccount().getUuid()) == account.getId());
//    }
//
//    @Test
//    public void testSetAccount() throws Exception
//    {
//        AccountModel account2 = new AccountModel();
//        orderModel.setAccount(account2);
//        //Check if the ID matches the ID of the new Account
//        Assert.assertTrue("OrderModel - Accounts ID do not match after seting a new Account!", (orderModel.getAccount().getId()) == account2.getId());
//    }
//
//    @Test
//    public void testGetOrderLines() throws Exception
//    {
//        //Check if the OrderModel lines are equal
//        Assert.assertTrue("OrderModel - OrderModel lines do not match", (orderModel.getOrderLines().equals(orderLines)));
//    }
//
//    @Test
//    public void testSetOrderLines() throws Exception
//    {
//        //Create a new List of orderlines and set it.
//        ArrayList<OrderLineModel> orderLines2 = new ArrayList<>();
//        orderLines2.add(new OrderLineModel());
//        orderModel.setOrderLines(orderLines2);
//        //Check if it is indeed set correctly
//        Assert.assertTrue("OrderModel - OrderModel lines do not match", (orderModel.getOrderLines().equals(orderLines2)));
//    }
//
//    @Test
//    public void testGetOrderDate() throws Exception
//    {
//        Assert.assertTrue("OrderModel - OrderModel data does not match", (orderModel.getOrderDate().compareTo(orderDate) == 0));
//    }
//
//    @Test
//    public void testSetOrderDate() throws Exception
//    {
//        //Create a new date
//        Date date2 = new Date(2,2,2012);
//        orderModel.setOrderDate(date2);
//
//        Assert.assertTrue("OrderModel - OrderModel data does not match", (orderModel.getOrderDate().compareTo(date2) == 0));
//    }
//
//    @Test
//    public void testGetId() throws Exception
//    {
//        Assert.assertTrue("OrderModel - OrderModel id does not match", orderModel.getId() == orderID);
//    }
//
//    @Test
//    public void testSetId() throws Exception
//    {
//        int orderID2 = 2;
//        orderModel.setId(orderID2);
//
//        Assert.assertTrue("OrderModel - OrderModel id does not match", orderModel.getId() == orderID2);
//
//    }
//}
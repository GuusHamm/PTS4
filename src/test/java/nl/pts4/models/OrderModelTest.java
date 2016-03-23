package nl.pts4.models;

import com.lambdaworks.crypto.SCryptUtil;
import nl.pts4.model.AccountModel;
import nl.pts4.model.OrderLineModel;
import nl.pts4.model.OrderModel;
import nl.pts4.security.HashConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Nekkyou on 16-3-2016.
 */
public class OrderModelTest
{
    private OrderModel orderModel;
    private OrderModel orderModel1;
    private OrderModel orderModel2;
    private int orderID;
    private Date orderDate;
    private AccountModel account;
    private ArrayList<OrderLineModel> orderLines;
    private ArrayList<OrderLineModel> orderLines1;

    @Before
    public void setUp() throws Exception
    {
        orderID = 1;
        orderDate = new Date(1,1, 1970);
        account = new AccountModel(UUID.randomUUID(), "Tim", "t.daniels@student.fontys.nl", SCryptUtil.scrypt("hallo", HashConstants.N, HashConstants.r, HashConstants.p), true, AccountModel.AccountTypeEnum.customer);
        orderLines = new ArrayList<>();
        orderLines1 = new ArrayList<>();
        orderModel = new OrderModel(orderID, orderDate, account);
        orderModel1 = new OrderModel(orderID, orderDate, account, orderLines);
        orderLines1.add(new OrderLineModel(1, 1, 1));
        orderModel2 = new OrderModel(orderID, orderDate, account, orderLines1);

    }

    @Test
    public void testGetAccount() throws Exception
    {
        //Check if the accounts are the same by ID, IDs should be unique
        Assert.assertTrue("OrderModel - Accounts ID do not match!", (orderModel.getAccount().getUuid()) == account.getUuid());
    }

    @Test
    public void testSetAccount() throws Exception
    {
        AccountModel account2 = new AccountModel(UUID.randomUUID(), "Timmy", "iets@student.fontys.nl", SCryptUtil.scrypt("password1", HashConstants.N, HashConstants.r, HashConstants.p), true, AccountModel.AccountTypeEnum.customer);
        orderModel.setAccount(account2);
        //Check if the ID matches the ID of the new Account
        Assert.assertTrue("OrderModel - Accounts ID do not match after seting a new Account!", (orderModel.getAccount().getUuid()) == account2.getUuid());
    }


    @Test
    public void testGetOrderDate() throws Exception
    {
        Assert.assertTrue("OrderModel - OrderModel data does not match", (orderModel.getOrderDate().compareTo(orderDate) == 0));
    }

    @Test
    public void testSetOrderDate() throws Exception
    {
        //Create a new date
        Date date2 = new Date(2,2,2012);
        orderModel.setOrderDate(date2);

        Assert.assertTrue("OrderModel - OrderModel data does not match", (orderModel.getOrderDate().compareTo(date2) == 0));
    }

    @Test
    public void testGetId() throws Exception
    {
        Assert.assertTrue("OrderModel - OrderModel id does not match", orderModel.getId() == orderID);
    }

    @Test
    public void testSetId() throws Exception
    {
        int orderID2 = 2;
        orderModel.setId(orderID2);

        Assert.assertTrue("OrderModel - OrderModel id does not match", orderModel.getId() == orderID2);
    }

    @Test
    public void testGetOrderLineModels() throws Exception
    {
        Assert.assertTrue("OrderModel - OrderModel order lines does not match", orderModel1.getOrderLineModels().equals(orderLines));
    }

    @Test
    public void testSetOrderLineModels() throws Exception {
        ArrayList<OrderLineModel> newOrderLines = new ArrayList<>();

        Assert.assertFalse("OrderModel - OrderModel set order lines fails", orderModel.getOrderLineModels().equals(newOrderLines));
        orderModel.setOrderLineModels(newOrderLines);
        Assert.assertTrue("OrderModel - OrderModel set order lines fails", orderModel.getOrderLineModels().equals(newOrderLines));
    }

    @Test
    public void testGetTotalPrice() throws Exception {
        Assert.assertTrue("OrderModel - OrderModel total price does not match expected price", orderModel1.getTotalPrice() == 0);
        Assert.assertTrue("OrderModel - OrderModel total price does not match expected price", orderModel2.getTotalPrice() == 5);
    }

}
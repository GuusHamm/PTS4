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
    private int orderID;
    private Date orderDate;
    private AccountModel account;
    private ArrayList<OrderLineModel> orderLines;

    @Before
    public void setUp() throws Exception
    {
        orderID = 1;
        orderDate = new Date(1,1, 1970);
        account = new AccountModel(UUID.randomUUID(), "Tim", "t.daniels@student.fontys.nl", SCryptUtil.scrypt("hallo", HashConstants.N, HashConstants.r, HashConstants.p), true, AccountModel.AccountTypeEnum.customer);
        orderLines = new ArrayList<>();
        //I'm using 1 here because we use the ID of the account to get the actual account
        orderModel = new OrderModel(orderID, orderDate, account);
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
}
package nl.pts4.controller;

import com.lambdaworks.crypto.SCryptUtil;
import nl.pts4.model.AccountModel;
import nl.pts4.security.HashConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Teun on 23-3-2016.
 */
public class AccountControllerTest {

    private DatabaseController databaseController;

    @Before
    public void before() throws Exception {
        databaseController = DatabaseController.getTestInstance();
        databaseController.createTables();
    }

    @Test
    public void checkPassword() throws Exception {
        String hash = SCryptUtil.scrypt("password", HashConstants.N, HashConstants.R, HashConstants.P);
        AccountModel accountModel = databaseController.getAccount("njones0@amazonaws.com");
        accountModel.setHash(hash);

        Assert.assertTrue(AccountController.checkPassword(accountModel, "password"));
        Assert.assertFalse(AccountController.checkPassword(accountModel, "password1"));
        Assert.assertFalse(AccountController.checkPassword(accountModel, null));
    }


}
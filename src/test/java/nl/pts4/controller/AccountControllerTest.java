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
    }

    @Test
    public void checkPassword() throws Exception {
        String hash = SCryptUtil.scrypt("password", HashConstants.N, HashConstants.r, HashConstants.p);
        AccountModel am = databaseController.getAccount("njones0@amazonaws.com");
        am.setHash(hash);

        Assert.assertTrue(AccountController.checkPassword(am, "password"));
        Assert.assertFalse(AccountController.checkPassword(am, "password1"));
        Assert.assertFalse(AccountController.checkPassword(am, null));
    }


}
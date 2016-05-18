package nl.pts4.models;

import com.lambdaworks.crypto.SCryptUtil;
import nl.pts4.FotowinkelSpringApplication;
import nl.pts4.controller.DatabaseController;
import nl.pts4.model.AccountModel;
import nl.pts4.model.AccountRestModel;
import nl.pts4.security.HashConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(FotowinkelSpringApplication.class)
@ContextConfiguration("classpath*:WEB-INF/spring-servlet.xml")
public class AccountRestModelTest {

    private AccountRestModel arm;

    @Autowired
    private MessageSource ms;

    @Before
    public void before() {
        arm = new AccountRestModel("test@email.com", "testpassword", Locale.ENGLISH, ms);

        DatabaseController.getTestInstance().createTables();
    }

    @Test
    public void emailTest() {
        arm = new AccountRestModel("", "testpassword", Locale.ENGLISH, ms);
        arm = new AccountRestModel("test.noemail.com", "testpassword", Locale.ENGLISH, ms);
    }

    @Test
    public void passwordTest() {
        arm = new AccountRestModel("test@email.com", null, Locale.ENGLISH, ms);
        arm = new AccountRestModel("test@email.com", "", Locale.ENGLISH, ms);
        arm = new AccountRestModel("test@email.com", "as", Locale.ENGLISH, ms);
    }

    @Test
    public void testAccountValidation() {
        String password = "testpassword";
        AccountModel ac = DatabaseController.getTestInstance().getRandomAccount();
        ac.setHash(SCryptUtil.scrypt(password, HashConstants.N, HashConstants.R, HashConstants.P));
        arm = new AccountRestModel(ac, password, Locale.ENGLISH, ms);

        assertTrue(arm.isSuccess());
    }

}

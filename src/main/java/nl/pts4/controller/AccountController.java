package nl.pts4.controller;

import com.lambdaworks.crypto.SCryptUtil;
import nl.pts4.model.AccountModel;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Teun on 16-3-2016.
 */
@Controller
public class AccountController {

    public static final String AccountCookie = "ACC_SESSION";
    public static final String AccountModelKey = "user";

    public static final String CSRFToken = "CSRF";
    public static final int CSRFExpiry = 60 * 10; // 10 Minutes

    private Map<String, Date> tokens = new HashMap<>();
    private SecureRandom random = new SecureRandom();

    /**
     * When you go the the delete page with a get method
     * @param m        : The model
     * @param request  : The request made
     * @param response : The response given
     * @param cookie   : The current cookie
     * @return delete to get the correct template
     */
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public String deleteGet(Model m, HttpServletRequest request, HttpServletResponse response, @CookieValue(value = AccountCookie) String cookie) {
        m.addAttribute(MainController.TITLE_ATTRIBUTE, "Delete!");

        String randomToken = new BigInteger(130, random).toString(32);
        tokens.put(randomToken, new Date());
        m.addAttribute(CSRFToken, randomToken);
        return "delete";
    }

    /**
     * When you go to the delete page with a post method
     * @param m             : The model
     * @param request       : The request made
     * @param response      : The response given
     * @param accountCookie : The cookie from the account
     * @param password      : The password to delete the account
     * @param csrfToken     : The token to prevent CSRF (Cross side request forgery)
     * @return delete to get the correct template
     * @throws IllegalArgumentException : when response isn't good
     * @throws IOException : When response isn't good
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String delete(Model m,
                         HttpServletRequest request,
                         HttpServletResponse response,
                         @CookieValue(AccountCookie) String accountCookie,
                         @RequestParam("password") String password,
                         @RequestParam(CSRFToken) String csrfToken) throws IllegalArgumentException, IOException {
        final String ERROR_ATTRIBUTE = "error";
        m.addAttribute(MainController.TITLE_ATTRIBUTE, "Account deleted");
        if (!tokens.containsKey(csrfToken))
            m.addAttribute(ERROR_ATTRIBUTE, "Token not found or expired");

        Date tokenDate = tokens.get(csrfToken);
        if (isExpired(tokenDate))
            m.addAttribute(ERROR_ATTRIBUTE, "Token not found expired");

        AccountModel am = DatabaseController.getInstance().getAccountByCookie(accountCookie);
        if (!checkPassword(am, password))
            m.addAttribute(ERROR_ATTRIBUTE, "Password invalid");

        if (!m.containsAttribute(ERROR_ATTRIBUTE)) {
            DatabaseController.getInstance().deleteAccount(am.getUuid());
            Cookie cookie = new Cookie(AccountCookie, "");
            cookie.setMaxAge(0);
            response.sendRedirect("/");
        }

        tokens.remove(csrfToken);
        return "delete";
    }

    /**
     * Checks if date is later than date + CSRFExpiry
     * @param date Date to check
     * @return True if date is earlier than date + CSRFExpiry, false otherwise
     */
    private boolean isExpired(Date date) {
        Date now = new Date();
        Date expiry = new Date(date.getTime());
        expiry.setTime(expiry.getTime() + AccountController.CSRFExpiry);
        return now.before(expiry);
    }

    /**
     * Going to the login page
     * @param m : The model / template
     * @return login to get the correct template
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model m){
        m.addAttribute(MainController.TITLE_ATTRIBUTE, "Login");
        return "login";
    }

    /**
     * Going to the register page
     * @param m : The model / template
     * @return register to get the register template
     */
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String register(Model m) {
        m.addAttribute(MainController.TITLE_ATTRIBUTE, "Register");
        return "register";
    }

    /**
     * Actualle registering an account
     * @param email     : The email used when registering, has to be unqiue
     * @param password  : Password for the account, minimal 8 characters, maximal 32 characters
     * @param name      : the username for the account
     * @param response  : The response from the server
     * @param m         : The model / template
     * @return Null
     * @throws IOException : When the input / output is incorrect
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(@RequestParam(value = "email", required = true) String email,
                           @RequestParam(value = "password", required = true) String password,
                           @RequestParam(value = "name", required = true) String name,
                           HttpServletResponse response,
                           Model m) throws IOException {
        String sanitizedName = Jsoup.clean(name, Whitelist.simpleText());
        String sanitizedEmail = Jsoup.clean(email, Whitelist.simpleText());
        String sanitizedPassword = Jsoup.clean(password, Whitelist.simpleText());

        boolean accountExists = DatabaseController.getInstance().getAccount(sanitizedEmail) != null;
        if (accountExists)
            response.sendRedirect("/register");

        AccountModel accountModel = DatabaseController.getInstance().createAccount(sanitizedName, sanitizedEmail, sanitizedPassword);
        m.addAttribute(MainController.TITLE_ATTRIBUTE, "Register");
        response.sendRedirect("/login");
        return null;
    }

    /**
     * Get the account settings page
     * @param account   : The account which is logged in
     * @param m         : The model / template
     * @param response  : The response from the server
     * @return account_settings to get the correct template
     * @throws IOException : When input / output is incorrect
     */
    @RequestMapping(value = "/account/settings", method = RequestMethod.GET)
    public String accountSettingsGet(@CookieValue(value = AccountCookie) String account,
                                     Model m,
                                     HttpServletResponse response) throws IOException {
        AccountModel accountModel = DatabaseController.getInstance().getAccountByCookie(account);
        m.addAttribute(MainController.TITLE_ATTRIBUTE, "Account Settings");
        if (accountModel == null)
            response.sendRedirect("/");
        m.addAttribute(AccountController.AccountModelKey, accountModel);
        return "account_settings";
    }

    /**
     * Logging out, has to be logged in first to log out.
     * @param accountCookie : The cookie which contains the account
     * @param m             : The model / template
     * @param response      : The response from the server
     * @return to login screen.
     */
    @RequestMapping(value = "/logout")
    public String accountLogout(@CookieValue(value = AccountCookie, required = false) String accountCookie,
                                Model m,
                                HttpServletResponse response) {
        if (accountCookie != null) {
            Cookie cookie = new Cookie(AccountCookie, null);
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }

        m.addAttribute(MainController.TITLE_ATTRIBUTE, "Logout");
        return "login";
    }

    /**
     * Checks the password for a account
     * @param account Account to check the password for
     * @param password Password to check against
     * @return True if password is correct, false otherwise
     */
    public static boolean checkPassword(AccountModel account, String password) {
        return !(password == null || Objects.equals(password, "") || account == null) && SCryptUtil.check(password, account.getHash());
    }
}

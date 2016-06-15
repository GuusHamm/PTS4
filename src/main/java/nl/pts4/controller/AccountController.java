package nl.pts4.controller;

import com.lambdaworks.crypto.SCryptUtil;
import nl.pts4.model.AccountModel;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

import static nl.pts4.controller.MainController.ERROR_ATTRIBUTE;

/**
 * @author Teun
 */
@Controller
public class AccountController {

    public static final String AccountModelKey = "user";
    private final String CSRFToken = "CSRF";


    @Autowired
    private MessageSource messageSource;


    /**
     * Checks the password for a account
     *
     * @param account  Account to check the password for
     * @param password Password to check against
     * @return True if password is correct, false otherwise
     */
    public static boolean checkPassword(AccountModel account, String password) {
        return !(password == null || Objects.equals(password, "") || account == null) && SCryptUtil.check(password, account.getHash());
    }

    /**
     * When you go the the delete page with a get method
     *
     * @param m       : The model
     * @param request : The request made
     * @return delete to get the correct template
     */
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public String deleteGet(Model m, HttpServletRequest request, HttpServletResponse response) {
        m.addAttribute(MainController.TITLE_ATTRIBUTE, "Delete!");

        m = MainController.addDefaultAttributesToModel(m, "Delete", request, response);

        m.addAttribute(CSRFController.getInstance().getCSRFToken(), CSRFController.getInstance().generateToken());
        m.addAttribute("cart", request.getSession().getAttribute("Cart"));
        return "delete";
    }

    /**
     * When you go to the delete page with a post method
     *
     * @param m         : The model
     * @param request   : The request made
     * @param response  : The response given
     * @param password  : The password to delete the account
     * @param csrfToken : The token to prevent CSRF (Cross side request forgery)
     * @return delete to get the correct template
     * @throws IllegalArgumentException : when response isn't good
     * @throws IOException              : When response isn't good
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String delete(Model m,
                         HttpServletRequest request,
                         HttpServletResponse response,
                         @RequestParam("password") String password,
                         @RequestParam(CSRFToken) String csrfToken) throws IllegalArgumentException, IOException {
        m = MainController.addDefaultAttributesToModel(m, "Delete", request, response);

        m.addAttribute(MainController.TITLE_ATTRIBUTE, "Account deleted");
        if (!CSRFController.getInstance().validToken(csrfToken))
            m.addAttribute(ERROR_ATTRIBUTE, "Token not found or expired");

        AccountModel accountModel = MainController.getCurrentUser(request);
        if (!checkPassword(accountModel, password))
            m.addAttribute(ERROR_ATTRIBUTE, "Password invalid");

        if (!m.containsAttribute(ERROR_ATTRIBUTE)) {
            DatabaseController.getInstance().deleteAccount(accountModel.getUUID());
            response.sendRedirect("/logout");
        }

        CSRFController.getInstance().voidToken(csrfToken);
        return "delete";
    }


    /**
     * Going to the login page
     *
     * @param m : The model / template
     * @return login to get the correct template
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model m, HttpServletRequest request, HttpServletResponse response) {
        Object o = request.getSession().getAttribute(MainController.REFERRER_ATTRIBUTE);
        String referrer = String.valueOf(o);
        request.getSession().setAttribute(MainController.REFERRER_ATTRIBUTE, null);

        m = MainController.addDefaultAttributesToModel(m, "Login", request, response);
        m.addAttribute("referrer", referrer);

        return "login";
    }

    /**
     * Going to the register page
     *
     * @param m : The model / template
     * @return register to get the register template
     */
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String register(Model m, HttpServletRequest request, HttpServletResponse response) {
        m = MainController.addDefaultAttributesToModel(m, "Register", request, response);

        return "register";
    }

    /**
     * Actualle registering an account
     *
     * @param email    : The email used when registering, has to be unqiue
     * @param password : Password for the account, minimal 8 characters, maximal 32 characters
     * @param name     : the username for the account
     * @param response : The response from the server
     * @param m        : The model / template
     * @return Null
     * @throws IOException : When the input / output is incorrect
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(@RequestParam(value = "email") String email,
                           @RequestParam(value = "password") String password,
                           @RequestParam(value = "name") String name,
                           HttpServletResponse response,
                           Model m,
                           HttpServletRequest request) throws IOException {
        String sanitizedName = Jsoup.clean(name, Whitelist.simpleText());
        String sanitizedEmail = Jsoup.clean(email, Whitelist.simpleText());
        String sanitizedPassword = Jsoup.clean(password, Whitelist.simpleText());

        boolean accountExists = DatabaseController.getInstance().getAccount(sanitizedEmail) != null;
        if (accountExists)
            response.sendRedirect("/register");

        DatabaseController.getInstance().createAccount(sanitizedName, sanitizedEmail, sanitizedPassword);

        response.sendRedirect("/login");
        return null;
    }

    /**
     * Get the account settings page
     *
     * @param m : The model / template
     * @return account_settings to get the correct template
     * @throws IOException : When input / output is incorrect
     */
    @RequestMapping(value = "/account/settings", method = RequestMethod.GET)
    public String accountSettingsGet(Model m,
                                     HttpServletRequest request,
                                     HttpServletResponse response) throws IOException {
        m = MainController.addDefaultAttributesToModel(m, "Settings", request, response);
        AccountModel accountModel = MainController.getCurrentUser(request);
        m.addAttribute(MainController.TITLE_ATTRIBUTE, "Account Settings");
        if (accountModel == null) {
            m.addAttribute(ERROR_ATTRIBUTE, messageSource.getMessage("error.notloggedin", null, request.getLocale()));
            response.sendRedirect("/login");
        }

        return "account_settings";
    }

    /**
     * Logging out, has to be logged in first to log out.
     *
     * @param response : The response from the server
     * @return to login screen.
     */
    @RequestMapping(value = "/logout")
    public void accountLogout(HttpServletRequest request,
                              HttpServletResponse response) throws IOException {
        request.getSession().setAttribute(MainController.ACCOUNT_ATTRIBUTE, null);
        request.getSession().setAttribute(MainController.PRIVILEGED_ATTRIBUTE, null);
        request.getSession().setAttribute(MainController.SUCCESS_ATTRIBUTE, messageSource.getMessage("logout.success", null, request.getLocale()));
        request.getSession().invalidate();
        response.sendRedirect("/");
    }
}

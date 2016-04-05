package nl.pts4.controller;

import nl.pts4.model.AccountModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Teun on 20-3-2016.
 */
@Controller
public class MainController {

    public static final String TITLE_ATTRIBUTE = "title";
    public static final String ERROR_ATTRIBUTE = "error";
    public static final String SUCCESS_ATTRIBUTE = "success";

    public static boolean assertUserIsPrivileged(String account, HttpServletRequest request, HttpServletResponse response) {
        if (account == null || !DatabaseController.getInstance().checkPrivalegedUser(DatabaseController.getInstance().getAccountByCookie(account).getUuid())) {
            try {
                request.getSession().setAttribute(MainController.ERROR_ATTRIBUTE, "You are not allowed to do that");
                response.sendRedirect("/login");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }

    /**
     * The main page
     * @param m         : The model / template
     * @param account   : The account cookie, is not required
     * @return main to load the correct template
     */
    @RequestMapping(value = "/")
    public String main(Model m, @CookieValue(value = AccountController.AccountCookie, required = false) String account, HttpServletRequest request) {
        m.addAttribute(MainController.TITLE_ATTRIBUTE, "Fotowinkel");
        AccountModel am = DatabaseController.getInstance().getAccountByCookie(account);
        m.addAttribute("user", am);
        m.addAttribute("cart", request.getSession().getAttribute("Cart"));
        if (account == null) {
            m.addAttribute("privileged", false);
        } else {
            m.addAttribute("privileged", DatabaseController.getInstance().checkPrivalegedUser(DatabaseController.getInstance().getAccountByCookie(account).getUuid()));
        }
        return "main";
    }

    /**
     * The header of the page
     * @param m         : The model / template
     * @param account   : The account cookie, is not required
     * @return header to get the correct template
     */
    @RequestMapping(value = "/header")
    public String header(Model m, @CookieValue(value = AccountController.AccountCookie, required = false) String account) {
        m.addAttribute(MainController.TITLE_ATTRIBUTE, "Fotowinkel");
        AccountModel am = DatabaseController.getInstance().getAccountByCookie(account);
        m.addAttribute("user", am);
        return "header";
    }
}

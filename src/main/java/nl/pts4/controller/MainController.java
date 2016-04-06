package nl.pts4.controller;

import nl.pts4.model.AccountModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    public static final String WARNING_ATTRIBUTE = "warning";
    public static final String ERROR_ATTRIBUTE = "error";
    public static final String SUCCESS_ATTRIBUTE = "success";
    public static final String PRIVILEGED_ATTRIBUTE = "privileged";
    public static final String ACCOUNT_ATTRIBUTE = "account";

    public static boolean assertUserIsPrivileged(HttpServletRequest request, HttpServletResponse response, boolean redirect) {
        Object privilegedAttribute = request.getSession().getAttribute(PRIVILEGED_ATTRIBUTE);
        AccountModel accountModel = (AccountModel) request.getSession().getAttribute(ACCOUNT_ATTRIBUTE);

        if (accountModel == null) {
            if (redirect) {
                redirectToLogin(request, response);
            }
            return false;
        }

        if (privilegedAttribute == null) {
            privilegedAttribute = DatabaseController.getInstance().checkPriviledgedUser(accountModel.getUUID());
            request.getSession().setAttribute(PRIVILEGED_ATTRIBUTE, privilegedAttribute);
        } else {
            privilegedAttribute = request.getSession().getAttribute(PRIVILEGED_ATTRIBUTE);
        }

        boolean privileged = (boolean) privilegedAttribute;
        if (!privileged && redirect) {
            redirectToLogin(request, response);
        }

        return privileged;
    }

    public static AccountModel getCurrentUser(HttpServletRequest request) {
        return (AccountModel) request.getSession().getAttribute(ACCOUNT_ATTRIBUTE);
    }

    private static void redirectToLogin(HttpServletRequest request, HttpServletResponse response) {
        // TODO Internationalisation
        request.getSession().setAttribute(MainController.ERROR_ATTRIBUTE, "You are not allowed to do that");
        try {
            response.sendRedirect("/login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The main page
     *
     * @param m : The model / template
     * @return main to load the correct template
     */
    @RequestMapping(value = "/")
    public String main(Model m, HttpServletRequest request, HttpServletResponse response) {
        m.addAttribute(MainController.TITLE_ATTRIBUTE, "Fotowinkel");
        AccountModel am = getCurrentUser(request);
        m.addAttribute("user", am);
        m.addAttribute("cart", request.getSession().getAttribute("Cart"));
        m.addAttribute(MainController.PRIVILEGED_ATTRIBUTE, MainController.assertUserIsPrivileged(request, response, false));
        return "main";
    }

    /**
     * The header of the page
     *
     * @param m : The model / template
     * @return header to get the correct template
     */
    @RequestMapping(value = "/header")
    public String header(Model m, HttpServletRequest request) {
        m.addAttribute(MainController.TITLE_ATTRIBUTE, "Fotowinkel");
        AccountModel am = getCurrentUser(request);
        m.addAttribute("user", am);
        return "header";
    }
}

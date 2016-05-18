package nl.pts4.controller;

import nl.pts4.model.AccountModel;
import nl.pts4.model.ItemModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by wouter on 30-3-2016.
 */
@Controller
public class ChildController {

    @Autowired
    MessageSource messageSource;

    @RequestMapping(value = "addchild", method = RequestMethod.GET)
    public String addChildGET(HttpServletRequest request,
                           HttpServletResponse response,
                           Model model) {

        if (!MainController.assertUserIsPrivileged(request, response, true)) return null;
        return "addchild";
    }

    @RequestMapping(value = "addchild", method = RequestMethod.POST)
    public String addChildPOST(HttpServletRequest request,
                           HttpServletResponse response,
                           @RequestParam("inputCode") String childCode,
                               Model model) {

        if (!MainController.assertUserIsPrivileged(request, response, true)) return null;
        DatabaseController db = DatabaseController.getInstance();


        if (db.addChildToUser(MainController.getCurrentUser(request),childCode)) {
            model.addAttribute("success", messageSource.getMessage("success.addchild", null, RequestContextUtils.getLocale(request)));
        } else {
            model.addAttribute("error", messageSource.getMessage("error.addchild", null, RequestContextUtils.getLocale(request)));
        }

        return "addchild";
    }

}


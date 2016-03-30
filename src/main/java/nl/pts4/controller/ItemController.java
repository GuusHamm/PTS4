package nl.pts4.controller;

import nl.pts4.model.AccountModel;
import nl.pts4.model.SchoolModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by wouter on 30-3-2016.
 */
@Controller
public class ItemController {

    @Autowired
    MessageSource messageSource;

    @RequestMapping("makeitem")
    public String makeItem(HttpServletRequest request, Model model, @CookieValue(AccountController.AccountCookie) String cookie){


        DatabaseController databaseController = DatabaseController.getInstance();
        AccountModel photographer= databaseController.getAccountByCookie(cookie);

        if(photographer ==null || photographer.getAccountTypeEnum()!= AccountModel.AccountTypeEnum.photographer){

            Locale locale = RequestContextUtils.getLocale(request);
            model.addAttribute(MainController.ERROR_ATTRIBUTE,  messageSource.getMessage("error.warning.not.allowed",null, locale));
            return "main";
        }

        List<SchoolModel> scholen= new ArrayList<>();
        for(SchoolModel s: databaseController.getSchools(photographer.getUuid())){
            scholen.add(s);
        }
        model.addAttribute("scholen", scholen);

        return "make_item";
    }
}

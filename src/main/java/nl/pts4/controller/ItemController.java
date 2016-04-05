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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
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

    @RequestMapping(value = "makeitem", method = RequestMethod.GET)
    public String makeItem(HttpServletRequest request, Model model, @CookieValue(AccountController.AccountCookie) String cookie, @RequestParam(value = "PreviousInsert",defaultValue = "0")int wentwell){

        if(wentwell==1){
            model.addAttribute("success", messageSource.getMessage("success.database",null,RequestContextUtils.getLocale(request)));
        }else if(wentwell==2){
            model.addAttribute("error", messageSource.getMessage("error.database",null,RequestContextUtils.getLocale(request)));
        }
        DatabaseController databaseController = DatabaseController.getInstance();
        AccountModel photographer= databaseController.getAccountByCookie(cookie);

        if(photographer ==null || photographer.getAccountTypeEnum()!= AccountModel.AccountTypeEnum.photographer){

            Locale locale = RequestContextUtils.getLocale(request);
            model.addAttribute(MainController.ERROR_ATTRIBUTE,  messageSource.getMessage("error.warning.not.allowed",null, locale));
            return "main";
        }

//        List<SchoolModel> scholen= new ArrayList<>();
//        for(SchoolModel s: databaseController.getSchools(photographer.getUuid())){
//            scholen.add(s);
//        }
//        model.addAttribute("scholen", scholen);

        return "make_item";
    }

    @RequestMapping(value = "makeitem", method = RequestMethod.POST)
    public String makeItem( @RequestParam(value = "type", required = true) String type,
                            @RequestParam(value = "price", required = true) double price,
                            @RequestParam(value = "description" , required = true) String description,
                            @RequestParam(value = "file", required = true) MultipartFile file,
                            HttpServletRequest request,
                            Model model,
                            @CookieValue(AccountController.AccountCookie) String cookie

    ){
        DatabaseController databaseController = DatabaseController.getInstance();

        String thumbnailPath = new FileUploadController().uploadItemThumbnail(file);
        int wentWell = 0;
        if(databaseController.insertItem(price, type,description,thumbnailPath))    wentWell=1;
        else wentWell=2;

        return makeItem(request,model,cookie, wentWell);

    }
}

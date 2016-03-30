package nl.pts4.controller;

import nl.pts4.model.AccountModel;
import nl.pts4.model.PhotoModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Created by Teun on 23-3-2016.
 */
@Controller
public class PhotoViewController {
    /**
     * Show all the photos
     * @param account   : The current account cookie
     * @param m         : The model / template
     * @return photoview to get the correct template
     */
    @RequestMapping("/photos")
    public String photosGet(@CookieValue(AccountController.AccountCookie) String account, Model m) {
        AccountModel accountModel = DatabaseController.getInstance().getAccountByCookie(account);
        List<PhotoModel> photos = DatabaseController.getInstance().getPhotos();

        m.addAttribute(MainController.TITLE_ATTRIBUTE, "Photos");
        m.addAttribute(AccountController.AccountModelKey, accountModel);
        m.addAttribute("photos", photos.toArray(new PhotoModel[photos.size()]));

        return "photoview";
    }

}

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
public class ItemController {

    @Autowired
    MessageSource messageSource;

    @RequestMapping(value = "makeitem", method = RequestMethod.GET)
    public String makeItem(HttpServletRequest request,
                           HttpServletResponse response,
                           Model model) {

        if (!MainController.assertUserIsPrivileged(request, response, true)) return null;

        model = MainController.addDefaultAttributesToModel(model, "Make Item", request, response);

        return "make_item";
    }

    @RequestMapping(value = "makeitem", method = RequestMethod.POST)
    public String makeItem(@RequestParam(value = "type") String type,
                           @RequestParam(value = "price") double price,
                           @RequestParam(value = "description") String description,
                           @RequestParam(value = "file") MultipartFile file,
                           HttpServletRequest request,
                           HttpServletResponse response,
                           Model model) {
        if (!MainController.assertUserIsPrivileged(request, response, true)) return null;

        DatabaseController databaseController = DatabaseController.getInstance();

        String thumbnailPath = new FileUploadController().uploadItemThumbnail(file);
        if (databaseController.insertItem(price, type, description, thumbnailPath)) {
            model.addAttribute("success", messageSource.getMessage("success.database", null, RequestContextUtils.getLocale(request)));
        } else {
            model.addAttribute("error", messageSource.getMessage("error.database", null, RequestContextUtils.getLocale(request)));
        }
        return makeItem(request, response, model);

    }

    @RequestMapping(value = "changeitem", method = RequestMethod.GET)
    public String changeItem(HttpServletRequest request,
                             HttpServletResponse response,
                             Model model,
                             @RequestParam(value = "itemid") Integer id) throws IOException {
        if (!MainController.assertUserIsPrivileged(request, response, true)) return null;

        request.getSession().setAttribute("itemID", id);
        if (id != null) {
            ItemModel itemModel = DatabaseController.getInstance().getItemByID(id);

            model.addAttribute("type", itemModel.getType());
            model.addAttribute("price", itemModel.getPrice());
            model.addAttribute("description", itemModel.getDescription());
        } else {
            model.addAttribute("error", messageSource.getMessage("error.item.not.selected", null, RequestContextUtils.getLocale(request)));
            //Todo this will lead the user to a page that is blank and pretty much does nothing since it updates by id.
            //it might be a good ides to lead him back to the previous page
            response.sendRedirect(request.getHeader("referer"));
        }
        model = MainController.addDefaultAttributesToModel(model, "Change Item", request, response);


        return "change_item";
    }

    @RequestMapping(value = "changeitem", method = RequestMethod.POST)
    /**
     * Handles the logic of the actual button press, after that it calls the get method for changeitem
     */
    public String changeItem(
            @RequestParam(value = "type") String type,
            @RequestParam(value = "price") double price,
            @RequestParam(value = "description") String description,
            @RequestParam(value = "file") MultipartFile file,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) throws IOException {
        if (!MainController.assertUserIsPrivileged(request, response, true)) return null;

        int id = (Integer) request.getSession().getAttribute("itemID");


        DatabaseController databaseController = DatabaseController.getInstance();

        String thumbnailPath = new FileUploadController().uploadItemThumbnail(file);
        //TODO optimize this, i think i can write this better.

        if (file.isEmpty()) {
            if (databaseController.updateItem(id, price, type, description))
                model.addAttribute("success", messageSource.getMessage("success.item.change.database", null, RequestContextUtils.getLocale(request)));
        } else {
            if (databaseController.updateItem(id, price, type, description, thumbnailPath))
                model.addAttribute("success", messageSource.getMessage("success.item.change.database", null, RequestContextUtils.getLocale(request)));
        }
        if (!model.containsAttribute("success")) {
            model.addAttribute("error", messageSource.getMessage("error.item.change.database", null, RequestContextUtils.getLocale(request)));
        }

        //TODO check if the item corresponds with who made it.
        return changeItem(request, response, model, id);

    }

    @RequestMapping(value = "items", method = RequestMethod.GET)
    /**
     * Handles the logic of the actual button press, after that it calls the get method for changeitem
     */
    public String itemOverView(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) {

        if (!MainController.assertUserIsPrivileged(request, response, true)) return null;

        AccountModel accountModel = MainController.getCurrentUser(request);

        List<ItemModel> items = DatabaseController.getInstance().getItems();
        model.addAttribute("items", items.toArray());

        model = MainController.addDefaultAttributesToModel(model, "Item Overview", request, response);

        return "item_overview";
    }

    @RequestMapping(value = "itemoverviewdelete", method = RequestMethod.GET)
    public String deleteItem(
            HttpServletRequest request,
            HttpServletResponse response,
            Model model,
            @RequestParam(value = "itemid") int id) {

        if (!MainController.assertUserIsPrivileged(request, response, true)) return null;

        DatabaseController.getInstance().deleteItem(id);

        return itemOverView(request, response, model);
    }
}

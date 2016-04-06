package nl.pts4.controller;

import nl.pts4.model.AccountModel;
import nl.pts4.model.ItemModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
	public String makeItem(HttpServletRequest request,
						   HttpServletResponse response,
						   Model model,
						   @CookieValue(AccountController.AccountCookie) String cookie,
						   @RequestParam(value = "PreviousInsert", defaultValue = "0") int wentwell
	) {

		if (!MainController.assertUserIsPrivileged(cookie, request, response, true)) return null;


		if (wentwell == 1) {
			model.addAttribute("success", messageSource.getMessage("success.database", null, RequestContextUtils.getLocale(request)));
		} else if (wentwell == 2) {
			model.addAttribute("error", messageSource.getMessage("error.database", null, RequestContextUtils.getLocale(request)));
		}
		DatabaseController databaseController = DatabaseController.getInstance();
		AccountModel photographer = MainController.getCurrentUser(cookie, request);

		if (photographer == null) {
			Locale locale = RequestContextUtils.getLocale(request);
			model.addAttribute(MainController.ERROR_ATTRIBUTE, messageSource.getMessage("error.warning.not.allowed", null, locale));
			return "main";
		}

		return "make_item";
	}

	@RequestMapping(value = "makeitem", method = RequestMethod.POST)
	public String makeItem(@RequestParam(value = "type", required = true) String type,
						   @RequestParam(value = "price", required = true) double price,
						   @RequestParam(value = "description", required = true) String description,
						   @RequestParam(value = "file", required = true) MultipartFile file,
						   HttpServletRequest request,
						   HttpServletResponse response,
						   Model model,
						   @CookieValue(AccountController.AccountCookie) String cookie

	) {
		if (!MainController.assertUserIsPrivileged(cookie, request, response, true)) return null;

		DatabaseController databaseController = DatabaseController.getInstance();

		String thumbnailPath = new FileUploadController().uploadItemThumbnail(file);
		int wentWell = 0;
		if (databaseController.insertItem(price, type, description, thumbnailPath)) wentWell = 1;
		else wentWell = 2;

		return makeItem(request, response, model, cookie, wentWell);

	}

	@RequestMapping(value = "changeitem", method = RequestMethod.GET)
	public String changeItem(HttpServletRequest request,
							 HttpServletResponse response,
							 Model model,
							 @CookieValue(AccountController.AccountCookie) String cookie,
							 @RequestParam(value = "PreviousInsert", defaultValue = "0") int wentwell,
							 @RequestParam(value = "itemid", required = true) int id
	) {
		if (!MainController.assertUserIsPrivileged(cookie, request, response, true)) return null;

		Integer itemid = id;
		request.getSession().setAttribute("itemID", itemid);
		if (itemid != null) {

			//TODO check if the item corresponds with who made it.
			if (wentwell == 1) {
				model.addAttribute("success", messageSource.getMessage("success.item.change.database", null, RequestContextUtils.getLocale(request)));
			} else if (wentwell == 2) {
				model.addAttribute("error", messageSource.getMessage("error.item.change.database", null, RequestContextUtils.getLocale(request)));
			}

			DatabaseController databaseController = DatabaseController.getInstance();
			AccountModel photographer = MainController.getCurrentUser(cookie, request);

			if (photographer == null) {

				Locale locale = RequestContextUtils.getLocale(request);
				model.addAttribute(MainController.ERROR_ATTRIBUTE, messageSource.getMessage("error.warning.not.allowed", null, locale));
				return "main";
			}
			ItemModel itemModel = DatabaseController.getInstance().getItemByID(itemid);

			model.addAttribute("type", itemModel.getType());
			model.addAttribute("price", itemModel.getPrice());
			model.addAttribute("description", itemModel.getDescription());
		} else {
			model.addAttribute("error", messageSource.getMessage("error.item.not.selected", null, RequestContextUtils.getLocale(request)));
			//Todo this will lead the user to a page that is blank and pretty much does nothing since it updates by id.
			//it might be a good ides to lead him back to the previous page
		}

		return "change_item";
	}

	@RequestMapping(value = "changeitem", method = RequestMethod.POST)
	/**
	 * Handles the logic of the actual button press, after that it calls the get method for changeitem
	 */
	public String changeItem(

			@RequestParam(value = "type", required = true) String type,
			@RequestParam(value = "price", required = true) double price,
			@RequestParam(value = "description", required = true) String description,
			@RequestParam(value = "file", required = true) MultipartFile file,
			HttpServletRequest request,
			HttpServletResponse response,
			Model model,
			@CookieValue(AccountController.AccountCookie) String cookie

	) {
		if (!MainController.assertUserIsPrivileged(cookie, request, response, true)) return null;

		int id = (Integer) request.getSession().getAttribute("itemID");


		DatabaseController databaseController = DatabaseController.getInstance();

		String thumbnailPath = new FileUploadController().uploadItemThumbnail(file);
		//TODO optimize this, i think i can write this better.
		int wentWell = 0;

		if (file.isEmpty()) {
			if (databaseController.updateItem(id, price, type, description)) wentWell = 1;
			else wentWell = 2;
		} else {
			if (databaseController.updateItem(id, price, type, description, thumbnailPath)) wentWell = 1;
			else wentWell = 2;
		}

		return changeItem(request, response, model, cookie, wentWell, id);

	}

	@RequestMapping(value = "items", method = RequestMethod.GET)
	/**
	 * Handles the logic of the actual button press, after that it calls the get method for changeitem
	 */
	public String itemOverView(
			HttpServletRequest request,
			HttpServletResponse response,
			Model model,
			@CookieValue(value = AccountController.AccountCookie, required = false) String cookie,
			@RequestParam(value = "PreviousInsert", defaultValue = "0") int wentwell
	) {

		if (!MainController.assertUserIsPrivileged(cookie, request, response, true)) return null;

		AccountModel accountModel = MainController.getCurrentUser(cookie, request);


		List<ItemModel> items = DatabaseController.getInstance().getItems();
		model.addAttribute("items", items.toArray());


		if (wentwell == 1) {
			model.addAttribute("success", messageSource.getMessage("success.delete.item.database", null, RequestContextUtils.getLocale(request)));
		} else if (wentwell == 2) {
			model.addAttribute("error", messageSource.getMessage("error.delete.item.database", null, RequestContextUtils.getLocale(request)));
		}


		return "item_overview";
	}

	@RequestMapping(value = "itemoverviewdelete", method = RequestMethod.GET)
	public String deleteItem(
			HttpServletRequest request,
			HttpServletResponse response,
			Model model,
			@CookieValue(AccountController.AccountCookie) String cookie,
			@RequestParam(value = "itemid", required = true) int id
	) {

		if (!MainController.assertUserIsPrivileged(cookie, request, response, true)) return null;

		int wentwell = 0;

		DatabaseController dbc = DatabaseController.getInstance();
		AccountModel accountModel = MainController.getCurrentUser(cookie, request);

		wentwell = dbc.deleteItem(id) ? 1 : 2;


		return itemOverView(request, response, model, cookie, wentwell);
	}
}

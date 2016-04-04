package nl.pts4.controller;

import nl.pts4.model.AccountModel;
import nl.pts4.model.PhotoModel;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Teun on 23-3-2016.
 */
@Controller
public class PhotoViewController {
	/**
	 * Show all the photos
	 *
	 * @param account : The current account cookie
	 * @param m       : The model / template
	 * @return photoview to get the correct template
	 */
	@RequestMapping("/photos")
	public String photosGet(@CookieValue(AccountController.AccountCookie) String account, Model m, HttpServletRequest request) {
		AccountModel accountModel = DatabaseController.getInstance().getAccountByCookie(account);
		List<PhotoModel> photos = DatabaseController.getInstance().getPhotos();

		m.addAttribute(MainController.TITLE_ATTRIBUTE, "Photos");
		m.addAttribute(AccountController.AccountModelKey, accountModel);
		m.addAttribute("photos", photos.toArray(new PhotoModel[photos.size()]));
		m.addAttribute("cart", request.getSession().getAttribute("Cart"));

		if (request.getSession().getAttribute("Succes") != null && (boolean) request.getSession().getAttribute("Succes") == true) {
			m.addAttribute(MainController.SUCCESS_ATTRIBUTE, "Photo has been added to your cart");
			request.getSession().removeAttribute("Succes");
		}
		return "photoview";
	}

	@RequestMapping(value = "/photos", params = {"id"})
	public String photosAddToCart(Model m, @CookieValue(AccountController.AccountCookie) String account, @RequestParam(value = "id", required = false) UUID id, HttpServletRequest request, HttpServletResponse response) {
		LinkedList<PhotoModel> cart;
		if (request.getSession().getAttribute("Cart") == null) {
			cart = new LinkedList<>();
		} else {
			cart = (LinkedList<PhotoModel>) request.getSession().getAttribute("Cart");
		}
		if (id != null) {
			cart.add(DatabaseController.getInstance().getPhotoByUUID(id));
			request.getSession().setAttribute("Cart", cart);

		}
		request.getSession().setAttribute("Cart", cart);
		request.getSession().setAttribute("Succes", true);


		try {
			response.sendRedirect("/photos");
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	@RequestMapping(value = "/clearcart")
	public String clearCart(HttpServletRequest request, HttpServletResponse response) {
		request.getSession().removeAttribute("Cart");
		try {
			response.sendRedirect(request.getHeader("referer"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public BufferedImage reduceImageQuility(File imageSource) throws ImageReadException, IOException {
		BufferedImage image = Imaging.getBufferedImage(imageSource);

		BufferedImage resizedImage = resizeImage(image, image.getWidth() / 10, image.getHeight() / 10);

		return resizedImage;
	}

	public BufferedImage reduceImageQuility(BufferedImage imageSource) throws ImageReadException, IOException {
		BufferedImage resizedImage = resizeImage(imageSource, imageSource.getWidth() / 10, imageSource.getHeight() / 10);

		return resizedImage;
	}

	public BufferedImage resizeImage(BufferedImage image, int width, int height) {
		int type = 0;
		type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();
		BufferedImage resizedImage = new BufferedImage(width, height, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(image, 0, 0, width, height, null);
		g.dispose();
		return resizedImage;
	}

}

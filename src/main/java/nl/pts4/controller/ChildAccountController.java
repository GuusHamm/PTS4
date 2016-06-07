package nl.pts4.controller;

import nl.pts4.model.ChildModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by wouter on 30-3-2016.
 */
@Controller
public class ChildAccountController {

    @Autowired
    MessageSource messageSource;

    @RequestMapping(value="addchild")
    public String addChild(HttpServletRequest request,
                           HttpServletResponse response,
                           Model model){
        MainController.addDefaultAttributesToModel(model,"Add Child",request,response);
		return "add_child";
    }

    @RequestMapping(value="createchild")
    public String createChild(HttpServletRequest request,
                           HttpServletResponse response,
                           Model model){

		ChildModel  childModel = null;
		while(childModel == null){
			String uniqueCode = UUID.randomUUID().toString();
			uniqueCode = uniqueCode.substring(uniqueCode.length()-9,uniqueCode.length()-1);
			childModel = DatabaseController.getInstance().createChild(UUID.randomUUID(),uniqueCode);
		}

		request.getSession().setAttribute(MainController.SUCCESS_ATTRIBUTE, String.format("Added a new child with the code %s",childModel.getUniqueCode()));

		try {
			response.sendRedirect("/addchild");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

    @RequestMapping(value = "addchildtoparent", method = RequestMethod.GET)
    public String addChildToParentGET(HttpServletRequest request,
                           HttpServletResponse response,
                           Model model) {

        if (!MainController.assertUserIsSignedIn(request, response)) return null;
        return "addchildtoparent";
    }

    @RequestMapping(value = "addchildtoparent", method = RequestMethod.POST)
    public String addChildToParentPOST(HttpServletRequest request,
                           HttpServletResponse response,
                           @RequestParam("inputCode") String childCode,
                               Model model) {

        if (!MainController.assertUserIsSignedIn(request, response)) return null;
        DatabaseController db = DatabaseController.getInstance();


        if (db.addChildToUser(MainController.getCurrentUser(request),childCode)) {
            model.addAttribute("success", messageSource.getMessage("success.addchild", null, RequestContextUtils.getLocale(request)));
        } else {
            model.addAttribute("error", messageSource.getMessage("error.addchild", null, RequestContextUtils.getLocale(request)));
        }

        return "addchildtoparent";
    }

}


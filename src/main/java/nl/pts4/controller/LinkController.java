package nl.pts4.controller;

import nl.pts4.model.AccountModel;
import nl.pts4.model.LinkModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class LinkController {

    @RequestMapping(value = "/l/{key}")
    public void getLink(HttpServletRequest request, HttpServletResponse response, @PathVariable String key) throws IOException {
        LinkModel lm = DatabaseController.getInstance().getLinkModel(key);
        AccountModel lmAccount = lm.getAuthorizedUser();
        if (lmAccount != null) {
            if (!MainController.assertUserIsSignedIn(request, response)) {
                return;
            }
        }
        response.sendRedirect(lm.getLink());
        
    }

}

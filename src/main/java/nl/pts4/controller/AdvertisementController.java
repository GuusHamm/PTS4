package nl.pts4.controller;

import nl.pts4.admin.advertisement.AdTemplate;
import nl.pts4.admin.advertisement.AdTemplateLoader;
import nl.pts4.admin.advertisement.AdTemplateParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * Created by Teun on 1-6-2016.
 */
@Controller
public class AdvertisementController {

    @Autowired
    private MessageSource messageSource;

    @RequestMapping(value = "/advertisement")
    public String getAdvertisement(HttpServletRequest request, HttpServletResponse response, Model m) {
        if (!MainController.assertUserIsPrivileged(request, response, true)) {
            return null;
        }
        m = MainController.addDefaultAttributesToModel(m, "Create a advertisement", request, response);

        String[] templates = AdTemplateLoader.getTemplates();
        List<AdTemplate> adTemplateList = new ArrayList<>(templates.length);
        Map<String, Object> defaultTemplate = AdTemplateParameters.getDefaultMapping(request);

        Enumeration<String> parameters = request.getParameterNames();
        while (parameters.hasMoreElements()) {
            String element = parameters.nextElement();
            defaultTemplate.put(element, request.getParameter(element));
        }

        for (String template : templates) {
            template = template.replaceAll(".vm", "");
            adTemplateList.add(new AdTemplate(defaultTemplate, template));
        }
        m.addAttribute("templates", adTemplateList);
        return "advertisement";
    }


}

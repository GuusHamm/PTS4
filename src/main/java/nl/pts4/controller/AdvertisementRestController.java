package nl.pts4.controller;

import nl.pts4.admin.advertisement.AdTemplate;
import nl.pts4.admin.advertisement.AdTemplateLoader;
import nl.pts4.admin.advertisement.AdTemplateParameters;
import nl.pts4.email.EmailManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Created by Teun on 1-6-2016.
 */
@RestController
public class AdvertisementRestController {


    public AdvertisementRestController() {

    }

    @RequestMapping(value = "/advertisement/templates")
    public List<AdTemplate> getTemplateFiles(HttpServletRequest request, HttpServletResponse response) {
        if (!MainController.assertUserIsPrivileged(request, response, true)) {
            return null;
        }

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
        return adTemplateList;
    }

    @RequestMapping(value = "/advertisement/send")
    public void sendAdvertisement(HttpServletRequest request,
                                  HttpServletResponse response,
                                  @RequestParam(name = "to") String to,
                                  @RequestParam(name = "template") String template) throws IOException {
        EmailManager em = new EmailManager();
        HashMap<String, Object> hm = new HashMap<>();
        hm.put("percent", 5);
        em.sendAdvertisementTemplate(template + ".vm", hm, "template mail");
        response.sendRedirect("/");
    }
}

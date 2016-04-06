package nl.pts4;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Teun on 9-3-2016.
 */
@Controller
public class GreetingController {

//    @Autowired
//    SessionLocaleResolver localeResolver;
//    @Autowired
//    LocaleChangeInterceptor localeChangeInterceptor;

    @Autowired
    MessageSource messageSource;


    @RequestMapping("/test")
    public String test(@RequestParam(value = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        return "test";
    }

    @RequestMapping("/languages")
    public String languages(Model model, HttpServletRequest request, HttpServletResponse response) {
//        locale.setDefault(new Locale("nl"));
//        RequestContextUtils.getLocaleResolver(request).setLocale(request,response, new Locale("nl"));
//        localeResolver.setLocale(request,response, new Locale("nl"));
//        Object a = localeChangeInterceptor;

        System.out.println(messageSource.getMessage("welcome.springmvc", new Object[]{}, RequestContextUtils.getLocale(request)));
        System.out.println(messageSource.getMessage("welcome.springmvc", new Object[]{}, RequestContextUtils.getLocale(request)));
        return "main";
    }

}

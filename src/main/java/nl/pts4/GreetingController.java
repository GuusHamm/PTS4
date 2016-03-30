package nl.pts4;

import org.apache.catalina.connector.Request;
import org.apache.commons.lang.LocaleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

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
    public String test(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "test";
    }
    @RequestMapping("/languages")
    public String languages(Model model, HttpServletRequest request, HttpServletResponse response ) {
//        locale.setDefault(new Locale("nl"));
//        RequestContextUtils.getLocaleResolver(request).setLocale(request,response, new Locale("nl"));
//        localeResolver.setLocale(request,response, new Locale("nl"));
//        Object a = localeChangeInterceptor;

        System.out.println(messageSource.getMessage("welcome.springmvc",new Object[]{}, RequestContextUtils.getLocale(request)));
        System.out.println(messageSource.getMessage("welcome.springmvc",new Object[]{}, RequestContextUtils.getLocale(request)));
        return "main";
    }

}

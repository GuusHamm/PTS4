package nl.pts4;

import org.apache.commons.lang.LocaleUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Locale;

/**
 * Created by Teun on 9-3-2016.
 */
@Controller
public class GreetingController {

    @RequestMapping("/test")
    public String test(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "test";
    }
    @RequestMapping("/languages")
    public String languages(@RequestParam(value="language", required=false, defaultValue="World") String language, Model model) {
        return "WelcomePage";
    }

}

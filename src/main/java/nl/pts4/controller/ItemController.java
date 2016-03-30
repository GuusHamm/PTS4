package nl.pts4.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wouter on 30-3-2016.
 */
@Controller
public class ItemController {

    @RequestMapping("makeitem")
    public String makeItem(HttpServletRequest request, Model model){

        List<String> scholen= new ArrayList<String>();
        scholen.add("hello");
        scholen.add("world");
        scholen.add("from");
        scholen.add("Eindhoven");
        model.addAttribute("scholen", scholen);



        return "make_item";
    }
}

package nl.pts4.admin.advertisement;

import nl.pts4.controller.MainController;
import nl.pts4.model.AccountModel;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Teun on 1-6-2016.
 */
public class AdTemplateParameters {

    public static final String DefaultUserKey = "user";

    public static Map<String, Object> getDefaultMapping(HttpServletRequest request) {
        HashMap<String, Object> map = new HashMap<>();

        AccountModel accountModel = MainController.getCurrentUser(request);
        map.put(DefaultUserKey, accountModel);
        map.put("percent", 5);

        return map;
    }

}

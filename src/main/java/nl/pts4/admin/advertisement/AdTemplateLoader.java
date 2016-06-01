package nl.pts4.admin.advertisement;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.io.File;
import java.util.Map;

/**
 * Created by Teun on 1-6-2016.
 */
public class AdTemplateLoader {

    public static String[] getTemplates() {
        File templateFolder = new File("src/main/resources/templates/advertisements/");
        return templateFolder.list();
    }

    public static File getTemplateFolder() {
        return new File("src/main/resources/templates/advertisements");
    }

    public static String getTemplateResult(String template, Map<String, Object> model) {
        VelocityEngine engine = VelocityEngineConfiguration.getConfiguredEngine();
        return VelocityEngineUtils.mergeTemplateIntoString(engine, "/templates/advertisements/" + template, "UTF-8", model);
    }

}

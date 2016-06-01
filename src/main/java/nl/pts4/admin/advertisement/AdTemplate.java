package nl.pts4.admin.advertisement;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.io.FileUtils;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Teun on 1-6-2016.
 */
public class AdTemplate extends JsonSerializer<AdTemplate> {

    public static final String AdvertisementsPath = "src/main/resources/templates/advertisements/";
    private static final String AdvertisementsVelocityPath = "/templates/advertisements/";

    private VelocityEngine engine;

    private String path;
    private String content;
    private Map<String, Object> attributes;

    public AdTemplate(String path) {
        this.path = path;
        refreshContent();

        this.engine = new VelocityEngine();
        this.engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        this.engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
    }

    public AdTemplate(Map<String, Object> mapping, String path) {
        this.path = path;
        this.attributes = mapping;
        this.engine = VelocityEngineConfiguration.getConfiguredEngine();
        refreshContent();
    }

    public String getPath() {
        return path;
    }

    public String getContent() {
        return content;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getProcessedTemplate() {
        return VelocityEngineUtils.mergeTemplateIntoString(engine, getTemplatePathVelocity(path), "UTF-8", attributes);
    }

    /**
     *
     * @return True if content was refreshed, false if not
     */
    public boolean refreshContent() {
        try {
            String fileContent = FileUtils.readFileToString(new File(getTemplatePath(path)));
            if (!Objects.equals(content, fileContent)) {
                this.content = fileContent;
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void serialize(AdTemplate adTemplate, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeObject(adTemplate.attributes);
        jsonGenerator.writeObject(adTemplate.content);
        jsonGenerator.writeObject(adTemplate.path);
        jsonGenerator.writeObject(adTemplate.getProcessedTemplate());
        jsonGenerator.writeEndObject();
    }

    public static String getTemplatePath(String t) {
        return AdTemplate.AdvertisementsPath + t + ".vm";
    }

    public static String getTemplatePathVelocity(String t) { return AdTemplate.AdvertisementsVelocityPath + t + ".vm"; }
}
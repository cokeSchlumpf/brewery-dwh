package common;

import com.google.common.collect.Maps;
import com.mitchellbosecke.pebble.PebbleEngine;
import scala.jdk.javaapi.CollectionConverters;

import java.io.StringWriter;
import java.util.Map;

/**
 * Util class to work with templates.
 *
 * @author Michael Wellner (michaelwellner@kpmg.com).
 */
public final class Templates {

    /**
     * Do not create instances.
     */
    private Templates() {

    }

    /**
     * Renders a template from a classpath resource with Jtwig.
     *
     * @param resourcePath The path of the resource
     * @return The rendered resource
     */
    public static String renderTemplateFromResources(String resourcePath) {
        return renderTemplateFromResources(resourcePath, Maps.newHashMap());
    }

    /**
     * Renders a template from a classpath resource with Jtwig using a model created from a map.
     *
     * @param resourcePath The path of the resource
     * @param values       A map of values to be injected into the model
     * @return The rendered resource
     */
    public static String renderTemplateFromResources(String resourcePath, Map<String, Object> values) {
        return Operators.suppressExceptions(() -> {
            var engine = new PebbleEngine.Builder().autoEscaping(false).build();
            var template = engine.getTemplate(resourcePath);
            var writer = new StringWriter();
            template.evaluate(writer, values);

            return writer.toString();
        });
    }

    /**
     * Same as {@link Templates#renderTemplateFromResources(String, Map)} but with Scala map.
     *
     * @param resourcePath The path of resource
     * @param values       A map of values to be injected into the model
     * @return The rendered resource
     */
    public static String renderTemplateFromResources(String resourcePath, scala.collection.Map<String, Object> values) {
        return renderTemplateFromResources(resourcePath, CollectionConverters.asJava(values));
    }

}

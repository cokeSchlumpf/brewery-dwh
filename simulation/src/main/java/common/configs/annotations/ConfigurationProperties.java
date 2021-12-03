package common.configs.annotations;

import com.typesafe.config.Config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used to annotate configuration class for
 * {@link maquette.core.config.Configs#mapToConfigClass(Class, Config)}.
 *
 * @author Michael Wellner (michael.wellner@gmail.com).
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigurationProperties {

    String prefix() default "";

}
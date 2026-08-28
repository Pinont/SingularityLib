package com.github.pinont.singularitylib.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark classes for automatic registration by the plugin.
 * This annotation is used to mark classes that need to be registered during the plugin's startup process.
 * <p>
 * Classes annotated with this will be processed by the plugin's registration system.
 * <p>
 * <b>Hint:</b> Use this annotation to register commands, events, or custom items.
 * It should only be used when the class extends {@code CustomItem}, {@code SimpleCommand}, or {@code Listener}.
 * <p>
 * <b>How the index works:</b> during compilation, the
 * {@code singularitylib-processor} annotation processor collects every class
 * annotated with this and writes it into
 * {@code META-INF/singularitylib/auto-register-index.properties} inside the built
 * jar. At plugin startup {@code Register} reads that index instead of scanning the
 * classpath with Reflections. The annotation keeps {@code CLASS} retention so it is
 * always present in compiled class files and visible to the processor.
 *
 * @see <a href="https://github.com/Pinont/SingularityLib">SingularityLib</a>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface AutoRegister {
    /**
     * Indicates that the annotated class should be automatically registered by the plugin.
     * This annotation is used to mark classes that need to be registered during the plugin's startup process.
     * <p>
     * Classes annotated with this will be processed by the plugin's registration system.
     * <p>
     * <b>Hint:</b> Use this annotation to register commands, events, or custom items.
     * It should only be used when the class extends {@code CustomItem}, {@code SimpleCommand}, or {@code Listener}.
     *
     * @return the registration value, defaults to empty string
     */
    String value() default "";
}

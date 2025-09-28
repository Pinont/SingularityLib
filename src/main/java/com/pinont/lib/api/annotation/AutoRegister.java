package com.pinont.lib.api.annotation;

public @interface AutoRegister {
    /**
     * Indicates that the annotated class should be automatically registered by the plugin.
     * This annotation is used to mark classes that need to be registered during the plugin's startup process.
     * <p>
     * Classes annotated with this will be processed by the plugin's registration system.
     * <p>
     * <b>Hint:</b> Use this annotation to register commands, events, or custom items.
     * It should only be used when the class extends {@code CustomItem}, {@code SimpleCommand}, or {@code Listener}.
     */
    String value() default "";
}

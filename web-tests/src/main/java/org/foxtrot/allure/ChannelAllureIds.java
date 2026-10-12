package org.foxtrot.allure;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allure TestOps ID для web- и desktop-прогонов одного и того же теста.
 * <p>
 * Web: {@code run.channel=web} → {@link #web()}<br>
 * Desktop: {@code run.channel=desktop} → {@link #desktop()}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface ChannelAllureIds {

    String web() default "";

    String desktop() default "";
}

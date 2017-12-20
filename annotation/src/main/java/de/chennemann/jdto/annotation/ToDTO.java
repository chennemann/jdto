package de.chennemann.jdto.annotation;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface ToDTO {
    String name() default "";
    String targetPackage() default "";
    String[] excludedFields() default "";
}

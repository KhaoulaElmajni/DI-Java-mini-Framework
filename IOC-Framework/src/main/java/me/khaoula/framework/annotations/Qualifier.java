package me.khaoula.framework.annotations;

import java.lang.annotation.*;


/**
 * Les objets de service doivent utiliser cette annotation
 * Cette annotation peut être utilisée pour éviter les conflits s'il existe
 * pls implémentations de la même interface
 */


@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Qualifier {
    String value() default "";
}
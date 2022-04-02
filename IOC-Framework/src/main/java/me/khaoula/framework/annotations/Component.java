package me.khaoula.framework.annotations;

import java.lang.annotation.*;

/**
 * la classe client doit utiliser cette annotation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Component {

}
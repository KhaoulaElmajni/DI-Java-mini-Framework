package me.khaoula.framework.utils;

import static org.burningwave.core.assembler.StaticComponentContainer.Fields;

import java.util.*;

import me.khaoula.framework.Injector;
import me.khaoula.framework.annotations.Autowired;
import me.khaoula.framework.annotations.Qualifier;
import org.burningwave.core.classes.FieldCriteria;


import java.lang.reflect.Field;

public class InjectionUtil {

    private InjectionUtil() {
        super();
    }

    /**
     * Effectuer l'injection de manière récursive,
     * pour chaque service à l'intérieur de la classe Client
     */
    public static void autowire(Injector injector, Class<?> classz, Object classInstance)
            throws InstantiationException, IllegalAccessException {
        Collection<Field> fields = Fields.findAllAndMakeThemAccessible(
                FieldCriteria.forEntireClassHierarchy().allThoseThatMatch(field ->
                        field.isAnnotationPresent(Autowired.class)
                ),
                classz
        );
        for (Field field : fields) {
            String qualifier = field.isAnnotationPresent(Qualifier.class)
                    ? field.getAnnotation(Qualifier.class).value()
                    : null;
            Object fieldInstance = injector.getBeanInstance(field.getType(), field.getName(), qualifier);
            Fields.setDirect(classInstance, field, fieldInstance);
            autowire(injector, fieldInstance.getClass(), fieldInstance);
        }
    }

}
package me.khaoula.framework;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

import me.khaoula.framework.annotations.Component;
import me.khaoula.framework.utils.InjectionUtil;
import org.burningwave.core.assembler.ComponentContainer;
import org.burningwave.core.classes.ClassCriteria;
import org.burningwave.core.classes.ClassHunter;
import org.burningwave.core.classes.SearchConfig;

/**
 * Injecteur, pour créer des objets pour toutes les classes Service. wiring automatique/injection
 * toutes les dépendances
 */
public class Injector {
    private Map<Class<?>, Class<?>> diMap;
    private Map<Class<?>, Object> applicationScope;

    private static Injector injector;

    private Injector() {
        super();
        diMap = new HashMap<>();
        applicationScope = new HashMap<>();
    }

    /**
     * Start application
     * @param mainClass
     */
    public static void startApplication(Class<?> mainClass) {
        try {
            synchronized (Injector.class) {
                if (injector == null) {
                    injector = new Injector();
                    injector.initFramework(mainClass);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static <T> T getService(Class<T> classz) {
        try {
            return injector.getBeanInstance(classz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * initialisation du framework injecteur
     */
    private void initFramework(Class<?> mainClass)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
        Class<?>[] classes = getClasses(mainClass.getPackage().getName(), true);
        ComponentContainer componentConatiner = ComponentContainer.getInstance();
        ClassHunter classHunter = componentConatiner.getClassHunter();
        String packageRelPath = mainClass.getPackage().getName().replace(".", "/");
        try (ClassHunter.SearchResult result = classHunter.findBy(
                SearchConfig.forResources(
                        packageRelPath
                ).by(ClassCriteria.create().allThoseThatMatch(cls -> {
                    return cls.getAnnotation(Component.class) != null;
                }))
        )) {
            Collection<Class<?>> types = result.getClasses();
            for (Class<?> implementationClass : types) {
                Class<?>[] interfaces = implementationClass.getInterfaces();
                if (interfaces.length == 0) {
                    diMap.put(implementationClass, implementationClass);
                } else {
                    for (Class<?> iface : interfaces) {
                        diMap.put(implementationClass, iface);
                    }
                }
            }

            for (Class<?> classz : classes) {
                if (classz.isAnnotationPresent(Component.class)) {
                    Object classInstance = classz.newInstance();
                    applicationScope.put(classz, classInstance);
                    InjectionUtil.autowire(this, classz, classInstance);
                }
            }
        };

    }

    /**
     * Get toutes les classes pour le package d'entrée
     */
    public Class<?>[] getClasses(String packageName, boolean recursive) throws ClassNotFoundException, IOException {
        ComponentContainer componentConatiner = ComponentContainer.getInstance();
        ClassHunter classHunter = componentConatiner.getClassHunter();
        String packageRelPath = packageName.replace(".", "/");
        SearchConfig config = SearchConfig.forResources(
                packageRelPath
        );
        if (!recursive) {
            config.findInChildren();
        }

        try (ClassHunter.SearchResult result = classHunter.findBy(config)) {
            Collection<Class<?>> classes = result.getClasses();
            return classes.toArray(new Class[classes.size()]);
        }
    }


    /**
     * Create & Get l'instance d'objet de la classe d'implémentation pour l'entrée
     * interface de services
     */
    @SuppressWarnings("unchecked")
    private <T> T getBeanInstance(Class<T> interfaceClass) throws InstantiationException, IllegalAccessException {
        return (T) getBeanInstance(interfaceClass, null, null);
    }

    /**
     * Overload getBeanInstance pour gérer le Qualisier et connexion automatique par type
     */
    public <T> Object getBeanInstance(Class<T> interfaceClass, String fieldName, String qualifier)
            throws InstantiationException, IllegalAccessException {
        Class<?> implementationClass = getImplimentationClass(interfaceClass, fieldName, qualifier);

        if (applicationScope.containsKey(implementationClass)) {
            return applicationScope.get(implementationClass);
        }

        synchronized (applicationScope) {
            Object service = implementationClass.newInstance();
            applicationScope.put(implementationClass, service);
            return service;
        }
    }

    /**
     * Get le nom de la classe d'implémentation pour le service d'interface d'entrée
     */
    private Class<?> getImplimentationClass(Class<?> interfaceClass, final String fieldName, final String qualifier) {
        Set<Entry<Class<?>, Class<?>>> implementationClasses = diMap.entrySet().stream()
                .filter(entry -> entry.getValue() == interfaceClass).collect(Collectors.toSet());
        String errorMessage = "";
        if (implementationClasses == null || implementationClasses.size() == 0) {
            errorMessage = "no implementation found for interface " + interfaceClass.getName();
        } else if (implementationClasses.size() == 1) {
            Optional<Entry<Class<?>, Class<?>>> optional = implementationClasses.stream().findFirst();
            if (optional.isPresent()) {
                return optional.get().getKey();
            }
        } else if (implementationClasses.size() > 1) {
            final String findBy = (qualifier == null || qualifier.trim().length() == 0) ? fieldName : qualifier;
            Optional<Entry<Class<?>, Class<?>>> optional = implementationClasses.stream()
                    .filter(entry -> entry.getKey().getSimpleName().equalsIgnoreCase(findBy)).findAny();
            if (optional.isPresent()) {
                return optional.get().getKey();
            } else {
                errorMessage = "There are " + implementationClasses.size() + " of interface " + interfaceClass.getName()
                        + " Expected single implementation or make use of @CustomQualifier to resolve conflict";
            }
        }
        throw new RuntimeErrorException(new Error(errorMessage));
    }
}
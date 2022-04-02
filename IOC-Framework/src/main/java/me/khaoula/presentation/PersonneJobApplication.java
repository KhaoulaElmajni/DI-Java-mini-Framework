package me.khaoula.presentation;

import me.khaoula.framework.Injector;
import me.khaoula.framework.PersonneJobClientComponent;

public class PersonneJobApplication {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        Injector.startApplication(PersonneJobApplication.class);
        Injector.getService(PersonneJobClientComponent.class).showPersonneJob();
        long endime = System.currentTimeMillis();
    }
}

package me.khaoula.framework.service.imp;


import me.khaoula.framework.annotations.Component;
import me.khaoula.framework.service.PersonneService;

@Component
public class PersonneServiceImp implements PersonneService {
    @Override
    public String getPersonneName() {
        return "PersonneName";
    }
}

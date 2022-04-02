package me.khaoula.service.imp;


import me.khaoula.framework.annotations.Autowired;
import me.khaoula.framework.annotations.Component;
import me.khaoula.service.JobService;
import me.khaoula.service.PersonneService;


@Component
public class PersonneServiceImp implements PersonneService {
    @Autowired
    JobService jobService;
    public PersonneServiceImp(Object o) {
    }
    public void setJobService(JobServiceImp jobService) {
    }
    @Override
    public String getPersonneName() {
        return "PersonneName";
    }
}

package me.khaoula.framework;


import me.khaoula.framework.annotations.Autowired;
import me.khaoula.framework.annotations.Component;
import me.khaoula.framework.annotations.Qualifier;
import me.khaoula.service.JobService;
import me.khaoula.service.PersonneService;
@Component
public class PersonneJobClientComponent {
    @Autowired
    private PersonneService personneService;
    @Autowired
    @Qualifier(value = "jobServiceImp")
    private JobService jobService;

    public void showPersonneJob() {
        String name = personneService.getPersonneName();
        double jobSalary = jobService.getJobSalary(name);
        System.out.println("\n\tPersonne Name: " + name + "\n\tJob salary: " + jobSalary);
    }
}

package teamapt.application.ui.services;


import teamapt.application.ui.domains.Employee;
import java.util.List;

public interface IEmployeeService {
    List<Employee> getAll();
    Employee getEmployee(String name);
}

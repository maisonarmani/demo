package teamapt.application.ui.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import teamapt.application.ui.domains.Employee;
import teamapt.application.ui.repository.EmployeeRepository;

import java.util.List;


@Service
public class EmployeeServiceImpl implements IEmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Employee> getAll() {
        return employeeRepository.findAll();
    }

    public Employee getEmployee(String name) {
        return employeeRepository.findOneByName(name);
    }


}

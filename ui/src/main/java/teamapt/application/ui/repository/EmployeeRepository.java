package teamapt.application.ui.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import teamapt.application.ui.domains.Employee;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    @Override
    List<Employee> findAll(Sort sort);

    @Override
    List<Employee> findAllById(Iterable<Long> longs);

    @Query("SELECT e.name FROM Employee e WHERE e.name = :name")
    Employee findOneByName(@Param("name") String name);

}

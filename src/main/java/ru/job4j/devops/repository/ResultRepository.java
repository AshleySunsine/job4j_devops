package ru.job4j.devops.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.job4j.devops.models.Result;
import java.util.List;

@Repository
public interface ResultRepository extends CrudRepository<Result, Long> {
    List<Result> findAll();
}

package ru.job4j.devops.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.job4j.devops.models.CalcEvent;

import java.util.List;

public interface CalcEventRepository extends JpaRepository<CalcEvent, Long> {
    List<CalcEvent> findByType(String type);
}
package ru.job4j.devops.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.devops.models.Result;
import ru.job4j.devops.repository.ResultRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class ResultService {

    private final ResultRepository resultRepository;

    public List<Result> findAll() {
        return resultRepository.findAll();
    }
}
package ru.job4j.devops.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.job4j.devops.models.Result;
import ru.job4j.devops.models.TwoArgs;
import ru.job4j.devops.services.ResultService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("calc")
public class CalcController {
    @Autowired
    private ResultService resultService;

    @PostMapping("summarise")
    public ResponseEntity<Result> summarise(@RequestBody TwoArgs twoArgs) {
        var result = twoArgs.getFirst() + twoArgs.getSecond();
        return ResponseEntity.ok(new Result(999L, twoArgs.getFirst(), twoArgs.getSecond(), result, LocalDate.now(), "+"));
    }

    @PostMapping("times")
    public ResponseEntity<Result> times(@RequestBody TwoArgs twoArgs) {
        var result = twoArgs.getFirst() * twoArgs.getSecond();
        return ResponseEntity.ok(new Result(999L, twoArgs.getFirst(), twoArgs.getSecond(), result, LocalDate.now(), "+"));
    }

    @GetMapping("/")
    public ResponseEntity<List<Result>> logs() {
        return ResponseEntity.ok(resultService.findAll());
    }
}

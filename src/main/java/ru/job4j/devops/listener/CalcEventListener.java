package ru.job4j.devops.listener;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.job4j.devops.models.CalcEvent;
import ru.job4j.devops.repository.CalcEventRepository;

@Component
@AllArgsConstructor
@Slf4j
public class CalcEventListener {
    private final CalcEventRepository repository;

    //@KafkaListener(topics = "calcEvent", groupId = "job4j")
    public void event(CalcEvent calcEvent) {
        log.debug("Calculation event type:{}, result:{}", calcEvent.getType(), calcEvent.getResult());
        repository.save(calcEvent);
    }
}

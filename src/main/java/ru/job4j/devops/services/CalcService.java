package ru.job4j.devops.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.devops.models.CalcEvent;
import ru.job4j.devops.models.User;
import ru.job4j.devops.repository.CalcEventRepository;

@Service
@Transactional
public class CalcService {

    private final CalcEventRepository calcEventRepository;

    public CalcService(CalcEventRepository calcEventRepository) {
        this.calcEventRepository = calcEventRepository;
    }

    /**
     * Складывает два числа и сохраняет событие в базу данных
     * @param user пользователь, выполняющий операцию
     * @param first первое число
     * @param second второе число
     * @return результат сложения
     */
    public int add(User user, int first, int second) {
        int result = first + second;

        CalcEvent event = new CalcEvent(
                user,
                first,
                second,
                result,
                "ADDITION"
        );

        calcEventRepository.save(event);

        return result;
    }
}
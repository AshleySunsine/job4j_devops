package ru.job4j.devops;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import ru.job4j.devops.controllers.CalcController;
import ru.job4j.devops.models.Result;
import ru.job4j.devops.models.TwoArgs;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CalcApplicationTest {

    @Test
    void contextLoads() {
    }

    @Test
    void mainMethodTest() {
        CalcApplication.main(new String[]{});
    }

    @Test
    public void whenNegativeNumber() {
        var input = new TwoArgs(-1, -1);
        var expected = new Result(999L, -1D, -1D, -2D, LocalDate.now(), "Sum");
        var output = new CalcController().summarise(input);
        assertThat(output.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(output.getBody()).isEqualTo(expected);
    }

    @Test
    public void whenNegativeNumberTwo() {
        var input = new TwoArgs(-3, -1);
        var expected = new Result(999L, -3D, -1D, -4D, LocalDate.now(), "Sum");
        var output = new CalcController().summarise(input);
        assertThat(output.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(output.getBody()).isEqualTo(expected);
    }

    @Test
    public void whenNegativeNumberthree() {
        var input = new TwoArgs(-3, -1);
        var expected = new Result(999L, -3D, -1D, -4D, LocalDate.now(), "Sum");
        var output = new CalcController().summarise(input);
        assertThat(output.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(output.getBody()).isEqualTo(expected);
    }
}
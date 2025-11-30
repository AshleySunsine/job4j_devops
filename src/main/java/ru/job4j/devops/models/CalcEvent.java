package ru.job4j.devops.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "calc_events")
public class CalcEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private int first;
    private int second;
    private int result;

    @Column(name = "create_date")
    private LocalDateTime createDate;

    private String type;

    public CalcEvent(User user, int first, int second, int result, String type) {
        setUser(user);
        this.first = first;
        this.second = second;
        this.result = result;
        this.createDate = LocalDateTime.now();
        this.type = type;
    }
}

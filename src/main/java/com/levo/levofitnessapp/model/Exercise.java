package com.levo.levofitnessapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "exercise")
@Data   // getters, setters
@NoArgsConstructor
@AllArgsConstructor
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "exercise_name")
    private String exercise_name;

//    foreign key for category - can't be empty
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

//    user that created the exercise - can be empty
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user_id;
}

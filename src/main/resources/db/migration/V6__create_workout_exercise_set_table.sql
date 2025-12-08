CREATE TABLE workout_exercise_set (
    id BIGSERIAL PRIMARY KEY,
    workout_exercise_id BIGINT NOT NULL,
    set_number INT NOT NULL,
    reps INT NOT NULL,
    weight_kg DECIMAL(5,2),
    CONSTRAINT fk_workout_exercise_set
        FOREIGN KEY (workout_exercise_id)
            REFERENCES workout_exercise(id)
            ON DELETE CASCADE
);


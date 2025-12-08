CREATE TABLE workout_exercise (
    id BIGSERIAL PRIMARY KEY,
    workout_session_id BIGINT NOT NULL,
    exercise_id BIGINT NOT NULL,
    notes TEXT,
    CONSTRAINT fk_workout_exercise_session
        FOREIGN KEY (workout_session_id)
            REFERENCES workout_session(id)
            ON DELETE CASCADE,
    CONSTRAINT fk_workout_exercise_exercise
        FOREIGN KEY (exercise_id)
            REFERENCES exercise(id)
            ON DELETE CASCADE
);



CREATE TABLE exercise (
    id BIGSERIAL PRIMARY KEY,
    exercise_name VARCHAR(255) NOT NULL,
    category_id BIGINT NOT NULL,
    user_id BIGINT NULL,
    CONSTRAINT fk_exercise_category
        FOREIGN KEY (category_id)
            REFERENCES exercise_category(id)
            ON DELETE CASCADE,
    CONSTRAINT fk_exercise_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE SET NULL
);


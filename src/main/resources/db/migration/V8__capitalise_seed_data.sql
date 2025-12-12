-- Capitalize category names
UPDATE exercise_category SET category_name = 'Legs' WHERE category_name = 'legs';
UPDATE exercise_category SET category_name = 'Arms' WHERE category_name = 'arms';
UPDATE exercise_category SET category_name = 'Back' WHERE category_name = 'back';

-- Capitalize exercise names
UPDATE exercise SET exercise_name = 'Squats' WHERE exercise_name = 'squats';
UPDATE exercise SET exercise_name = 'Leg Press' WHERE exercise_name = 'leg press';
UPDATE exercise SET exercise_name = 'Lunges' WHERE exercise_name = 'lunges';

UPDATE exercise SET exercise_name = 'Bicep Curls' WHERE exercise_name = 'bicep curls';
UPDATE exercise SET exercise_name = 'Tricep Dips' WHERE exercise_name = 'tricep dips';
UPDATE exercise SET exercise_name = 'Hammer Curls' WHERE exercise_name = 'hammer curls';

UPDATE exercise SET exercise_name = 'Lat Pull Down' WHERE exercise_name = 'lat pull down';
UPDATE exercise SET exercise_name = 'Cable Rows' WHERE exercise_name = 'cable rows';
UPDATE exercise SET exercise_name = 'Pull-Ups' WHERE exercise_name = 'pull-ups';

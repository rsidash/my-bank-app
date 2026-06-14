INSERT INTO accounts (login, name, birthdate, sum) VALUES ('ivanov', 'Иванов Иван', '2001-01-01', 100) ON CONFLICT (login) DO NOTHING;
INSERT INTO accounts (login, name, birthdate, sum) VALUES ('petrov', 'Петров Петр', '1995-05-15', 200) ON CONFLICT (login) DO NOTHING;
INSERT INTO accounts (login, name, birthdate, sum) VALUES ('sidorov', 'Сидоров Сидор', '1990-10-20', 300) ON CONFLICT (login) DO NOTHING;

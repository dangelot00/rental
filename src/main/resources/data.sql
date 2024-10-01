/* user mocks */

/* add test user with encrypted password 123 and 1000 credits */
INSERT INTO APP_USER (username, password, credit, has_missed_film, total_rentals)
VALUES ('test', '$2a$10$xH6FBE7edBMoZ0UIySkdAunEdgPME.P/enzeEVGGLSGBqe3AIaNIy', 1000, 0, 1);
/* add test user with encrypted password 123 and 10 credits */
INSERT INTO APP_USER (username, password, credit, has_missed_film, total_rentals)
VALUES ('test2', '$2a$10$xH6FBE7edBMoZ0UIySkdAunEdgPME.P/enzeEVGGLSGBqe3AIaNIy', 10, 0, 1);

/* film mocks */

INSERT INTO Film (title, genre)
VALUES ('film1', 'STANDARD');
INSERT INTO Film (title, genre)
VALUES ('film2', 'LAST_EXIT');
INSERT INTO Film (title, genre)
VALUES ('film3', 'CHILDREN');

/* rental mocks */

INSERT INTO Rental (user_id, film_id, rental_date, due_date, return_date, cost, deposit, is_late)
VALUES (1, 1, '2024-09-01', '2024-09-08', null, 10.00, 5.00, FALSE);
INSERT INTO Rental (user_id, film_id, rental_date, due_date, return_date, cost, deposit, is_late)
VALUES (2, 2, '2024-09-02', '2024-09-09', '2024-09-08', 15.00, 5.00, FALSE);
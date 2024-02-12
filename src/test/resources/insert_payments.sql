insert into person (id, email, first_name, last_name, password, phone_number, role)
VALUES (5, 'email@mail.ru', 'john', 'johnson', 'pass', '89099999999', 'CUSTOMER');

insert into customer (person_id, customer_type) VALUES (5, 'REGULAR');

insert into loan (id, end_date, interest_rate, loan_amount, loan_type, start_date, status, term, customer_id)
VALUES (1, '2025-02-02', 10, 300000, 'PERSONAL', '2024-02-02', 'ACTIVE', 12, 5);

insert into loan_payment (id, interest_amount, payment_amount, principal_amount, scheduled_payment_date, paid_amount, residual_amount, loan_id)
VALUES (1, 2377.05, 26374.77, 23997.72, '2024-03-02', 0, 276002.28, 1);
insert into loan_payment (id, interest_amount, payment_amount, principal_amount, scheduled_payment_date, paid_amount, residual_amount, loan_id)
VALUES (2, 2337.72, 26374.77, 24037.05, '2024-04-02', 0, 251965.23, 1);
insert into loan_payment (id, interest_amount, payment_amount, principal_amount, scheduled_payment_date, paid_amount, residual_amount, loan_id)
VALUES (3, 2065.29, 26374.77, 24309.48, '2024-05-02', 0, 251965.23, 1);
insert into loan_payment (id, interest_amount, payment_amount, principal_amount, scheduled_payment_date, paid_amount, residual_amount, loan_id)
VALUES (4, 2065.29, 26374.77, 24309.48, '2024-06-02', 0, 251965.23, 1);
insert into loan_payment (id, interest_amount, payment_amount, principal_amount, scheduled_payment_date, paid_amount, residual_amount, loan_id)
VALUES (5, 2065.29, 26374.77, 24309.48, '2024-07-02', 0, 251965.23, 1);
insert into loan_payment (id, interest_amount, payment_amount, principal_amount, scheduled_payment_date, paid_amount, residual_amount, loan_id)
VALUES (6, 2065.29, 26374.77, 24309.48, '2024-08-02', 0, 251965.23, 1);
insert into loan_payment (id, interest_amount, payment_amount, principal_amount, scheduled_payment_date, paid_amount, residual_amount, loan_id)
VALUES (7, 2065.29, 26374.77, 24309.48, '2024-09-02', 0, 251965.23, 1);
insert into loan_payment (id, interest_amount, payment_amount, principal_amount, scheduled_payment_date, paid_amount, residual_amount, loan_id)
VALUES (8, 2065.29, 26374.77, 24309.48, '2024-10-02', 0, 251965.23, 1);
insert into loan_payment (id, interest_amount, payment_amount, principal_amount, scheduled_payment_date, paid_amount, residual_amount, loan_id)
VALUES (9, 2065.29, 26374.77, 24309.48, '2024-11-02', 0, 251965.23, 1);
insert into loan_payment (id, interest_amount, payment_amount, principal_amount, scheduled_payment_date, paid_amount, residual_amount, loan_id)
VALUES (10, 2065.29, 26374.77, 24309.48, '2024-12-02', 0, 251965.23, 1);
insert into loan_payment (id, interest_amount, payment_amount, principal_amount, scheduled_payment_date, paid_amount, residual_amount, loan_id)
VALUES (11, 2065.29, 26374.77, 24309.48, '2025-01-02', 0, 251965.23, 1);
insert into loan_payment (id, interest_amount, payment_amount, principal_amount, scheduled_payment_date, paid_amount, residual_amount, loan_id)
VALUES (12, 2065.29, 26374.77, 24309.48, '2025-05-02', 0, 251965.23, 1);
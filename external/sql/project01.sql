-- Refresh tables
drop table if exists employees cascade;
drop table if exists events cascade;
drop table if exists grade_formats cascade;
drop table if exists requests cascade;

create table if not exists employees (
	username varchar primary key,
	password_cred varchar,
	first_name varchar,
	last_name varchar,
	employee_type varchar,
	reimbursement_funds numeric(6, 2),
	funds numeric(6, 2)	-- Soley for testing purposes since I don't have a bank account to send funds to
);

create table if not exists events (
	event_type varchar primary key,
	reimbursement_percentage numeric(3, 2)
);

create table if not exists grade_formats (
	format varchar primary key,
	default_cutoff varchar,
	presentation_required boolean
);

create table if not exists requests (
	id serial primary key,
	employee_username varchar references employees(username),
	event_type varchar references events(event_type),
	status varchar,
	request_cost numeric(6, 2),
	reimbursement_amount numeric(6, 2),
	grade_format varchar references grade_formats(format),
	grade varchar,
	cutoff varchar,
	justification varchar,
	submission_date timestamp,
	start_date timestamp,
	event_location varchar,
	event_description varchar,
	urgent boolean,
	exceeds_funds boolean,
	reason varchar
);

-- Initial data set
insert into employees values
('admin1', 'secret1', 'Wolf', 'Flow', 'MANAGER', 500.00, 50.00),
('admin2', 'secret2', 'Wolf2', 'Flow2', 'MANAGER', 0.00, 0.00),
('admin3', 'secret3', 'Wolf3', 'Flow3', 'MANAGER', 1000.00, 100.00),
('user1', 'pass1', 'Alice', 'Apple', 'EMPLOYEE', 1000.00, 100.00),
('user2', 'pass2', 'Bob', 'Bacon', 'EMPLOYEE', 0.00, 0.00),
('user3', 'pass3', 'Carl', 'Cake', 'EMPLOYEE', 500.50, 50.50),
('user4', 'pass4', 'David', 'Drink', 'EMPLOYEE', 1000.00, 100.00);

insert into events values
('UNIVERSITY', 0.80),
('SEMINAR', 0.60),
('CERTIFICATION_PREP', 0.75),
('CERTIFICATION', 1.00),
('TRAINING', 0.90),
('OTHER', 0.30);

insert into grade_formats values
('LETTER', 'C', false),
('PASSFAIL', 'P', true);

insert into requests values
(default, 'admin1', 'UNIVERSITY',  	 'PENDING_APPROVAL',  	500.00,  100.00,  'LETTER',   'B',   'C', 'Because I can', 	'2022-06-30 12:13:14', '2022-06-30 12:13:14', 'loc', 'desc', true,  false, 'my reason'),
(default, 'admin1', 'OTHER',  		 'APPROVED',  			20.00,   100.00,  'LETTER',   'B',   'C', 'Because I can', 	'2022-06-30 12:13:14', '2022-06-30 12:13:14', 'loc', 'desc', true,  false, 'my reason'),
(default, 'user1',  'CERTIFICATION', 'PENDING_REVIEW',      100.00,  100.00,  'PASSFAIL', null,  'P', 'just',           '2022-06-09 12:13:14', '2022-06-01 12:13:14', 'loc', 'desc', false, false, null),
(default, 'user1',  'UNIVERSITY',    'PENDING_APPROVAL',  	500.00,  100.00,  'LETTER',   'B',   'C', 'Because I can', 	'2022-06-30 12:13:14', '2022-06-30 12:13:14', 'loc', 'desc', true,  false, 'my reason'),
(default, 'user1',  'CERTIFICATION', 'APPROVED', 			20.99,   10.00,   'PASSFAIL', null,  'P', 'Why Not', 		'2022-06-30 12:13:14', '2022-06-30 12:13:14', 'loc', 'desc', false, false,  null),
(default, 'user2',  'CERTIFICATION', 'PENDING_GRADE', 		120.50,  60.00,   'PASSFAIL', 'P',   'P', 'Woot',		    '2022-06-30 12:13:14', '2022-06-30 12:13:14', 'loc', 'desc', false, true,   null),
(default, 'user2',  'TRAINING', 	 'REJECTED', 			1500.00, 750.00,  'PASSFAIL', 'F',   'P', 'Gimmi Teh Cash', '2022-06-30 12:13:14', '2022-06-30 12:13:14', 'loc', 'desc', true,  true,   null),
(default, 'user3',  'OTHER', 	  	 'CANCELLED', 			15.00,   15.00,   'LETTER',   null,  'B', 'Testing', 		'2022-06-30 12:13:14', '2022-06-30 12:13:14', 'loc', 'desc', false, false,  'my reason'),
(default, 'admin1', 'UNIVERSITY',  	 'PENDING_APPROVAL',  	500.00,  100.00,  'LETTER',   'B',   'C', 'Because I can', 	'2022-06-30 12:13:14', '2022-06-30 12:13:14', 'loc', 'desc', true,  false, 'my reason');

-- View data
select * from employees;
select * from events;
select * from grade_formats;
select * from requests;

select first_name, last_name, r.*
from employees, requests r
where username = employee_username
order by id;
create sequence account_seq start with 1 increment by 1;
create sequence branch_seq start with 1 increment by 1;
create sequence loan_payment_seq start with 1 increment by 1;
create sequence loan_seq start with 1 increment by 1;
create sequence person_seq start with 2 increment by 1;
create sequence transaction_seq start with 1 increment by 1;

create table account (
    id bigint not null,
    current_balance numeric(38,2),
    account_number varchar(20),
    date_opened date,
    account_status varchar(255) check (account_status in ('OPEN','SUSPENDED','CLOSED')),
    account_type varchar(255) check (account_type in ('CHECKING','SAVINGS','MMA','CD')),
    branch_id bigint,
    date_closed date,
    primary key (id));

create table branch (
    id bigint not null,
    name varchar(255),
    bic varchar(9) unique,
    balance numeric(38,2),
    phone_number varchar(20),
    primary key (id));

create table branch_address (
    branch_id bigint not null,
    city varchar(255),
    street varchar(255),
    house varchar(255),
    primary key (branch_id));

create table customer (
    person_id bigint not null,
    customer_type varchar(255) check (customer_type in ('REGULAR','PREMIUM')),
    primary key (person_id));

create table customer_accounts (
    account_id bigint not null,
    customer_id bigint not null);

create table loan (
    id bigint not null,
    interest_rate numeric(38,2),
    loan_amount numeric(38,2) not null,
    term integer not null,
    start_date date not null,
    end_date date,
    customer_id bigint,
    loan_type varchar(255) check (loan_type in ('MORTGAGE','PERSONAL','BUSINESS')),
    status varchar(255) check (status in ('ACTIVE','CANCELED','CLOSED')),
    primary key (id));

create table loan_payment (
    id bigint not null,
    scheduled_payment_date date,
    interest_amount numeric(38,2),
    principal_amount numeric(38,2),
    payment_amount numeric(38,2),
    paid_amount numeric(38,2),
    residual_amount numeric(38,2),
    paid_date date,
    loan_id bigint,
    primary key (id));

create table person (
    id bigint not null,
    email varchar(255),
    password varchar(255),
    first_name varchar(255),
    last_name varchar(255),
    phone_number varchar(20),
    dob date,
    role varchar(255) check (role in ('USER','CUSTOMER','MANAGER','ADMIN')),
    primary key (id));

create table person_address (
    person_id bigint not null,
    city varchar(255),
    street varchar(255),
    house varchar(255),
    apt varchar(255),
    primary key (person_id));

create table transaction (
    id bigint not null,
    transaction_type varchar(255) check (transaction_type in ('DEPOSIT','WITHDRAWAL','PURCHASE','TRANSFER','LOAN','LOAN_PAYMENT')),
    amount numeric(38,2),
    account_id bigint,
    loan_payment_id bigint,
    transaction_date timestamp(6),
    related_transaction_id bigint unique,
    primary key (id));

alter table if exists account
    add constraint account_branch_id
        foreign key (branch_id) references branch;

alter table if exists branch_address
    add constraint branch_address_branch_id
        foreign key (branch_id) references branch;

alter table if exists customer
    add constraint customer_person_id
        foreign key (person_id) references person;

alter table if exists customer_accounts
    add constraint customer_accounts_account_ids
        foreign key (account_id) references account;

alter table if exists customer_accounts
    add constraint customer_accounts_customer_ids
        foreign key (customer_id) references customer;

alter table if exists loan
    add constraint loan_customer_id
        foreign key (customer_id) references customer;

alter table if exists loan_payment
    add constraint loan_payment_loan_id
        foreign key (loan_id) references loan;

alter table if exists person_address
    add constraint person_address_person_id
        foreign key (person_id) references person;

alter table if exists transaction
    add constraint transaction_account_id
        foreign key (account_id) references account;

alter table if exists transaction
    add constraint transaction_loan_payment_id
        foreign key (loan_payment_id) references loan_payment;

alter table if exists transaction
    add constraint transaction_related_transaction_id
        foreign key (related_transaction_id) references transaction;
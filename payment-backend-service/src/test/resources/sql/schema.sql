drop table if exists test_payment;

create table test_payment
(
    id     varchar(255) not null,
    status varchar(255),
    primary key (id)
);

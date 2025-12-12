CREATE SCHEMA IF NOT EXISTS test;

CREATE TABLE IF NOT EXISTS test.user_table (
  ut_username     VARCHAR(30) PRIMARY KEY,
  ut_password     VARCHAR(100) NOT NULL,
  ut_email        VARCHAR(35),
  ut_role         VARCHAR(25),
  ut_registered   TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ut_last_visit   TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE IF NOT EXISTS test.task_table (
  tt_id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  tt_title         VARCHAR(50) NOT NULL,
  tt_description   TEXT,
  tt_status        VARCHAR(30) NOT NULL,
  tt_created       TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  tt_modified      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  tt_user_id       VARCHAR(30),
  CONSTRAINT fk_user FOREIGN KEY (tt_user_id) REFERENCES test.user_table(ut_username) ON DELETE SET NULL
);

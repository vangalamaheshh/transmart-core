-- Role: galaxy

-- DROP ROLE galaxy;

CREATE ROLE galaxy LOGIN
  ENCRYPTED PASSWORD 'md5feade312f82b850e646b3671c99acb28'
  NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE NOREPLICATION;
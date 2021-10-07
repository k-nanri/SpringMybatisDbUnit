-- Table: public.document

-- DROP TABLE public.document;
DROP TABLE IF EXISTS document;
DROP SEQUENCE IF EXISTS document_id_seq;

CREATE SEQUENCE document_id_seq;
CREATE TABLE document
(
  id integer NOT NULL DEFAULT nextval('document_id_seq'),
  name text ,
  registrant character varying(50),
  registration_time timestamp without time zone,
  editor character varying(50),
  edit_time timestamp without time zone
  );
  
  ALTER TABLE document ADD PRIMARY KEY (id, name);
  
  
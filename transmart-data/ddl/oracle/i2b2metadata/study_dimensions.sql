--
-- Type: TABLE; Owner: I2B2METADATA; Name: STUDY_DIMENSIONS
--
 CREATE TABLE "I2B2METADATA"."STUDY_DIMENSIONS"
  (	"DIMENSION_DESCRIPTION_ID" NUMBER NOT NULL ENABLE,
"STUDY_ID" NUMBER NOT NULL ENABLE,
 PRIMARY KEY ("STUDY_ID", "DIMENSION_DESCRIPTION_ID")
 USING INDEX
 TABLESPACE "TRANSMART"  ENABLE
  ) SEGMENT CREATION DEFERRED
NOCOMPRESS LOGGING
 TABLESPACE "TRANSMART" ;
--
-- Type: REF_CONSTRAINT; Owner: I2B2METADATA; Name: FK_9VVV5M5NBXSU2LKBEEFTEVD5J
--
ALTER TABLE "I2B2METADATA"."STUDY_DIMENSIONS" ADD CONSTRAINT "FK_9VVV5M5NBXSU2LKBEEFTEVD5J" FOREIGN KEY ("DIMENSION_DESCRIPTION_ID")
 REFERENCES "I2B2METADATA"."DIMENSION_DESCRIPTION" ("ID") ENABLE;
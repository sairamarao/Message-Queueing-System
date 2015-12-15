DROP FUNCTION IF EXISTS createQueue();
CREATE OR REPLACE FUNCTION createQueue() RETURNS text AS $$
DECLARE
	qid INTEGER;
BEGIN
	INSERT INTO queue(queue_name) VALUES('q') RETURNING queue_id INTO qid;
	RETURN qid::text;
END;
$$
LANGUAGE 'plpgsql';

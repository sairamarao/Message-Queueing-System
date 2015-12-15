DROP FUNCTION IF EXISTS deleteQueue(integer);
CREATE OR REPLACE FUNCTION deleteQueue(qid integer) RETURNS void AS $$
BEGIN
	DELETE FROM client WHERE client_id = cid;
END;
$$
LANGUAGE 'plpgsql';

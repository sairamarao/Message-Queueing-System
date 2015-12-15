DROP FUNCTION IF EXISTS getQueues(integer);
CREATE OR REPLACE FUNCTION getQueues(rid integer) RETURNS SETOF text AS $$

BEGIN
	PERFORM * FROM client WHERE client_id = rid;
	IF FOUND THEN
		BEGIN
			RETURN QUERY SELECT queue_id FROM message WHERE ( rec_id = rid OR rec_id IS NULL) LIMIT 1;
		EXCEPTION
			WHEN NO_DATA_FOUND THEN
				RAISE EXCEPTION 'No queues found';
		END;
	ELSE 
		RAISE EXCEPTION 'Receiver not found';
	END IF;
END;
$$
LANGUAGE 'plpgsql';

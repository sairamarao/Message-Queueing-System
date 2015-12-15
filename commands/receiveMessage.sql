DROP FUNCTION IF EXISTS receiveMessage(integer,integer);
CREATE OR REPLACE FUNCTION receiveMessage(rid integer,pop integer) RETURNS SETOF text AS $$

BEGIN
	PERFORM * FROM client WHERE client_id = rid;		
	IF FOUND THEN	
		BEGIN
			IF pop = 1 THEN
				RETURN QUERY DELETE FROM message WHERE message_id IN 
				(SELECT message_id FROM message WHERE ( rec_id = rid OR rec_id IS NULL) ORDER BY t ASC LIMIT 1) RETURNING chat;
			ELSE 
				RETURN QUERY SELECT chat FROM message WHERE ( rec_id = rid OR rec_id IS NULL) ORDER BY t ASC LIMIT 1;
			END IF;
		EXCEPTION 
			WHEN NO_DATA_FOUND THEN
				RAISE EXCEPTION 'No message found';
		END;
	ELSE
		RAISE 'Couldnt find the Receiver';
	END IF; 
END;
$$
LANGUAGE 'plpgsql';


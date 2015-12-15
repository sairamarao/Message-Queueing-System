DROP FUNCTION IF EXISTS receiveMessageS(integer,integer,integer);
CREATE OR REPLACE FUNCTION receiveMessageS(sid integer,rid integer,pop integer) RETURNS text AS $$

BEGIN
	PERFORM * FROM client WHERE client_id = rid;
	IF FOUND
		PERFORM * FROM client WHERE client_id = sid;		
		IF FOUND		
			BEGIN
				IF POP == 1
					RETURN QUERY DELETE FROM message WHERE message_id IN 
					(SELECT message_id FROM message WHERE send_id = sid AND ( rec_id = rid OR rec_id = NULL) ORDER BY t ASC LIMIT 1) 
					RETURNING chat;
				ELSE 
					RETURN QUERY SELECT chat FROM message WHERE send_id = sid AND ( rec_id = rid OR rec_id = NULL) ORDER BY t ASC LIMIT 1;
				END IF;
			EXCEPTION THEN
				RETURN 'Failure';
			END;
		ELSE
			RETURN 'No Sender Found';
		END IF;
	ELSE
		RETURN 'No Receiver Found'; 
	END IF;
END;
$$
LANGUAGE 'plpgsql';

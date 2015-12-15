DROP FUNCTION IF EXISTS receiveMessageQ(integer,integer,integer);
CREATE OR REPLACE FUNCTION receiveMessageQ(qid integer,rid integer,pop integer) RETURNS text AS $$

BEGIN
	PERFORM * FROM queue WHERE queue_id = qid;
	IF FOUND
		PERFORM * FROM client WHERE client_id = rid;		
		IF FOUND		
			BEGIN
				IF POP == 1
					RETURN QUERY DELETE FROM message WHERE message_id IN 
					(SELECT message_id FROM message WHERE queue_id = qid AND ( rec_id = rid OR rec_id = NULL) ORDER BY t ASC LIMIT 1) 
					RETURNING chat;
				ELSE 
					RETURN QUERY SELECT chat FROM message WHERE queue_id = qid AND ( rec_id = rid OR rec_id = NULL) ORDER BY t ASC LIMIT 1;
				END IF;
			EXCEPTION THEN
				RETURN 'Failure';
			END;
		ELSE
			RETURN 'No Receiver Found';
		END IF;
	ELSE
		RETURN 'No Queue Found'; 
	END IF;
END;
$$
LANGUAGE 'plpgsql';

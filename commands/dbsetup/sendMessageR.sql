DROP FUNCTION IF EXISTS sendMessageR(integer,integer,integer,text);
CREATE OR REPLACE FUNCTION sendMessageR(qid integer,sid integer,rid integer,mess text) RETURNS text AS $$
DECLARE
	ret text;
BEGIN
	PERFORM * FROM client WHERE client_id = rid;
	IF FOUND THEN
		BEGIN
			INSERT INTO message(queue_id,rec_id,sen_id,chat,t) VALUES(qid,rid,sid,mess,now());
			ret = 'Success';
		EXCEPTION WHEN OTHERS THEN	
			ret = 'Failure to insert message';
		END;
		RETURN ret;
	ELSE
		RETURN 'Failure. No such Receiver.';
	END IF;
END;
$$
LANGUAGE 'plpgsql';

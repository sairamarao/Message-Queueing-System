DROP FUNCTION IF EXISTS sendMessage(integer,integer,text);
CREATE OR REPLACE FUNCTION sendMessage(qid integer,sid integer,mess text) RETURNS text AS $$
DECLARE
	ret text;
BEGIN
	BEGIN
		INSERT INTO message(queue_id,rec_id,sen_id,chat,t) VALUES(qid,NULL,sid,mess,now());
		ret = 'Success';
		RETURN ret;
	EXCEPTION 
		WHEN OTHERS THEN
			RAISE EXCEPTION 'Inserting';
			ret = 'Failure';
			RETURN ret;
	END;
END;
$$
LANGUAGE 'plpgsql';

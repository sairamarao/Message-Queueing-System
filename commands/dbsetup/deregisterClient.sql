DROP FUNCTION IF EXISTS deregisterClient(integer);
CREATE OR REPLACE FUNCTION deregisterClient(cid integer) RETURNS text AS $$
DECLARE 
	ret text;
BEGIN
	BEGIN
		DELETE FROM client WHERE client_id = cid;
		ret = 'Success';
		RETURN ret;
	EXCEPTION
		WHEN OTHERS THEN
			RAISE EXCEPTION 'Cant delete client';
			ret = 'Failure';
			RETURN ret;
	END;
END;
$$
LANGUAGE 'plpgsql';

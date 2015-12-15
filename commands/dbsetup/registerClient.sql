DROP FUNCTION IF EXISTS registerClient(text, integer, integer);
CREATE OR REPLACE FUNCTION registerClient(client_address text,host_id integer, send_receive integer) RETURNS text AS $$
DECLARE
	cid INTEGER;
BEGIN
	BEGIN
		INSERT INTO client(ip_address,id,sr) VALUES(client_address,host_id,send_receive) RETURNING client_id INTO cid;
		RETURN cid::text;
	EXCEPTION
		WHEN OTHERS THEN
			RAISE NOTICE 'Couldnt register client';
			RETURN 'null';
	END;
END;
$$
LANGUAGE 'plpgsql';

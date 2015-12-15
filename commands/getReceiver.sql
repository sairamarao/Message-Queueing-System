DROP FUNCTION IF EXISTS getReceiver(text);
CREATE OR REPLACE FUNCTION getReceiver(address text) RETURNS text AS $$
DECLARE
	rid integer;
BEGIN
	BEGIN
		SELECT client_id FROM client WHERE ip_address = address AND sr = '1' ORDER BY RANDOM() LIMIT 1 INTO rid;
		RETURN rid::text;
	EXCEPTION
		WHEN OTHERS THEN
			RAISE EXCEPTION 'No Receivers Found';
			RETURN 'null';
	END;	
END;
$$
LANGUAGE 'plpgsql';

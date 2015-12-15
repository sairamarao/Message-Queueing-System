DROP FUNCTION IF EXISTS receiveMessage(integer,integer);
CREATE OR REPLACE FUNCTION receiveMessage(rid integer,pop integer) RETURNS text AS $$
DECLARE
        ret text;
        mid INTEGER;
BEGIN
        PERFORM * FROM client WHERE client_id = rid;
        IF FOUND THEN
                BEGIN
                        IF pop = 1 THEN
                                SELECT message_id,chat INTO mid,ret FROM message WHERE ( rec_id = rid OR rec_id IS NULL) LIMIT 1 FOR UPDATE;
                                DELETE FROM message WHERE message_id = mid;
                                RETURN ret;
                        ELSE
                                SELECT chat INTO ret FROM message WHERE ( rec_id = rid OR rec_id IS NULL) ORDER BY t ASC LIMIT 1;
                                RETURN ret;
                        END IF;
                EXCEPTION
                        WHEN no_data_found THEN
                                RAISE EXCEPTION 'No message found';
                        WHEN too_many_rows THEN
                                RAISE EXCEPTION 'Two mnay rows';
                END;
        ELSE
                RAISE 'Couldnt find the Receiver';
        END IF;
END;
$$
LANGUAGE 'plpgsql';

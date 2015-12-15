DROP TABLE IF EXISTS client CASCADE;
CREATE TABLE IF NOT EXISTS  client(
client_id SERIAL UNIQUE NOT NULL,
ip_address TEXT,
id INTEGER,
sr INTEGER,
PRIMARY KEY(ip_address,id)
);

DROP TABLE IF EXISTS queue CASCADE;
CREATE TABLE IF NOT EXISTS queue(
queue_id SERIAL PRIMARY KEY,
queue_name text);

DROP TABLE IF EXISTS message;
CREATE TABLE IF NOT EXISTS message(
message_id SERIAL PRIMARY KEY,
queue_id INT REFERENCES queue ,
rec_id INT  REFERENCES client(client_id) ON UPDATE CASCADE ON DELETE CASCADE,
sen_id INT  REFERENCES client(client_id) ON UPDATE CASCADE ON DELETE CASCADE,
chat TEXT,
t TIMESTAMP WITH TIME ZONE
);


CREATE INDEX queue_ind ON message(queue_id);
CREATE INDEX rec_ind ON message(rec_id);
CREATE INDEX sen_ind ON message(sen_id);




#!/bin/bash
sudo -u postgres PGPASSWORD=sa1ramara0 psql -d postgres < /home/ec2-user/StoredProcedures/setupdb.sql
sudo -u postgres PGPASSWORD=sa1ramara0 psql -d postgres < /home/ec2-user/StoredProcedures/registerClient.sql
sudo -u postgres PGPASSWORD=sa1ramara0 psql -d postgres < /home/ec2-user/StoredProcedures/createQueue.sql
sudo -u postgres PGPASSWORD=sa1ramara0 psql -d postgres < /home/ec2-user/StoredProcedures/sendMessage.sql
sudo -u postgres PGPASSWORD=sa1ramara0 psql -d postgres < /home/ec2-user/StoredProcedures/receiveMessage2.sql
sudo -u postgres PGPASSWORD=sa1ramara0 psql -d postgres < /home/ec2-user/StoredProcedures/sendMessageR.sql
sudo -u postgres PGPASSWORD=sa1ramara0 psql -d postgres < /home/ec2-user/StoredProcedures/getReceiver.sql
sudo -u postgres PGPASSWORD=sa1ramara0 psql -d postgres < /home/ec2-user/StoredProcedures/deregisterClient.sql
sudo -u postgres PGPASSWORD=sa1ramara0 psql -d postgres < /home/ec2-user/StoredProcedures/getQueues.sql


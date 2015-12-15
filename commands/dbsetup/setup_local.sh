#!/bin/bash
sudo -u postgres PGPASSWORD=sa1ramara0 psql -d postgres < setupdb.sql
sudo -u postgres PGPASSWORD=sa1ramara0 psql -d postgres < registerClient.sql
sudo -u postgres PGPASSWORD=sa1ramara0 psql -d postgres < createQueue.sql
sudo -u postgres PGPASSWORD=sa1ramara0 psql -d postgres < sendMessage.sql
sudo -u postgres PGPASSWORD=sa1ramara0 psql -d postgres < receiveMessage2.sql
sudo -u postgres PGPASSWORD=sa1ramara0 psql -d postgres < sendMessageR.sql
sudo -u postgres PGPASSWORD=sa1ramara0 psql -d postgres < getReceiver.sql
sudo -u postgres PGPASSWORD=sa1ramara0 psql -d postgres < deregisterClient.sql
sudo -u postgres PGPASSWORD=sa1ramara0 psql -d postgres < getQueues.sql
#sudo -u postgres PGPASSWORD=sa1ramara0 psql -d postgres < filldb.sql


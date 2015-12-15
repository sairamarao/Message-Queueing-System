import subprocess
import paraminput
import time

MW = paraminput.MW
MW2 = paraminput.MW2
MW3 = paraminput.MW3
db_conn = ['21','43','85']#3*21=63,3*43=129;3*85=255
for dbconn in db_conn:
	dbp = open('dbPool.properties','w')
	dbp.write('postgres.driver=org.postgresql.Driver\n');
	dbp.write('postgres.url=jdbc:postgresql://ec2-52-32-241-72.us-west-2.compute.amazonaws.com:5432/postgres\n')
	dbp.write('postgres.user=postgres\n')
	dbp.write('postgres.password=sa1ramara0\n')
	dbp.write('postgres.maximum='+dbconn+'\n')
	dbp.close()
	mwp = open('middlewareS.properties','w')
	mwp.write('port=5434\n')
	mwp.write('backlog=50\n')
	mwp.write('RequestHandlerClass=ClientReceiver.QueryHandler\n')
	mwp.write('maxQueueLength=20\n')
	mwp.write('minThreads='+dbconn+'\n')
	mwp.write('maxThreads='+dbconn+'\n')
	mwp.close()
	db_cmd1 = 'scp -i ../AWS/aslab.pem dbPool.properties ec2-user@'+MW+':~/Middleware'
	db_cmd2 = 'scp -i ../AWS/aslab.pem middlewareS.properties ec2-user@'+MW+':~/Middleware'
	db_cmd3 = 'scp -i ../AWS/aslab.pem dbPool.properties ec2-user@'+MW2+':~/Middleware'
	db_cmd4 = 'scp -i ../AWS/aslab.pem middlewareS.properties ec2-user@'+MW2+':~/Middleware'
	db_cmd5 = 'scp -i ../AWS/aslab.pem dbPool.properties ec2-user@'+MW3+':~/Middleware'
	db_cmd6 = 'scp -i ../AWS/aslab.pem middlewareS.properties ec2-user@'+MW3+':~/Middleware'
	
	result = subprocess.Popen(db_cmd1,shell=True,stdout=subprocess.PIPE,stdin=subprocess.PIPE)
	print 'Sending dbPool.properties -> MW1',result.communicate()
	time.sleep(1)
	result = subprocess.Popen(db_cmd2,shell=True,stdout=subprocess.PIPE,stdin=subprocess.PIPE)
	print 'Sending middleware.properties -> MW1',result.communicate()
	time.sleep(1)
	result = subprocess.Popen(db_cmd3,shell=True,stdout=subprocess.PIPE,stdin=subprocess.PIPE)
	print 'Sending dbPool.properties -> MW2',result.communicate()
	time.sleep(1)
	result = subprocess.Popen(db_cmd4,shell=True,stdout=subprocess.PIPE,stdin=subprocess.PIPE)
	print 'Sending middleware.properties -> MW2',result.communicate()
	time.sleep(1)
	result = subprocess.Popen(db_cmd5,shell=True,stdout=subprocess.PIPE,stdin=subprocess.PIPE)
	print 'Sending dbPool.properties -> MW3',result.communicate()
	time.sleep(1)
	result = subprocess.Popen(db_cmd6,shell=True,stdout=subprocess.PIPE,stdin=subprocess.PIPE)
	print 'Sending middleware.properties -> MW3',result.communicate()
	time.sleep(1)
	no_clients = ['20','26']#3*20=60;3*26=78
	tot_dbconn = str(3*int(dbconn))
	for client in no_clients:
		tot_clients = str(3*int(client))
		filep = open('Client.properties','w')
		filep.write('MW='+MW+'\n')
		filep.write('MW2='+MW2+'\n')#Configure Client2 to take MW2
		filep.write('MW3='+MW3+'\n')#Configure Client3 to take MW3
		filep.write('long='+paraminput.long+'\n')
		filep.write('short='+paraminput.short+'\n')
		filep.write('Clients='+client+'\n')
		loc=tot_dbconn+'DB'+tot_clients+'C3MW'
		filep.close()
		configp = open('config.py','w')
		configp.write('dbconn=\"'+tot_dbconn+'\"\n')#For GNU Plot
		configp.write('numclients=\"'+client+'\"\n')#For calculate2.py
		configp.write('experiment=\"'+loc+'\"\n')#For calculate2.py
		configp.close()
		print 'Running setup '+loc
		result = subprocess.Popen('python run2.py 0',shell=True,stdout=subprocess.PIPE,stdin=subprocess.PIPE,stderr=subprocess.PIPE)
		print 'Ran init commands..',result.communicate() 
		time.sleep(2)
		p = subprocess.Popen('python run2.py 1',shell=True,stdin=subprocess.PIPE,stdout=subprocess.PIPE,stderr=subprocess.PIPE)
		print 'Ran 1st iteration..',p.communicate()
		time.sleep(2)
		result2 = subprocess.Popen('python run2.py 2',shell=True,stdout=subprocess.PIPE,stdin=subprocess.PIPE,stderr=subprocess.PIPE)
		print 'Ran 2nd iteration..',result2.communicate()
		time.sleep(2)
		result3 = subprocess.Popen('python run2.py 3',shell=True,stdout=subprocess.PIPE,stdin=subprocess.PIPE,stderr=subprocess.PIPE)
		print 'Getting results successful..',result3.communicate()
		time.sleep(2)
		result4 = subprocess.Popen('python run2.py 4',shell=True,stdout=subprocess.PIPE,stdin=subprocess.PIPE,stderr=subprocess.PIPE)
		print 'Plotting graph successful..',result4.communicate()

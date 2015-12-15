import subprocess
import paraminput
import time

c_threads = ['1']
db_threads = ['1']
MW1 = paraminput.MW1
MW2 = paraminput.MW2
for c_work in c_threads:
	for db_work in db_threads:
		dbp = open('dbPool.properties','w')
		dbp.write('postgres.driver=org.postgresql.Driver\n');
		dbp.write('postgres.url=jdbc:postgresql://'+paraminput.Server+':5432/postgres\n')
		dbp.write('postgres.user=postgres\n')
		dbp.write('postgres.password=sa1ramara0\n')
		dbp.write('postgres.maximum='+db_work+'\n')
		dbp.close()
		mwp = open('middlewareS.properties','w')
		mwp.write('port=5434\n')
		mwp.write('backlog=50\n')
		mwp.write('RequestHandlerClass=ClientReceiver.QueryHandler\n')
		mwp.write('ClientWorkers='+c_work+'\n')
		mwp.close()
		db_cmd1 = 'scp -i ../../../AWS/aslab.pem dbPool.properties ec2-user@'+MW1+':~/Middleware'
		db_cmd2 = 'scp -i ../../../AWS/aslab.pem middlewareS.properties ec2-user@'+MW1+':~/Middleware'
		db_cmd3 = 'scp -i ../../../AWS/aslab.pem dbPool.properties ec2-user@'+MW2+':~/Middleware'
		db_cmd4 = 'scp -i ../../../AWS/aslab.pem middlewareS.properties ec2-user@'+MW2+':~/Middleware'
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
		no_clients = ['1']
		tot_dbconn = str(2*int(db_work))
		for client in no_clients:
			tot_clients = str(2*int(client))
			filep = open('Client.properties','w')
			filep.write('MW1='+MW1+'\n')
			filep.write('MW2='+MW2+'\n')#Configure Client2 to take MW2
			filep.write('long='+paraminput.long+'\n')
			filep.write('short='+paraminput.short+'\n')
			filep.write('Clients='+client+'\n')
			loc=c_work+'c'+db_work+'db'+client+'C2MW_2min_delay_0ms'
			filep.close()
			configp = open('config.py','w')
			configp.write('db_work=\"'+db_work+'\"\n')
			configp.write('c_work=\"'+c_work+'\"\n')
			configp.write('numclients=\"'+client+'\"\n')#For calculate2.py
			configp.write('experiment=\"'+loc+'\"\n')#For calculate2.py
			configp.write('MW1=\"'+paraminput.MW1+'\"\n')
			configp.write('MW2=\"'+paraminput.MW2+'\"\n')
			configp.write('Client1=\"'+paraminput.Client1+'\"\n')
			configp.write('Client2=\"'+paraminput.Client2+'\"\n')
			configp.write('Server=\"'+paraminput.Server+'\"\n')
			configp.close()
			print 'Starting Experiment '+loc
			print 'Starting Init commands'
			result = subprocess.Popen('python run2.py 0',shell=True,stdout=subprocess.PIPE,stdin=subprocess.PIPE,stderr=subprocess.PIPE)
			print 'Init result: ',result.communicate() 
			time.sleep(2)
			print 'Starting First Run'
			p = subprocess.Popen('python run2.py 1',shell=True,stdin=subprocess.PIPE,stdout=subprocess.PIPE,stderr=subprocess.PIPE)
			print '1st iteration result: ',p.communicate()
			time.sleep(2)
			print 'Starting Second Run'
			result2 = subprocess.Popen('python run2.py 2',shell=True,stdout=subprocess.PIPE,stdin=subprocess.PIPE,stderr=subprocess.PIPE)
			print '2nd iteration result: ',result2.communicate()
			time.sleep(2)
			print 'Sorting Experimental Data'
			result3 = subprocess.Popen('python run2.py 3',shell=True,stdout=subprocess.PIPE,stdin=subprocess.PIPE,stderr=subprocess.PIPE)
			print 'Getting results result: ',result3.communicate()
			time.sleep(2)
			print 'Plotting Graph'
			result4 = subprocess.Popen('python run2.py 4',shell=True,stdout=subprocess.PIPE,stdin=subprocess.PIPE,stderr=subprocess.PIPE)
			print 'Plotting graph result: ',result4.communicate()
			print 'Computing results'
			result5 = subprocess.Popen('python compute2.py',shell=True,stdout=subprocess.PIPE,stdin=subprocess.PIPE)
			print result5.communicate()

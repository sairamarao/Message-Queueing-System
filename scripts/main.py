import subprocess
import paraminput
import time


c_threads = ['32']
db_threads = ['16']
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
		mwp.write('backlog=20\n')
		mwp.write('RequestHandlerClass=ClientReceiver.QueryHandler\n')
		mwp.write('ClientWorkers='+c_work+'\n')
		mwp.close()
		db_cmd1 = 'scp -i ../../../AWS/aslab.pem dbPool.properties ec2-user@'+paraminput.MW+':~/Middleware;'
		db_cmd2 = 'scp -i ../../../AWS/aslab.pem middlewareS.properties ec2-user@'+paraminput.MW+':~/Middleware;'
		result = subprocess.Popen(db_cmd1,shell=True,stdout=subprocess.PIPE,stdin=subprocess.PIPE)
		print result.communicate()
		time.sleep(1)
		result = subprocess.Popen(db_cmd2,shell=True,stdout=subprocess.PIPE,stdin=subprocess.PIPE)
		print result.communicate()
		time.sleep(1)
		no_clients = ['100']
		for client in no_clients:
			filep = open('Client.properties','w')
			filep.write('MW1='+paraminput.MW1+'\n')
			filep.write('long='+paraminput.long+'\n')
			filep.write('short='+paraminput.short+'\n')
			filep.write('Clients='+client+'\n')
			loc=c_work+'c'+db_work+'db'+client+'C1MW'
			filep.close()
			configp = open('config.py','w')
			configp.write('db_work=\"'+db_work+'\"\n')
			configp.write('c_work=\"'+c_work+'\"\n')
			configp.write('numclients=\"'+client+'\"\n')
			configp.write('experiment=\"'+loc+'\"\n')
			configp.write('mw=\"'+paraminput.MW+'\"\n')
			configp.write('client=\"'+paraminput.Client1+'\"\n')
			configp.write('db=\"'+paraminput.Server+'\"\n')
			configp.close()
			print 'Starting Experiment '+loc
			print 'Starting First Run'
			result = subprocess.Popen('python run.py 1',shell=True,stdout=subprocess.PIPE,stdin=subprocess.PIPE)
			print result.communicate() 
			time.sleep(2)
			print 'Starting Second Run'
			result2 = subprocess.Popen('python run.py 2',shell=True,stdout=subprocess.PIPE,stdin=subprocess.PIPE)
			print result2.communicate()
			time.sleep(2)
			print 'Sorting Experimental Data'
			result3 = subprocess.Popen('python run.py 3',shell=True,stdout=subprocess.PIPE,stdin=subprocess.PIPE)
			print result3.communicate()
			time.sleep(2)
			print 'Plotting Graph'
			result4 = subprocess.Popen('python run.py 4',shell=True,stdout=subprocess.PIPE,stdin=subprocess.PIPE)
			print result4.communicate()
			print 'Computing results'
			result5 = subprocess.Popen('python compute.py',shell=True,stdout=subprocess.PIPE,stdin=subprocess.PIPE)
			print result5.communicate()

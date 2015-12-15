import os
import sys
import config
import subprocess
import signal
import time
import paraminput

clients = config.numclients
loc = config.experiment
db_conn = config.dbconn
MW = paraminput.MW
MW2 = paraminput.MW2
MW3 = paraminput.MW3
Server = paraminput.Server
Client1 = paraminput.Client1
Client2 = paraminput.Client2
Client3 = paraminput.Client3

server_cmd = 'ssh -t -t -i ../AWS/aslab.pem ec2-user@'+Server+' \'sh ~/StoredProcedures/setup.sh\''
mw_first_run = 'ssh -t -t -i ../AWS/aslab.pem ec2-user@'+MW+' \'cd ~/Middleware\' \'bash && ant main > '+loc+'_MW1.txt\''

mw_second_run ='ssh -t -t -i ../AWS/aslab.pem ec2-user@'+MW+' \'cd ~/Middleware\' \'bash && ant main > '+loc+'_2_MW1.txt\''

mw2_first_run = 'ssh -t -t -i ../AWS/aslab.pem ec2-user@'+MW2+' \'cd ~/Middleware\' \'bash && ant main > '+loc+'_MW2.txt\''
mw2_second_run ='ssh -t -t -i ../AWS/aslab.pem ec2-user@'+MW2+' \'cd ~/Middleware\' \'bash && ant main > '+loc+'_2_MW2.txt\''

mw3_first_run = 'ssh -t -t -i ../AWS/aslab.pem ec2-user@'+MW3+' \'cd ~/Middleware\' \'bash && ant main > '+loc+'_MW3.txt\''
mw3_second_run ='ssh -t -t -i ../AWS/aslab.pem ec2-user@'+MW3+' \'cd ~/Middleware\' \'bash && ant main > '+loc+'_2_MW3.txt\''

#mkdir ResponseTime and Throughput
#Send to second client also
#get throughput and response time folders from both the clients and calculate t,r
init_cmd = 'mkdir Experiments/'+loc+'; mkdir Experiments/'+loc+'/Client1; mkdir Experiments/'+loc+'/Client2; mkdir Experiments/'+loc+'/Client3;'\
+'mkdir Experiments/'+loc+'/Client1/first; mkdir Experiments/'+loc+'/Client2/first; mkdir Experiments/'+loc+'/Client3/first;'\
+'mkdir Experiments/'+loc+'/Client1/second; mkdir Experiments/'+loc+'/Client2/second; mkdir Experiments/'+loc+'/Client3/second;'\
+'scp -i ../AWS/aslab.pem Client.properties ec2-user@'+Client1+':~/Client;'\
+'scp -i ../AWS/aslab.pem Client.properties ec2-user@'+Client2+':~/Client;'\
+'scp -i ../AWS/aslab.pem Client.properties ec2-user@'+Client3+':~/Client;'

client1_run='ssh -i ../AWS/aslab.pem ec2-user@'+Client1+' \'cd ~/Client\' \'bash && ant main > ~/Client/'+loc+'.txt\''

client2_run='ssh -i ../AWS/aslab.pem ec2-user@'+Client2+' \'cd ~/Client\' \'bash && ant main > ~/Client/'+loc+'.txt\''

client3_run='ssh -i ../AWS/aslab.pem ec2-user@'+Client3+' \'cd ~/Client\' \'bash && ant main > ~/Client/'+loc+'.txt\''

iter = ['first','second']
c = 0
if int(sys.argv[1]) == 2:
	c = 1
client1_get_res = 'scp -i ../AWS/aslab.pem -r ec2-user@'+Client1+':~/Client/ResponseTime Experiments/'+loc+'/Client1/'+iter[c]+'/;'\
+'scp -i ../AWS/aslab.pem -r ec2-user@'+Client1+':~/Client/Throughput Experiments/'+loc+'/Client1/'+iter[c]+'/;'\
+'scp -i ../AWS/aslab.pem -r ec2-user@'+Client1+':~/Client/'+loc+'.txt Experiments/'+loc+'/Client1/'+iter[c]+'/;'\
+'ssh -i ../AWS/aslab.pem ec2-user@'+Client1+' \'rm -r ~/Client/Throughput/*;\' \'rm -r ~/Client/ResponseTime/*;\' '\
+'\'rm ~/Client/'+loc+'.txt\''

client2_get_res = 'scp -i ../AWS/aslab.pem -r ec2-user@'+Client2+':~/Client/ResponseTime Experiments/'+loc+'/Client2/'+iter[c]+'/;'\
+'scp -i ../AWS/aslab.pem -r ec2-user@'+Client2+':~/Client/Throughput Experiments/'+loc+'/Client2/'+iter[c]+'/;'\
+'scp -i ../AWS/aslab.pem -r ec2-user@'+Client2+':~/Client/'+loc+'.txt Experiments/'+loc+'/Client2/'+iter[c]+'/;'\
+'ssh -i ../AWS/aslab.pem ec2-user@'+Client2+' \'rm -r ~/Client/Throughput/*;\' \'rm -r ~/Client/ResponseTime/*;\' '\
+'\'rm ~/Client/'+loc+'.txt\''


client3_get_res = 'scp -i ../AWS/aslab.pem -r ec2-user@'+Client3+':~/Client/ResponseTime Experiments/'+loc+'/Client3/'+iter[c]+'/;'\
+'scp -i ../AWS/aslab.pem -r ec2-user@'+Client3+':~/Client/Throughput Experiments/'+loc+'/Client3/'+iter[c]+'/;'\
+'scp -i ../AWS/aslab.pem -r ec2-user@'+Client3+':~/Client/'+loc+'.txt Experiments/'+loc+'/Client3/'+iter[c]+'/;'\
+'ssh -i ../AWS/aslab.pem ec2-user@'+Client3+' \'rm -r ~/Client/Throughput/*;\' \'rm -r ~/Client/ResponseTime/*;\' '\
+'\'rm ~/Client/'+loc+'.txt\''

mw_get_res = 'scp -i ../AWS/aslab.pem ec2-user@'+MW+':~/Middleware/'+loc+'_MW1.txt Experiments/'+loc+'/;'\
+'scp -i ../AWS/aslab.pem ec2-user@'+MW+':~/Middleware/'+loc+'_2_MW1.txt Experiments/'+loc+'/;'\
+'scp -i ../AWS/aslab.pem ec2-user@'+MW2+':~/Middleware/'+loc+'_MW2.txt Experiments/'+loc+'/;'\
+'scp -i ../AWS/aslab.pem ec2-user@'+MW2+':~/Middleware/'+loc+'_2_MW2.txt Experiments/'+loc+'/;'\
+'scp -i ../AWS/aslab.pem ec2-user@'+MW3+':~/Middleware/'+loc+'_MW3.txt Experiments/'+loc+'/;'\
+'scp -i ../AWS/aslab.pem ec2-user@'+MW3+':~/Middleware/'+loc+'_2_MW3.txt Experiments/'+loc+'/;'\
+'ssh -i ../AWS/aslab.pem ec2-user@'+MW+' \'rm ~/Middleware/'+loc+'_MW1.txt;\' \'rm ~/Middleware/'+loc+'_2_MW1.txt;\';'\
+'ssh -i ../AWS/aslab.pem ec2-user@'+MW2+' \'rm ~/Middleware/'+loc+'_MW2.txt;\' \'rm ~/Middleware/'+loc+'_2_MW2.txt;\';'\
+'ssh -i ../AWS/aslab.pem ec2-user@'+MW2+' \'rm ~/Middleware/'+loc+'_MW3.txt;\' \'rm ~/Middleware/'+loc+'_2_MW3.txt;\';'

value = int(sys.argv[1])

if value == 0:
	p = subprocess.Popen(init_cmd,shell=True,stdout=subprocess.PIPE,stdin=subprocess.PIPE)
	print p.communicate()
if value == 1:
	mw1_cmd = mw_first_run
	mw2_cmd = mw2_first_run
	mw3_cmd = mw3_first_run
if value == 2:
	mw1_cmd = mw_second_run
	mw2_cmd = mw2_second_run
	mw3_cmd = mw3_second_run
if value == 3:#Get MW logs and also Compute Throughput and ResponseTime
	p = subprocess.Popen(mw_get_res,shell=True,stdout=subprocess.PIPE,stdin=subprocess.PIPE)
	print p.communicate()
	time.sleep(1)
	p = subprocess.Popen('python calculate2.py '+clients+' '+loc,shell=True,stdout=subprocess.PIPE,stdin=subprocess.PIPE)
	print p.communicate()
	time.sleep(1)
if value == 4:
	tot_clients = str(3*int(clients))
	gnu = subprocess.Popen(['gnuplot','-p'],shell=True,stdin=subprocess.PIPE)
	gnu.stdin.write('set xlabel \'Time\'\n')
	gnu.stdin.write('set ylabel \'Throughput\'\n')
	gnu.stdin.write('set title \'DBCon: '+db_conn+' Clients: '+tot_clients+', Message: 2000\'\n')
	gnu.stdin.write('set term png\n')
	gnu.stdin.write('set output \'Experiments/'+loc+'/secondplot.png\'\n')
	gnu.stdin.write('plot \'Experiments/'+loc+'/second_plot_'+loc+'.data\'\n')
	gnu.stdin.write('set output \'Experiments/'+loc+'/firstplot.png\'\n')
	gnu.stdin.write('plot \'Experiments/'+loc+'/first_plot_'+loc+'.data\' notitle\n')
	gnu.stdin.write('quit\n')
if value == 1 or value == 2:
	server_out = subprocess.Popen(server_cmd,shell=True,stdout=subprocess.PIPE,stdin=subprocess.PIPE)
	server_out.communicate()
	mw1out = subprocess.Popen("exec "+mw1_cmd, shell=True, stdout=subprocess.PIPE, stdin=subprocess.PIPE,stderr=subprocess.PIPE)
	mw2out = subprocess.Popen("exec "+mw2_cmd, shell=True, stdout=subprocess.PIPE, stdin=subprocess.PIPE,stderr=subprocess.PIPE)
	mw3out = subprocess.Popen("exec "+mw3_cmd, shell=True, stdout=subprocess.PIPE, stdin=subprocess.PIPE,stderr=subprocess.PIPE)
	#time.sleep(4)
	print 'Started MW1,MW2 and MW3..'
	time.sleep(5)
	client1_out = subprocess.Popen(client1_run, shell=True, stdout=subprocess.PIPE,stdin=subprocess.PIPE)
	client2_out = subprocess.Popen(client2_run, shell=True, stdout=subprocess.PIPE,stdin=subprocess.PIPE)
	client3_out = subprocess.Popen(client3_run, shell=True, stdout=subprocess.PIPE,stdin=subprocess.PIPE)
	print client1_out.communicate()
	print client2_out.communicate()
	print client3_out.communicate()
	res_out = subprocess.Popen(client1_get_res, shell=True, stdout=subprocess.PIPE,stdin=subprocess.PIPE)
	print res_out.communicate()
	res_out = subprocess.Popen(client2_get_res, shell=True, stdout=subprocess.PIPE,stdin=subprocess.PIPE)
	print res_out.communicate()
	res_out = subprocess.Popen(client3_get_res, shell=True, stdout=subprocess.PIPE,stdin=subprocess.PIPE)
	print res_out.communicate()
	print 'Client 1,2 and 3 complete..'
	mw1out.kill()
	mw2out.kill()
	mw3out.kill()



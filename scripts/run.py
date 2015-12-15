import os
import sys
import config
import subprocess
import signal
import time

client = config.numclients
loc = config.experiment
c_work = config.c_work
db_work = config.db_work
client_add = config.client
mw_add = config.mw
db_add = config.db

#Change ip value for database
server_cmd = 'ssh -t -t -i ../../../AWS/aslab.pem ec2-user@'+db_add+' '\
+'\'sh ~/StoredProcedures/setup.sh\''
#Change ip value for middleware
mw_first_cmd = 'ssh -t -t -i ../../../AWS/aslab.pem ec2-user@'+mw_add+' \'cd ~/Middleware\' \'bash && ant main > '+loc+'_MW.txt\''
#Change ip value for middleware
mw_second_cmd ='ssh -t -t -i ../../../AWS/aslab.pem ec2-user@'+mw_add+' \'cd ~/Middleware\' \'bash && ant main > '+loc+'_2_MW.txt\''


#mkdir ResponseTime and Throughput
#change ip value for Client in all the below code
first_cmd = 'scp -i ../../../AWS/aslab.pem calculate.py ec2-user@'+client_add+':~/Client;'\
+'scp -i ../../../AWS/aslab.pem config.py ec2-user@'+client_add+':~/Client;'\
+'scp -i ../../../AWS/aslab.pem Client.properties ec2-user@'+client_add+':~/Client;'\
+'ssh -i ../../../AWS/aslab.pem ec2-user@'+client_add+' '\
+'\'cd ~/Client;\' \'bash && ant main > ~/Client/'+loc+'.txt;\''
compute_cmd_first = 'ssh -i ../../../AWS/aslab.pem ec2-user@'+client_add+' \'python ~/Client/calculate.py;\''
sorting_cmd = 'ssh -i ../../../AWS/aslab.pem ec2-user@'+client_add+' \'mv ~/Client/ResponseTime ~/Client/first;\' \'mv ~/Client/Throughput ~/Client/first;\''\
+'\'mv ~/Client/plot_'+loc+'.data ~/Client/first;\' \'mv ~/Client/Result_'+loc+'.py ~/Client/first;\''\
+'\'mv ~/Client/'+loc+'.txt ~/Client/first;\''\
+'\'mkdir ~/Client/ResponseTime;\' \'mkdir ~/Client/Throughput;\''

second_cmd = 'ssh -i ../../../AWS/aslab.pem ec2-user@'+client_add+' '\
+'\'cd ~/Client;\' \'bash && ant main > ~/Client/'+loc+'.txt;\''
compute_cmd_sec = 'ssh -i ../../../AWS/aslab.pem ec2-user@'+client_add+' \'python ~/Client/calculate.py;\''
sorting_cmd_2 = 'ssh -i ../../../AWS/aslab.pem ec2-user@'+client_add+'  \'mv ~/Client/ResponseTime ~/Client/second;\' \'mv ~/Client/Throughput ~/Client/second;\''\
+'\'mv ~/Client/plot_'+loc+'.data ~/Client/second;\' \'mv ~/Client/Result_'+loc+'.py ~/Client/second;\''\
+'\'mv ~/Client/'+loc+'.txt ~/Client/second;\''\
+'\'mkdir ~/Client/ResponseTime;\' \'mkdir ~/Client/Throughput;\''

#Copy results from first run and second run into Local Storage
third_cmd = 'mkdir Experiments/'+loc+'; scp -i ../../../AWS/aslab.pem -r ec2-user@'+client_add+':~/Client/first Experiments/'+loc+'/;'\
+'scp -i ../../../AWS/aslab.pem -r ec2-user@'+client_add+':~/Client/second Experiments/'+loc+'/;'\
+'ssh -i ../../../AWS/aslab.pem ec2-user@'+client_add+' '\
+'\'rm -r ~/Client/first/*;\' \'rm -r ~/Client/second/*;\';'\
+'scp -i ../../../AWS/aslab.pem ec2-user@'+mw_add+':~/Middleware/'+loc+'_MW.txt Experiments/'+loc+'/;'\
+'scp -i ../../../AWS/aslab.pem ec2-user@'+mw_add+':~/Middleware/'+loc+'_2_MW.txt Experiments/'+loc+'/;'\
+'scp -i ../../../AWS/aslab.pem ec2-user@'+mw_add+':~/Middleware/Stats.txt Experiments/'+loc+'/;'\
+'ssh -i ../../../AWS/aslab.pem ec2-user@'+mw_add+' \'rm ~/Middleware/'+loc+'_MW.txt;\' \'rm ~/Middleware/Stats.txt;\''\
+'\'rm ~/Middleware/'+loc+'_2_MW.txt;\''

value = int(sys.argv[1])

if value == 1:
	mw_cmd = mw_first_cmd
	client_cmd = first_cmd
	compute = compute_cmd_first
	sort_cmd = sorting_cmd
if value == 2:
	mw_cmd = mw_second_cmd
	client_cmd = second_cmd
	compute = compute_cmd_sec
	sort_cmd = sorting_cmd_2
if value == 3:
	result = subprocess.Popen(third_cmd, shell=True, stdout=subprocess.PIPE,stdin=subprocess.PIPE)
	response,err = result.communicate()
if value == 4:	
	gnu = subprocess.Popen(['gnuplot','-p'],shell=True,stdin=subprocess.PIPE)
	gnu.stdin.write('set xlabel \'Time\'\n')
	gnu.stdin.write('set ylabel \'Throughput\'\n')
	gnu.stdin.write('set title \'Client_worker: '+c_work+' DBWorker: '+db_work+' Clients: '+client+', Message: 2000\'\n')#MWTest
	gnu.stdin.write('set term png\n')
	gnu.stdin.write('set output \'Experiments/'+loc+'/secondplot.png\'\n')
	gnu.stdin.write('plot \'Experiments/'+loc+'/second/plot_'+loc+'.data\'\n')
	gnu.stdin.write('set output \'Experiments/'+loc+'/firstplot.png\'\n')
	gnu.stdin.write('plot \'Experiments/'+loc+'/first/plot_'+loc+'.data\'\n')
	gnu.stdin.write('quit\n')
if value == 1 or value == 2:
	#mw_cmd = 'ssh -t -t -i ../AWS/aslab.pem ec2-user@ec2-52-32-225-218.us-west-2.compute.amazonaws.com \'cd ~/Middleware\' \'bash && ant main > '+loc+'.txt\''

	server_out = subprocess.Popen(server_cmd,shell=True,stdout=subprocess.PIPE,stdin=subprocess.PIPE)
	#response,err = server_out.communicate()
	print server_out.communicate()
	mwout = subprocess.Popen("exec "+mw_cmd, shell=True, stdout=subprocess.PIPE, stdin=subprocess.PIPE)
	#time.sleep(4)
	print 'Started MW..'
	time.sleep(2)
	client_out = subprocess.Popen(client_cmd, shell=True, stdout=subprocess.PIPE,stdin=subprocess.PIPE)
	print client_out.communicate()
	print 'Client complete..'
	client_out = subprocess.Popen(compute, shell=True, stdout=subprocess.PIPE,stdin=subprocess.PIPE)
	print client_out.communicate()

	client_out = subprocess.Popen(sort_cmd, shell=True, stdout=subprocess.PIPE,stdin=subprocess.PIPE)
	print client_out.communicate()
	mwout.kill()



import math
import fileinput


filep = [open('Throughput/Throughput%d.txt'%index,'r') for index in range(1,20)]
fileout = open('original.data','w')
#fileout2 = open('forplot.data','w')
notep = open('notes.txt','w')

resRec = 0

files = ['Throughput/Throughput%d.txt'%index for index in range(1,20)]
resfiles = ['ResponseTime/ResponseTime%d.txt'%index for index in range(1,20)]

min_lines = 200000
max_lines = 0

#Finding Average response time
responses = sorted(int(line) for line in fileinput.input(resfiles))
res_quartile_1 = responses[int(len(responses)*.25)]
res_median = responses[int(len(responses)*.5)]
res_quartile_3 = responses[int(len(responses)*.75)]
res = [x for x in responses if x >= res_quartile_1 and x <= res_quartile_3]
notep.write("Avg Response Time (without Outliers): "+str(sum(res)/len(res))+'\n')


#/sum(1 for line in fileinput.input(resfiles))
#notep.write("Average ResponseTime: "+str(avgres)+'\n')	
#Finding the min number of lines to chose the limit of time in X-axis
for i in files:
	num_lines = sum(1 for line in open(i))
	if num_lines < min_lines:
		min_lines = num_lines
	if num_lines > max_lines:
		max_lines = num_lines
notep.write("Min Lines: "+str(min_lines)+"Max Lines: "+str(max_lines)+'\n')
original=[]

#Calculating the difference of adjacent time values to get the number of successfull requests per second
for i in range(1,min_lines+1):
	value = 0
	for fp in filep:
		line = fp.readline()
		intarray = [int(x) for x in line.split()]
		value = value + intarray[1]
	temp = value
	value = value - resRec
	resRec = temp
	fileout.write(str(i)+' '+str(value)+'\n')
	original.append(value)
notep.write("Average Throughput: "+str(sum(original)/len(original))+'\n')


#Calculating Inter Quartile Range values only
sort_orig = sorted(original)
quartile_1 = sort_orig[int(len(sort_orig)*.25)]
median = sort_orig[int(len(sort_orig)*.5)]
quartile_3 = sort_orig[int(len(sort_orig)*.75)]
plot_value = [x for x in original if x >= quartile_1 and x <= quartile_3]
print "Q1",quartile_1
print "Median",median
print "Q3",quartile_3
notep.write("Avg Throughput(without Outliers): "+str(sum(plot_value)/len(plot_value))+'\n')
#print len(original)
#counter = 1
#for i in range(0,len(plot_value)):
#	fileout2.write(str(counter)+' '+str(plot_value[i])+'\n')
#	counter = counter + 1

#Calculating standard deviation and ignoring values > 2*st_dev
#stdev = []
#st_deviation = 0
#for j in original:
#    stdev.append(pow((j - (sum(original)/len(original))), 2))
#    st_deviation = math.sqrt(sum(stdev)/len(stdev))
#print st_deviation
#limit = 2*st_deviation
#print limit
#plot_value = [x for x in original if x > -1*limit or x < limit ]
#counter = 1
#for i in plot_value:
#	fileout2.write(str(counter)+' '+str(plot_value[i])+'\n')
#	counter = counter + 1


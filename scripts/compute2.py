import config
loc = config.experiment

notep = open('./Experiments/Analysis.txt','a')
firstp = dict(line.strip().split('=') for line in open('Experiments/'+loc+'/first_Result_'+loc+'.py'))
secondp = dict(line.strip().split('=') for line in open('Experiments/'+loc+'/second_Result_'+loc+'.py'))
notep.write('Experiment='+loc+'\n')
AvgThroughput = (int(firstp['AvgThroughput'])+int(secondp['AvgThroughput']))/2 
AvgRespTime = (int(firstp['AvgRespTime'])+int(secondp['AvgRespTime']))/2
AvgStdError = (float(firstp['stdError'])+float(secondp['stdError']))/2
notep.write('AvgThroughput='+str(AvgThroughput)+'\n'+'AvgResponseTime='+str(AvgRespTime)+'\n'+'AvgStdError='+str(AvgStdError)+'\n')
notep.close()
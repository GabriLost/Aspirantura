"""
Created on Wed Dec  7 14:05:04 2016
#ruston2013@mail.com
#534532a
@author: gabri
"""
# coding=utf8


from statsmodels.tsa.arima_model import ARIMA
import statsmodels.api as sm
import pandas as pd
import numpy as np
import datetime
import warnings 
import matplotlib.pyplot as plt
from sklearn.linear_model import LinearRegression
from sklearn.linear_model import LogisticRegression
from sklearn.linear_model import SGDClassifier
from sklearn.ensemble import RandomForestClassifier
from sklearn.grid_search import GridSearchCV
from sklearn.neighbors import KNeighborsClassifier
from sklearn.naive_bayes import GaussianNB
from sklearn.svm import SVC
from sklearn.feature_selection import SelectKBest, f_classif
from sklearn.cross_validation import StratifiedKFold
from sklearn.cross_validation import KFold
from sklearn.cross_validation import cross_val_score
from sklearn.preprocessing import StandardScaler
from sklearn import metrics

from sklearn.neighbors import KNeighborsRegressor
from sklearn.svm import SVR
from sklearn.ensemble import RandomForestRegressor
from sklearn.metrics import r2_score
from sklearn.cross_validation import train_test_split

from pylab import plot,show
from numpy import vstack,array
from numpy.random import rand
from scipy.cluster.vq import kmeans,vq

warnings.filterwarnings('ignore')
#read
try: 
    train_data
except NameError:
    print("reading primary data... procceed...")
    train_data = pd.read_csv("/home/gabri/ds/train_data.csv",sep=";", encoding="cp1251", engine = 'python')

try:
    train_economic
except NameError:    
    print("reading secondary data... procceed...")
    a_pointer = "setting economic train_economic"  ;
    train_economic = pd.read_csv("/home/gabri/ds/train_economic.csv",sep=";", encoding="cp1251", engine = 'python').convert_objects(convert_numeric = True)
#
    train_economic = train_economic.interpolate(method = 'cubic')
    train_economic = train_economic.set_index(pd.to_datetime(train_economic['date'], format  ='%d.%m.%Y')).drop(['date'],axis=1)
    train_economic.plot(figsize = (10,5)).get_figure().savefig('output/economic.png');

#constants 
types = {
#          'coal':'4402'
#          'cheese':'406',
          'wheat':'1001'
#          'all':''
         }
#         mass,factur stoimost,stat stoimost,gr47,tamozhennya stoimost, obschaya factur stoimost
predicates = ['G35','G42','G46','G474RUB','G12','G222']
#predicates = ['G35']#massa

types_list = []
for predicate in predicates:
    for type in types:
        types_list.append(predicate+type)
        
print('')
#preparing fulldata for all categories
a_pointer = 'preparing fulldata for all categories'
    
all_type_all_predicate_sum = pd.DataFrame()

for predicate in predicates:
#    print(predicate)
    all_type_one_predicate_sum = pd.DataFrame()
    for type in types:
        a_pointer = type+' '+predicate+' start'
        one_type_one_predicate_sum = pd.DataFrame()
#        one_column  = train_data[train_data['G33'] & (train_data['G15A'].apply(str).str.startswith('RU')==True)].convert_objects(convert_numeric = True).get(['G072']+[predicate]) 
        one_column  = train_data[train_data['G33'].apply(str).str.startswith(types[type]) & (train_data['G15A'].apply(str).str.startswith('RU')==True)].convert_objects(convert_numeric = True).get(['G072']+[predicate])     
        one_type_one_predicate_sum   = one_column.groupby(one_column["G072"].apply(str)).sum()
        one_type_one_predicate_sum.columns = [predicate+type]
        all_type_one_predicate_sum = pd.concat([all_type_one_predicate_sum, one_type_one_predicate_sum] ,axis=1)
    #merging data to one file    
    all_type_one_predicate_sum['DATE'] = pd.to_datetime(all_type_one_predicate_sum.index, format  ='%d.%m.%Y')
    try:
        all_type_one_predicate_sum = all_type_one_predicate_sum.reset_index().drop(['index'], axis =1)#**[index] ['G072']
    except: 
        all_type_one_predicate_sum = all_type_one_predicate_sum.reset_index().drop(['G072'], axis =1)#**[index] ['G072']
        a_pointer = '28b'
#    all_type_one_predicate_sum = all_type_one_predicate_sum.interpolate(method = 'cubic')
    all_type_one_predicate_sum = all_type_one_predicate_sum.set_index(['DATE'])    
    all_type_all_predicate_sum = pd.concat([all_type_all_predicate_sum, all_type_one_predicate_sum] ,axis=1)  
#    all_type_one_predicate_sum = pd.rolling_mean(all_type_one_predicate_sum,50)
#end of merging
    a_pointer = predicate+' end'

#    all_type_one_predicate_sum.to_csv("/home/gabri/ds/"+predicates[i]+".csv", sep=";", encoding="cp1251")


#smoothing
a_pointer = 'smoothing'
#!TO_DO kill eco columns 
fulldata_wna = pd.concat([train_economic, all_type_all_predicate_sum],axis = 1).fillna(0)
#fulldata_wna['G35all'].plot(figsize = (9,5), linewidth = 1).legend(bbox_to_anchor = (1.005,1), loc = 2, borderaxespad= 0.).get_figure().savefig('output/fulldata_wna_'+'all'+'_'+'types'+'.png')
for predicate in predicates:
    for type in types:
        fulldata_wna[predicate+type] = fulldata_wna[predicate+type].rolling(window=10, center = True).mean()
#        fulldata_wna[predicate+type] = fulldata_wna[predicate+type].rolling(window=10, center = True).mean()
##fulldata_wna['G35all'].plot(figsize = (9,5), linewidth = 4).legend(bbox_to_anchor = (1.005,1), loc = 2, borderaxespad= 0.).get_figure().savefig('output/fulldata_smoothed_'+'all'+'_'+'types'+'.png')
fulldata_eco = pd.concat([train_economic, fulldata_wna],axis = 1)



#correlation
#a_pointer = 'correlation'
#fulldata_corr = fulldata_eco.corr('spearman',min_periods = 120)
#fulldata_corr['reindex'] = fulldata_corr.index
#fulldata_corr['x'] = (0)
#fulldata_corr = fulldata_corr.drop(list(train_economic.columns.values),1) #del columns
#fulldata_corr = fulldata_corr.drop(types_list) #del rows
#fulldata_corr.plot(figsize = (12,5), linewidth = 3).legend(bbox_to_anchor = (1.005,1), loc = 2, borderaxespad= 0.).get_figure().savefig('output/corr_'+'all'+'_'+'types'+'.png')


  
a_pointer = 'forecasting'


plt.figure(figsize = (8,4))

plt.grid()

#MA
ts = fulldata_eco.get(['G35wheat']).dropna()

plt.plot(ts[300:],color = 'green', label = 'real')
plt.plot(ts[0:301],color = 'blue', label = 'test data')


ts_train = ts[0:300]
ts_test = ts[300:]
ts = ts_train

start_date = ts.index[-5]+ 1
end_date = ts.index[-5]+ 60



#ARMA
#in
#some_temp = sm.tsa.arma_order_select_ic(ts, max_ar = 20, max_ma = 0, ic = 'aic')
#print(some_temp) #select order18 0
#arma_model = sm.tsa.ARMA(ts, order=(18,0))
#arma_ar_res = arma_model.fit(disp=0)
#arma_test3 = arma_ar_res.predict(ts.index[5].isoformat(),ts.index[-2].isoformat())
#arma_test3.plot(style = 'g')
##out
#arma_test4 = arma_ar_res.predict(start_date.isoformat(),end_date.isoformat())
#arma_test4.plot(style = 'b')

#ARIMA  
print("doing arima model... procceed...")
arima_model = sm.tsa.ARIMA(ts, order=(18,0,2))#p-roots d q, 
arima_ar_res = arima_model.fit(disp=-1)
    
#print(arima_ar_res.summary())
#in
#arima_predict_in = arima_ar_res.predict(ts.index[1].isoformat(),ts.index[-2].isoformat())
#arima_predict_in.plot(style = 'b--')
#out
arima_predict_out = arima_ar_res.predict(start_date.isoformat(),end_date.isoformat())
plt.plot(arima_predict_out, color = 'red',linestyle = '--',label = 'forecast')
plt.legend(bbox_to_anchor=(1.00,1), loc = 1)
plt.title('wheat')
plt.gcf().savefig('output/arima_wheat.png')

#bad arima_r2_score = r2_score(ts_test[0:20], arima_predict_out[0:20])
q_test = sm.tsa.stattools.acf(arima_ar_res.resid,qstat=True)
#print(pd.DataFrame({'Q-stat':q_test[1],'p-value':q_test[2]}))

#sARiMAx    
#a_pointer = 'sARiMAx'
#
#arima_log = fulldata_eco.get(['G35all']).dropna()
#a_model = sm.tsa.statespace.SARIMAX(arima_log,trend='n', order=(2,1,2),seasonal_order=(1,1,1,12))
#sarimax_res = a_model.fit()
#print(sarimax_res.summary())














a_pointer = 'success'  

print('succeed')  



#ax = fulldata_wna.groupby((fulldata_wna['oil']>0))[predicate+type,'rolling2','rolling3'].sum().iloc[0,:].tolist()
#
#saves|write
#fulldata1.to_csv("/home/gabri/ds/output/fulldata1"+datetime.datetime.now().strftime("%Y-%m-%d_%H:%M:%S")+".csv", sep=";", encoding="cp1251")
#    
    



#fields = ["KOLVO","FCOST1","FCOST","TCOST1","TCOST","OCOST","SCOSTUSD","G4DOL","G4RUB","NKG",    "MKG","USDKG","G032","G19","G311","G312","G05"]

#data = train_data.get(["FCOST","TCOST","OCOST","SCOSTUSD","NKG","MKG","USDKG"]).as_matrix()

#data = data.astype("float64")




#centroids,_ = kmeans(data,10);

#idx,_ = vq(data,centroids)







#plot(data[idx==0,0],data[idx==0,1],'or',

#     data[idx==1,0],data[idx==1,1],'og',

#     data[idx==2,0],data[idx==2,1],'ob',

#     data[idx==3,0],data[idx==3,1],'^r')

#plot(centroids[:,0],centroids[:,1],'sg',markersize=5)

#show()




#train_data['TYPE'] = pd.Series(idx, index=train_data.index)




#train_data.to_csv("/home/gabri/Downloads/out_traffic3.csv", sep=";", encoding="cp1251")




#describe_fields = ["FCOST","TCOST","OCOST","SCOSTUSD","NKG","MKG","USDKG"]

#describe_fields = ["OCOST","MKG"]

#g2 = train_data.groupby(["TYPE"])[describe_fields].describe()

#g3 = train_data.groupby(["DATE"])["OCOST"].mean()

#g4 = g3.get(["ND"]).as_matrix().astype("float64")

#g5 = train_data.groupby(["DAY"]).count()




#dataset = train_data

#dateset_head = dataset.head()

#dateset_corr = dataset.corr()







#models = [LinearRegression(),RandomForestRegressor(n_estimators=100, max_features ='sqrt'), KNeighborsRegressor(n_neighbors=6),SVR(kernel='linear'), LogisticRegression()]







#trg = dataset[describe_fields]

#trn = dataset.drop((describe_fields+["ND","DATE","KOD","G011","FROM","TO","G19"]), axis=1)




#trg = trg.as_matrix()

#trn = trn.as_matrix()




#Xtrn, Xtest, Ytrn, Ytest = train_test_split(trn, trg, test_size=0.2)




#regr = LinearRegression()

#regr = RandomForestRegressor(n_estimators=100, max_features ='sqrt')

#regr = KNeighborsRegressor(n_neighbors=6)

#regr = SVR(kernel='linear')

#regr = LogisticRegression()
#
#regr.fit(Xtrn, Ytrn)
#
#tmp = r2_score(Ytest, regr.predict(Xtest))
#
#
#
#
#Ypredict = regr.predict(Xtest);
#



#TestModels = TestModels.append([tmp])

#TestModels.set_index('Model', inplace=True)

# The coefficients

#print('Coefficients: \n', regr.coef_)

# The mean squared error

#print("Mean squared error: %.2f"    % np.mean((regr.predict(Xtest) - Ytest) ** 2))

# Explained variance score: 1 is perfect prediction

#print('Variance score: %.2f' % regr.score(Xtest, Ytest))
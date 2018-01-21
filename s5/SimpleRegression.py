""""
# coding=utf8
"""
import pandas as pd
import matplotlib.pyplot as plt
from sklearn.ensemble import RandomForestRegressor
from sklearn.linear_model import LinearRegression, LogisticRegression
from sklearn.metrics import mean_squared_error, r2_score
from sklearn.model_selection import train_test_split
from sklearn.neighbors import KNeighborsRegressor
from sklearn.svm import SVR

main_data = pd.read_csv("files/main_data.csv", sep=";", encoding="utf-8", engine='python').get(['DATE', 'TYPE', 'VALUE', 'DIR'])
tariff = pd.read_csv("files/tariff.csv", sep=";", encoding="utf-8", engine='python').get(['DATE','RAIL'])#,'TUBE' , 'AUTO', 'SEA','AIR'
# economic = pd.read_csv("files/inflation.csv", sep=";", encoding="utf-8", engine='python').get(['DATE','INF'])
economic = pd.read_csv("files/inflation.csv", sep=";", encoding="utf-8", engine='python').get(['DATE','INF'])



main_data = main_data.set_index(pd.to_datetime(main_data['DATE'], format='%d.%m.%y')).drop(['DATE'], axis=1)
tariff = tariff.set_index(pd.to_datetime(tariff['DATE'], format='%Y.%m.%d')).drop(['DATE'], axis=1)
economic = economic.set_index(pd.to_datetime(economic['DATE'], format='%d.%m.%Y')).drop(['DATE'], axis=1)




data = main_data[
           (main_data['TYPE'] == 'BRD')
           & (main_data['DIR'] == 'LC')]['2014-01-01':'2017-07-01']

# economic.join(tariff['RAIL']-100).join(data['VALUE']/1000).plot()
# data.plot()
# plt.show()

# pd.to_numeric(main_data['VALUE'], downcast='float')
# pd.to_numeric(tariff['RAIL'], downcast='float')

data = data.join(tariff).join(economic)

print(data.corr())

Ydata = data[['VALUE']]
Xdata = data.drop(['VALUE','TYPE','DIR'], axis=1)


Xtrn  = Xdata[:'2016-06-01']
Xtest = Xdata['2016-06-01':]
Ytrn =  Ydata[:'2016-06-01']
Ytest = Ydata['2016-06-01':]

regr = LinearRegression()
regr.fit(Xtrn, Ytrn)
# The coefficients
print('intercept_: \n', regr.intercept_)
print('Coefficients: \n', regr.coef_)
#  The mean squared error
print("Mean squared error: %.2f" % mean_squared_error(Ytest, regr.predict(Xtest)))
# Explained variance score: 1 is perfect prediction
print('Variance score: %.2f' % r2_score(Ytest, regr.predict(Xtest)))

models = [LinearRegression()
        , RandomForestRegressor(n_estimators=100, max_features ='sqrt')
        , KNeighborsRegressor(n_neighbors=3)
        , SVR(kernel='linear')
        # , LogisticRegression()
          ]



TestModels = pd.DataFrame()
tmp = {}
for model in models:
    m = str(model)
    tmp['Model'] = m[:m.index('(')]
    model.fit(Xtrn, Ytrn)
    tmp['R2_Y1'] = r2_score(Ytest, model.predict(Xtest))
    TestModels = TestModels.append([tmp])

TestModels.set_index('Model', inplace=True)
TestModels.R2_Y1.plot(kind = 'bar',  title='R2 score', figsize=(5, 4))
plt.show()


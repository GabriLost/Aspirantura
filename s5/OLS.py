""""
# coding=utf8
"""
import warnings
import pandas as pd
import matplotlib.pyplot as plt
from sklearn.ensemble import RandomForestRegressor
from sklearn.linear_model import LinearRegression, LogisticRegression
from sklearn.metrics import mean_squared_error, r2_score
from sklearn.neighbors import KNeighborsRegressor
from sklearn.svm import SVR

warnings.filterwarnings('ignore')
c_split_date = 30
c_r2s = 0.3;

tariff = pd.read_csv("files/tariff2.csv", sep=";", encoding="utf-8", engine='python').get(['DATE','RAIL','VALUE'])
economic = pd.read_csv("files/inflation3.csv", sep=";", encoding="utf-8", engine='python').get(['DATE','INF'])
# tariff = tariff.set_index(pd.to_datetime(tariff['DATE'], format='%d.%m.%Y')).drop(['DATE'], axis=1)

Ydata = tariff[['VALUE']]

Xdata = economic[['INF']]
# Xdata = pd.DataFrame([i for i in range(len(Ydata))])
print Ydata, Xdata
Xtrn  = Xdata[:c_split_date]
Ytrn =  Ydata[:c_split_date]
Xtest = Xdata[c_split_date:]
Ytest = Ydata[c_split_date:]


print(Xtrn,Ytrn)
models = [LinearRegression()]#, RandomForestRegressor(n_estimators=100, max_features ='sqrt'),KNeighborsRegressor(n_neighbors=3), SVR(kernel='linear')]
    # , LogisticRegression()


TestModels = pd.DataFrame()
tmp = {}
for model in models:
    model.fit(Xtrn, Ytrn)
    m = str(model)
    model_name = m[:m.index('(')]
    print(model.coef_)
    tmp['Model'] = m[:m.index('(')]
    tmp['R2_Y1'] = r2_score(Ytest, model.predict(Xtest))
    r2s = r2_score(Ytest, model.predict(Xtest))
    Ypred = pd.DataFrame(model.predict(Xdata)).set_index([Ydata.index.tolist()])
    # Ypred = pd.DataFrame(model.predict(Xtest)).set_index([Ytest.index.tolist()])
    fig = plt.figure(figsize=(6, 4))
    ax = fig.add_subplot(111)
    plt.title(model_name)
    ax.plot(Ydata, color='green', label='tariff')
    ax.plot(Xdata, color='blue',  label='inf')
    ax.plot(Ypred, color='red',   label='predict')
    ax.legend(loc='center left', bbox_to_anchor=(0.82,1.04))
    plt.show()

        #         TestModels = TestModels.append([tmp])
        # TestModels.set_index('Model', inplace=True)
        # TestModels.R2_Y1.plot(kind = 'bar',  title='R2 score', figsize=(5, 4))
        # plt.show()

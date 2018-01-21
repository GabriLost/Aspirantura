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

main_data = pd.read_csv("files/main_data.csv", sep=";", encoding="utf-8", engine='python').get(['TYPE', 'VALUE', 'DATE', 'DIR'])
tariff = pd.read_csv("files/tariff.csv", sep=";", encoding="utf-8", engine='python').get(['RAIL', 'TUBE', 'SEA', 'WATER', 'AUTO', 'AIR', 'DATE'])
economic = pd.read_csv("files/inflation.csv", sep=";", encoding="utf-8", engine='python').get(['DATE','INF'])

main_data = main_data.set_index(pd.to_datetime(main_data['DATE'], format='%d.%m.%y')).drop(['DATE'], axis=1)
tariff = tariff.set_index(pd.to_datetime(tariff['DATE'], format='%Y.%m.%d')).drop(['DATE'], axis=1)
economic = economic.set_index(pd.to_datetime(economic['DATE'], format='%d.%m.%Y')).drop(['DATE'], axis=1)

print (economic)


#
# data = main_data[
#            (main_data['TYPE'] == 'ALL')
#            & (main_data['DIR'] == 'LC')]['2014-01-01':'2017-07-01']
# # pd.to_numeric(main_data['VALUE'], downcast='float')
# # pd.to_numeric(tariff['RAIL'], downcast='float')
#
# data = data.join(tariff)
# print(data.corr())
#
# trg = data[['VALUE']].reset_index().drop('DATE', axis =1)
# trn = data.drop(['VALUE','TYPE','DIR'], axis=1).reset_index().drop('DATE', axis =1)
#
# Xtrn, Xtest, Ytrn, Ytest = train_test_split(trn, trg, test_size=0.2)
#
# models = [LinearRegression()
#         , RandomForestRegressor(n_estimators=100, max_features ='sqrt')
#         , KNeighborsRegressor(n_neighbors=6)
#         , SVR(kernel='linear')
#         # , LogisticRegression()
#           ]
#
# print(Xtrn, Xtest, Ytrn, Ytest)
#
# TestModels = pd.DataFrame()
# tmp = {}
# for model in models:
#     m = str(model)
#     tmp['Model'] = m[:m.index('(')]
#     model.fit(Xtrn, Ytrn)
#     tmp['R2_Y1'] = r2_score(Ytest, model.predict(Xtest))
#     TestModels = TestModels.append([tmp])
# TestModels.set_index('Model', inplace=True)
#
# fig, axes = plt.subplots(ncols=2, figsize=(10, 4))
# TestModels.R2_Y1.plot(ax=axes[0], kind='bar', title='R2_Y1')
# plt.show()
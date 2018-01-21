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
c_split_date = '2017-01-01'
c_r2s = 0.3;

main_data = pd.read_csv("files/main_data.csv", sep=";", encoding="utf-8", engine='python').get(['DATE', 'TYPE', 'VALUE', 'DIR'])
tariff = pd.read_csv("files/tariff.csv", sep=";", encoding="utf-8", engine='python').get(['DATE','RAIL'])#,'TUBE' , 'AUTO', 'SEA','AIR'
economic3 = pd.read_csv("files/inflation3.csv", sep=";", encoding="utf-8", engine='python').get(['DATE','INF'])
economic = pd.read_csv("files/inflation.csv", sep=";", encoding="utf-8", engine='pyt hon').get(['DATE','INF'])


main_data = main_data.set_index(pd.to_datetime(main_data['DATE'], format='%d.%m.%y')).drop(['DATE'], axis=1)
tariff = tariff.set_index(pd.to_datetime(tariff['DATE'], format='%Y.%m.%d')).drop(['DATE'], axis=1)
economic = economic.set_index(pd.to_datetime(economic['DATE'], format='%d.%m.%Y')).drop(['DATE'], axis=1)



for dir in ['LC', 'IM', 'EX', 'TR']:
    for type in ['CL1','WD','MIN','PTR','ORE','CL2','FRT','BRD','MTL','ALL']:
        data = main_data[(main_data['TYPE'] == type)
                   & (main_data['DIR'] == dir)]['2014-01-01':'2017-07-01']

        data = data.join(tariff).join(economic)

        Ydata = data[['VALUE']]
        Xdata = data.drop(['VALUE','TYPE','DIR'], axis=1)



        Xtrn  = Xdata[:c_split_date]
        Xtest = Xdata[c_split_date:]
        Ytrn =  Ydata[:c_split_date]
        Ytest = Ydata[c_split_date:]
        #
        # regr = LinearRegression()
        # regr.fit(Xtrn, Ytrn)
        # # The coefficients
        # # print('intercept_: ', regr.intercept_)
        # # print('Coefficients: ', regr.coef_)
        # #  The mean squared error
        # # print("Mean squared error: %.2f" % mean_squared_error(Ytest, regr.predict(Xtest)))
        # # Explained variance score: 1 is perfect prediction
        # print('Variance score: '+dir +'_'+ type+' %.2f' % r2_score(Ytest, regr.predict(Xtest)))

        models = [LinearRegression()
                , RandomForestRegressor(n_estimators=100, max_features ='sqrt')
                , KNeighborsRegressor(n_neighbors=3)
                , SVR(kernel='linear')
                # , LogisticRegression()
                  ]



        TestModels = pd.DataFrame()
        tmp = {}
        for model in models:

            model.fit(Xtrn, Ytrn)

            m = str(model)
            model_name = m[:m.index('(')]
            tmp['Model'] = m[:m.index('(')]
            tmp['R2_Y1'] = r2_score(Ytest, model.predict(Xtest))

            r2s = r2_score(Ytest, model.predict(Xtest))
            name = dir +'	'+ type+'	%.2f' % r2s +'	'+ model_name
            name1 = dir + ' ' + type + ' %.2f' % r2s + ' ' + model_name

            if r2s > c_r2s:
                print(name)
                Ypred = pd.DataFrame(model.predict(Xtest)).set_index([Ytest.index.tolist()])
                fig = plt.figure(figsize=(6, 4))
                ax = fig.add_subplot(111)
                plt.title(name1)
                ax.plot(Ytrn,  color='green', label='real')
                ax.plot(Ytest, color='blue',  label='real')
                ax.plot(Ypred, color='red',   label='predict')
                ax.legend(loc='center left', bbox_to_anchor=(0.82,1.04))
                plt.gcf().savefig('pictures/best/'+name1+'.png')

                # plt.show()
                fig.clf();
        #         TestModels = TestModels.append([tmp])
        # TestModels.set_index('Model', inplace=True)
        # TestModels.R2_Y1.plot(kind = 'bar',  title='R2 score', figsize=(5, 4))
        # plt.show()

import warnings
import itertools
from unittest import result

import pandas as pd
import numpy as np
import statsmodels.api as sm
import matplotlib.pyplot as plt
from sklearn.metrics import r2_score

plt.style.use('fivethirtyeight')

main_data = pd.read_csv("files/main_data.csv", sep=";", encoding="utf-8", engine='python').get(['DATE', 'TYPE', 'VALUE', 'DIR'])
main_data = main_data.set_index(pd.to_datetime(main_data['DATE'], format='%d.%m.%y')).drop(['DATE'], axis=1)
economic3 = pd.read_csv("files/inflation3.csv", sep=";", encoding="utf-8", engine='python').get(['DATE','INF'])
economic = economic3.set_index(pd.to_datetime(economic3['DATE'], format='%d.%m.%Y')).drop(['DATE'], axis=1)




y = main_data[
           (main_data['TYPE'] == 'FRT')
           & (main_data['DIR'] == 'LC')]['2014-01-01':'2017-07-01'].get(['VALUE'])
# y = main_data[
#            (main_data['TYPE'] == 'ALL')
#            & (main_data['DIR'] == 'LC')]['2014-01-01':'2017-07-01'].get(['VALUE'])

# print y
# y.plot(figsize=(15, 6))
# plt.show()

p = d = q = range(0, 2)
# pdq = list(itertools.product(p, d, q))
# seasonal_pdq = [(x[0], x[1], x[2], 12) for x in list(itertools.product(p, d, q))]
# print('Examples of parameter combinations for Seasonal ARIMA...')
# print('SARIMAX: {} x {}'.format(pdq[6], seasonal_pdq[6]))
# SARIMAX(0, 1, 0)x(1, 1, 0, 12)
# SARIMAX(1, 1, 0)x(1, 1, 0, 12)

pdq = (0, 1, 0)
seasonal_pdq = (1, 1, 0, 12)
from_date = '2015-02-01'
tmp = 1000000000000000
r2s = 0
string = 's'
y1 = 0
y2 = 0

# # Graph data
# fig, axes = plt.subplots(1, 2, figsize=(15, 4))
#
# fig = sm.graphics.tsa.plot_acf(y.ix[1:, 'VALUE'], lags=40, ax=axes[0])
# fig = sm.graphics.tsa.plot_pacf(y.ix[1:, 'VALUE'], lags=40, ax=axes[1])
# plt.show()
# exit(5)
# # Dataset
# data = y
#
# # data.index = data.t
# data['ln_value'] = np.log(data['VALUE'])
# data['D.ln_value'] = data['ln_value'].diff()
# print(data)
# # Graph data
# fig, axes = plt.subplots(1, 2, figsize=(15,4))
#
# # Levels
# axes[0].plot(data.index._mpl_repr(), data['VALUE'], '-')
# axes[0].set(title='value')
#
# # Log difference
# axes[1].plot(data.index._mpl_repr(), data['D.ln_value'], '-')
# axes[1].hlines(0, data.index[0], data.index[-1], 'r')
# axes[1].set(title='difference of logs');
#
#




warnings.filterwarnings("ignore")
p = d = q = range(0, 2)
pdq = list(itertools.product(p, d, q))
seasonal_pdq = [(x[0], x[1], x[2], 12) for x in list(itertools.product(p, d, q))]

tmp = 1000000000000000
r2s = 0
string = 's'
y1 = 0
y2 = 0
mse = 0
# SARIMAX(1, 1, 0)x(1, 1, 0, 12)
warnings.filterwarnings("ignore")
for param in pdq:
    for param_seasonal in seasonal_pdq:
        try:
            # if param != (0,0,0):
            #     continue
            if param_seasonal != (1, 0, 0, 12):
                continue
            mod = sm.tsa.statespace.SARIMAX(y, exog=economic, order = param, seasonal_order=param_seasonal, enforce_stationarity=False, enforce_invertibility=True)
            results = mod.fit(disp=True)

            # print(results.summary().tables[1])
            # results.plot_diagnostics(figsize=(15, 12))
            # plt.show()
            pred = results.get_prediction(start=pd.to_datetime(from_date), dynamic=False)
            pred_ci = pred.conf_int()
            y_forecasted = pred.predicted_mean
            y_truth = y[from_date:]
            print(results.summary())
            # print param, param_seasonal, 'here'
            mse = ((y_forecasted - y_truth['VALUE']) ** 2).mean()
            r2s = r2_score(y_truth, y_forecasted)
            # print y_forecasted.values
            # print y_truth.values
            ax = y[:].plot(label='observed')

            pred.predicted_mean.plot(ax=ax, label='Forecast', alpha=.8, color='red')
            ax.fill_between(pred_ci.index, pred_ci.iloc[:, 0], pred_ci.iloc[:, 1], color='k', alpha=.1)
            # print "index", pred_ci.index
            # print "iloc0", pred_ci.iloc[:, 0]
            # print "iloc1", pred_ci.iloc[:, 1]
            ax.set_xlabel('Date')
            ax.set_ylabel('Value')
            print (param, param_seasonal)
            plt.legend()
            plt.show()
        except:
            print ('error')

print string
print r2s
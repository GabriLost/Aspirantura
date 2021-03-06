import warnings
import itertools
import pandas as pd
import numpy as np
import statsmodels.api as sm
import matplotlib.pyplot as plt
plt.style.use('fivethirtyeight')

main_data = pd.read_csv("files/main_data.csv", sep=";", encoding="utf-8", engine='python').get(['DATE', 'TYPE', 'VALUE', 'DIR'])


main_data = main_data.set_index(pd.to_datetime(main_data['DATE'], format='%d.%m.%y')).drop(['DATE'], axis=1)



y = main_data[
           (main_data['TYPE'] == 'ALL')
           & (main_data['DIR'] == 'LC')]['2014-01-01':'2017-07-01'].get(['VALUE'])
# y = main_data[
#            (main_data['TYPE'] == 'ALL')
#            & (main_data['DIR'] == 'LC')]['2014-01-01':'2017-07-01'].get(['VALUE'])

# print y
# y.plot(figsize=(15, 6))
# plt.show()

p = d = q = range(0, 2)
pdq = list(itertools.product(p, d, q))
seasonal_pdq = [(x[0], x[1], x[2], 12) for x in list(itertools.product(p, d, q))]
print('Examples of parameter combinations for Seasonal ARIMA...')
print('SARIMAX: {} x {}'.format(pdq[6], seasonal_pdq[6]))


warnings.filterwarnings("ignore")
# for param in pdq:
#     for param_seasonal in seasonal_pdq:
#         try:
#             mod = sm.tsa.statespace.SARIMAX(y,order=param,seasonal_order=param_seasonal,enforce_stationarity=False,enforce_invertibility=False)
#             # mod = sm.tsa.statespace.SARIMAX(y,order=(1,1,1),seasonal_order=((1, 1, 0, 12),enforce_stationarity=False,enforce_invertibility=False)
#             results = mod.fit()
#             print('ARIMA{}x{}12 - AIC:{}'.format(param, param_seasonal, results.aic))
#         except:
#             continue
warnings.filterwarnings("ignore")

# ARIMA(1, 1, 1)x(1, 1, 0, 12)12 - AIC:309.560623821
# ARIMA(1, 1, 0)x(1, 1, 0, 12)12 - AIC:308.33101144

mod = sm.tsa.statespace.SARIMAX(y, order=(1, 1, 0), seasonal_order=(1, 1, 0, 12), enforce_stationarity=False, enforce_invertibility=False)
results = mod.fit()
print(results.summary().tables[1])
results.plot_diagnostics(figsize=(15, 12))
plt.show()

from_date = '2015-02-01'

pred = results.get_prediction(start=pd.to_datetime(from_date), dynamic=False)
pred_ci = pred.conf_int()



ax = y[from_date:].plot(label='observed')
pred.predicted_mean.plot(ax=ax, label='Forecast', alpha=.7)
ax.fill_between(pred_ci.index, pred_ci.iloc[:, 0],pred_ci.iloc[:, 1], color='k', alpha=.2)
ax.set_xlabel('Date')
ax.set_ylabel('Value')
plt.legend()
plt.show()

y_forecasted = pred.predicted_mean
y_truth = y[from_date:]
print y_forecasted
print y_truth['VALUE']
# Compute the mean square error
mse = ((y_forecasted - y_truth['VALUE']) ** 2).mean()
print('The Mean Squared Error of our forecasts is {}'.format(round(mse, 2)))

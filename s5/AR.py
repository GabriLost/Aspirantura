from pandas import Series
import pandas as pd
from matplotlib import pyplot
from statsmodels.tsa.ar_model import AR
from sklearn.metrics import mean_squared_error
import numpy as np

pyplot.style.use('seaborn')
main_data = pd.read_csv("files/train_data.csv", sep=";", encoding="utf-8", engine='python').get(['DATE','VALUE'])
main_data = main_data.set_index(pd.to_datetime(main_data['DATE'], format='%d.%m.%Y')).drop(['DATE'], axis=1)
# data = main_data[(main_data['TYPE'] == 'ALL')
#                    & (main_data['DIR'] == 'LC')]['2014-01-01':'2017-07-01']

X = main_data['VALUE'].values

# #
split = 6
train, test = X[:split], X[split:]

print len(train)
# train autoregression
model = AR(train)
# print ('recommended lag %s' % model.select_order(maxlag=split-2, ic='bic', trend='c'))
model_fit = model.fit(method='cmle', maxlag=3, trend='c', ic='aic')
print('Lag: %s' % model_fit.k_ar)
print('nobs: %s' % model_fit.nobs)
# print('X: %s' % model_fit.X)
print('k_trend: %s' % model_fit.k_trend)
print('Coefficients: %s' % model_fit.params)

# make predictions
predictions = model_fit.predict(start=len(train), end=len(train) + len(test) - 1, dynamic=False)
for i in range(len(predictions)):
    print('%f,%f' % (predictions[i], test[i]))
error = mean_squared_error(test, predictions)
print('Test MSE: %.3f' % error)


print (X)
print model_fit.predict(start=3, end=len(X), dynamic=False)
pyplot.plot(X[2:],linewidth=4.0)
# pyplot.plot(predictions, color='red')
pyplot.title("Autoregression")
pyplot.plot(model_fit.predict(start=3, end=len(X), dynamic=False), color='red', linewidth=4.0)
pyplot.show()

import statsmodels.api as sm
import pandas as pd
import matplotlib.pyplot as plt
plt.style.use('seaborn')
main_data = pd.read_csv("files/main_data.csv", sep=";", encoding="utf-8", engine='python').get(['DATE', 'TYPE', 'VALUE', 'DIR'])
main_data = main_data.set_index(pd.to_datetime(main_data['DATE'], format='%d.%m.%y')).drop(['DATE'], axis=1)
train_data = main_data[
           (main_data['TYPE'] == 'WD')
           & (main_data['DIR'] == 'LC')]['2014-01-01':'2017-07-01'].get(['VALUE'])
# train_data = main_data[
#            (main_data['TYPE'] == 'WD')
#            & (main_data['DIR'] == 'LC')]['2014-01-01':'2017-07-01'].get(['VALUE'])

train_data['VALUE'] = train_data['VALUE'].as_matrix().astype("float64")
train_data = train_data['VALUE']


date = '2014-01-01'
td_train = train_data[:date]
td_test = train_data[date:]

print td_test

arima_model = sm.tsa.ARIMA(train_data, order=(12,0,0))  # p-roots d q,


arima_ar_res = arima_model.fit(disp=-1)
print(arima_ar_res.summary())
arima_predict_out = arima_ar_res.predict('2014-01-01', '2017-07-01')

print (arima_predict_out)
plt.plot(td_test, label = 'real', linewidth=3.0)
plt.plot(arima_predict_out, color = 'red', linestyle = '--' ,label = 'forecast', linewidth=3.0)
plt.legend(bbox_to_anchor=(1.00 ,1), loc = 1)
plt.title('ARMA')
# plt.gcf().savefig('pictures/arma.png')
plt.show()
import statsmodels.api as sm
import pandas as pd
import matplotlib.pyplot as plt

plt.style.use('seaborn')

train_data = pd.read_csv("files/train_data.csv", sep=";", encoding="utf-8", engine='python', parse_dates=True)#.get(['DATE' 'VALUE'])

train_data = train_data.set_index(train_data['DATE']).drop(['DATE'], axis=1)

train_data['VALUE'] = train_data['VALUE'].as_matrix().astype("float64")
train_data = train_data['VALUE']
print (train_data)

date = '01.01.2011'
td_train = train_data[:date]
td_test = train_data[date:]
print td_train, td_test


arima_model = sm.tsa.ARIMA(train_data, order=(3,0,0))  # p-roots d q,
arima_ar_res = arima_model.fit(disp=-1)
print(arima_ar_res.summary())
arima_predict_out = arima_ar_res.predict(date, '01.01.2016')

print (arima_predict_out)
plt.plot(arima_predict_out, color = 'red' ,label = 'forecast', linewidth=4.0)
plt.plot(train_data, color = 'blue' ,label = 'real' , linewidth=4.0)
# plt.legend(bbox_to_anchor=(1.00 ,1), loc = 1)
plt.title("Autoregression")
# plt.gcf().savefig('pictures/arma.png')
plt.show()
""""
# coding=utf8
"""
import pandas as pd
import matplotlib.pyplot as plt

main_data = pd.read_csv("files/main_data.csv", sep=";", encoding="utf-8", engine='python').get(['DATE', 'TYPE', 'VALUE', 'DIR'])
tariff = pd.read_csv("files/tariff.csv", sep=";", encoding="utf-8", engine='python').get(['DATE','RAIL'])#,'TUBE' , 'AUTO', 'SEA','AIR'
economic3 = pd.read_csv("files/inflation3.csv", sep=";", encoding="utf-8", engine='python').get(['DATE','INF'])
economic = pd.read_csv("files/inflation.csv", sep=";", encoding="utf-8", engine='python').get(['DATE','INF'])


main_data = main_data.set_index(pd.to_datetime(main_data['DATE'], format='%d.%m.%y')).drop(['DATE'], axis=1)
tariff = tariff.set_index(pd.to_datetime(tariff['DATE'], format='%Y.%m.%d')).drop(['DATE'], axis=1)
economic = economic.set_index(pd.to_datetime(economic['DATE'], format='%d.%m.%Y')).drop(['DATE'], axis=1)

for dir in ['LC', 'IM', 'EX', 'TR']:
    for type in ['CL1','WD','MIN','PTR','ORE','CL2','FRT','BRD','MTL','ALL']:
        print(dir + type)
        data = main_data[(main_data['TYPE'] == type)
                   & (main_data['DIR'] == dir)]['2014-01-01':'2017-07-01']

# economic.join(tariff['RAIL']-100).join(data['VALUE']/1000).plot()
        data.plot()
        plt.title(dir +'_'+type)
        plt.gcf().savefig('pictures/'+dir +'_'+type+'.png')

economic.plot()
plt.title('economic')
plt.gcf().savefig('pictures/!economic.png')

economic3.plot()
plt.title('economic+')
plt.gcf().savefig('pictures/!economic+.png')

tariff.plot()
plt.title('tariff')
plt.gcf().savefig('pictures/!tariff.png')

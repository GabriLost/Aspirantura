import sys
from sklearn.model_selection import TimeSeriesSplit
import warnings
warnings.filterwarnings('ignore')
import pandas as pd
import numpy as np
from sklearn.metrics import mean_absolute_error, mean_squared_error

import statsmodels.formula.api as smf
import statsmodels.tsa.api as smt
import statsmodels.api as sm
import scipy.stats as scs
from scipy.optimize import minimize

import matplotlib.pyplot as plt

train_data = pd.read_csv("files/train_data.csv", sep=";", encoding="utf-8", engine='python',
                         parse_dates=True)  # .get(['DATE' 'VALUE'])
train_data = train_data.set_index(train_data['DATE']).drop(['DATE'], axis=1)
X = train_data['VALUE'].values
split = 10
train, test = X[:split], X[split:]

def exponential_smoothing(series, alpha):
    result = [series[0]]  # first value is same as series
    for n in range(1, len(series)):
        result.append(alpha * series[n] + (1 - alpha) * result[n - 1])
    return result

class HoltWinters:

    def __init__(self, series, slen, alpha, beta, gamma, n_preds, scaling_factor=1.96):
        self.series = series
        self.slen = slen
        self.alpha = alpha
        self.beta = beta
        self.gamma = gamma
        self.n_preds = n_preds
        self.scaling_factor = scaling_factor

    def initial_trend(self):
        sum = 0.0
        for i in range(self.slen):
            sum += float(self.series[i+self.slen] - self.series[i]) / self.slen
        return sum / self.slen

    def initial_seasonal_components(self):
        seasonals = {}
        season_averages = []
        n_seasons = int(len(self.series)/self.slen)
        for j in range(n_seasons):
            season_averages.append(sum(self.series[self.slen*j:self.slen*j+self.slen])/float(self.slen))

        for i in range(self.slen):
            sum_of_vals_over_avg = 0.0
            for j in range(n_seasons):
                sum_of_vals_over_avg += self.series[self.slen*j+i]-season_averages[j]
            seasonals[i] = sum_of_vals_over_avg/n_seasons
        return seasonals

    def triple_exponential_smoothing(self):
        self.result = []
        self.Smooth = []
        self.Season = []
        self.Trend = []
        self.PredictedDeviation = []
        self.UpperBond = []
        self.LowerBond = []

        seasonals = self.initial_seasonal_components()

        for i in range(len(self.series)+self.n_preds):
            if i == 0:
                smooth = self.series[0]
                trend = self.initial_trend()
                self.result.append(self.series[0])
                self.Smooth.append(smooth)
                self.Trend.append(trend)
                self.Season.append(seasonals[i%self.slen])

                self.PredictedDeviation.append(0)

                self.UpperBond.append(self.result[0] +
                                      self.scaling_factor *
                                      self.PredictedDeviation[0])

                self.LowerBond.append(self.result[0] -
                                      self.scaling_factor *
                                      self.PredictedDeviation[0])

                continue
            if i >= len(self.series):
                m = i - len(self.series) + 1
                self.result.append((smooth + m*trend) + seasonals[i%self.slen])

                self.PredictedDeviation.append(self.PredictedDeviation[-1]*1.01)

            else:
                val = self.series[i]
                last_smooth, smooth = smooth, self.alpha*(val-seasonals[i%self.slen]) + (1-self.alpha)*(smooth+trend)
                trend = self.beta * (smooth-last_smooth) + (1-self.beta)*trend
                seasonals[i%self.slen] = self.gamma*(val-smooth) + (1-self.gamma)*seasonals[i%self.slen]
                self.result.append(smooth+trend+seasonals[i%self.slen])

                self.PredictedDeviation.append(self.gamma * np.abs(self.series[i] - self.result[i])
                                               + (1-self.gamma)*self.PredictedDeviation[-1])

            self.UpperBond.append(self.result[-1] +
                                  self.scaling_factor *
                                  self.PredictedDeviation[-1])

            self.LowerBond.append(self.result[-1] -
                                  self.scaling_factor *
                                  self.PredictedDeviation[-1])

            self.Smooth.append(smooth)
            self.Trend.append(trend)
            self.Season.append(seasonals[i % self.slen])

def double_exponential_smoothing(series, alpha, beta):
    result = [series[0]]
    for n in range(1, len(series) + 1):
        if n == 1:
            level, trend = series[0], series[1] - series[0]
        if n >= len(series):
            value = result[-1]
        else:
            value = series[n]
        last_level, level = level, alpha * value + (1 - alpha) * (level + trend)
        trend = beta * (level - last_level) + (1 - beta) * trend
        result.append(level + trend)
    return result

def timeseriesCVscore(x):
    errors = []

    values = X
    alpha, beta, gamma = x

    tscv = TimeSeriesSplit(n_splits=3)

    for train, test in tscv.split(values):

        model = HoltWinters(series=values[train], slen = 1, alpha=alpha, beta=beta, gamma=gamma, n_preds=len(test))
        model.triple_exponential_smoothing()

        predictions = model.result[-len(test):]
        actual = values[test]
        error = mean_squared_error(predictions, actual)
        errors.append(error)
    return np.mean(np.array(errors))
#
# with plt.style.context('seaborn-white'):
#     plt.figure(figsize=(20, 8))
#     for alpha in [0.9, 0.8, 0.7, 0.6, 0.5, 0.4]:
#         plt.plot(exponential_smoothing(X, alpha), label="Alpha {}".format(alpha))
#     plt.plot(X, "c", label = "Actual")
#     plt.legend(loc="best")
#     plt.axis('tight')
#     plt.title("Exponential Smoothing")
#     plt.grid(True)
#     plt.show()

with plt.style.context('seaborn-white'):
    # plt.figure(figsize=(20, 8))

    for betta in [0.9, 0.7]:
        plt.plot(X[:], "c", color='blue', label="Actual", linewidth=3.0)
        for alpha in [0.9, 0.7, 0.5]:
            plt.plot(double_exponential_smoothing(X[:-1], alpha, betta), label="Alpha {}, Betta {}, ".format(alpha, betta))
        plt.legend(loc="best")
        plt.axis('tight')
        plt.title("double_exponential_smoothing")
        plt.grid(True)
        plt.show()


# Galpha = 0.5
# def plotHoltWinters():
#     Anomalies = np.array([np.NaN]*len(data))
#     Anomalies[data<model.LowerBond] = data[data<model.LowerBond]
#     plt.figure(figsize=(25, 10))
#     plt.plot(model.result, label = "Model")
#     plt.plot(model.UpperBond, "r--", alpha=Galpha, label = "Up/Low confidence")
#     plt.plot(model.LowerBond, "r--", alpha=Galpha)
#     plt.fill_between(x=range(0,len(model.result)), y1=model.UpperBond, y2=model.LowerBond, alpha=Galpha, color = "grey")
#     plt.plot(data, label = "Actual")
#     plt.plot(Anomalies, "o", markersize=10, label = "Anomalies")
#     plt.axvspan(len(data)-3, len(data), alpha=Galpha, color='lightgrey')
#     plt.grid(True)
#     plt.axis('tight')
#     plt.legend(loc="best", fontsize=13);
#     plt.show()
#
# data = X[:-3]
# x = [0, 0, 0]
#
# for name in ['Nelder-Mead','Powell','CG' ,'BFGS' ,'L-BFGS-B' ,'TNC']:
#     opt = minimize(timeseriesCVscore, x0=x, method=name, bounds=((0, 1.00), (0, 1.00), (0, 1.00)))
#     alpha_final, beta_final, gamma_final = opt.x
#     model = HoltWinters(data[:-3], slen=1, alpha=alpha_final, beta=beta_final, gamma=gamma_final, n_preds=3,
#                         scaling_factor=1.56)
#     model.triple_exponential_smoothing()
#     print(name, alpha_final, beta_final, gamma_final)
#     plotHoltWinters()

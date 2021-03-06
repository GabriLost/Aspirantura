from scipy.optimize import linprog, zeros
import numpy
import pandas as p
dCdT = [[0, 0, 777.22, 1299.44, 4155, 3032.43, 0, 7929.73, 7217.12, 0, 4991, 7385.1, 0, 2309.51, 0, 7496.56, 0, 2741.44, 5744.39],
        [0, 0, 1.5, 1.8, 4.6, 4.4, 0, 8.3, 8, 0, 2.4, 9, 0, 2.5, 0, 9.2, 0, 4.3, 5.6],
        [0, 0, 6178.86, 6931.73, 5016.55, 7649.26, 0, 3168, 7464.29, 0, 5285.71, 6418.68, 0, 7446.26, 0, 6172.86, 0, 7446.26, 5949.92],
        [0, 0, 10.5, 11.5, 7.9, 12.7, 0, 7.3, 9.7, 0, 8.2, 10.7, 0, 12.2, 0, 10.5, 0, 12.5, 10],
        [0, 0, 0, 4465, 11290, 10657, 0, 36645, 10080, 0, 14064, 30986, 0, 7446.26, 0, 32017, 0, 9509, 18600],
        [0, 0, 0, 4.2, 5.2, 4.3, 0, 9.9, 11.9, 0, 5.3, 13, 0, 12.2, 0, 8.8, 0, 4.3, 6.5],
        [0, 0, 4465, 0, 13242.63, 9283.1, 0, 34604.03, 29072.67, 0, 15559.12, 29929.72, 0, 4840.4, 0, 30756.51, 0,
         8283.1, 19344.62],
        [0, 0, 4.2, 0, 5.4, 6.1, 0, 12.2, 10.1, 0, 6.1, 10.6, 0, 3.1, 0, 10.8, 0, 5.9, 7.2],
        [0, 0, 11290, 13242.63, 0, 6377, 0, 7933, 8977, 0, 1773, 27152, 0, 15559.12, 0, 7478, 0, 6234, 5407],
        [0, 0, 5.2, 5.4, 0, 6.8, 0, 8, 11.4, 0, 3.2, 12.8, 0, 6.5, 0, 7.9, 0, 6.2, 6.1],
        [0, 0, 10657, 9283.1, 6377, 0, 0, 44939, 11242, 0, 23415, 36645, 0, 7876, 0, 37547, 0, 6409, 24598],
        [0, 0, 4.3, 6.1, 6.8, 0, 0, 10.4, 13.9, 0, 7.1, 15, 0, 5.8, 0, 9.7, 0, 4.1, 7.1],
        [0, 0, 15069.69, 17956.8, 6700.67, 22661.37, 0, 21426.36, 23651.3, 0, 8572, 20395.82, 0, 20395.82, 0, 20395.82,
         0, 21426.36, 14416.38],
        [0, 0, 6.9, 7.9, 4.8, 9.2, 0, 8.9, 9.5, 0, 6.1, 8.7, 0, 8.6, 0, 8.7, 0, 9, 6.8],
        [0, 0, 36645, 34604.03, 7933, 44939, 0, 0, 12729, 0, 8098, 37547, 0, 36879.17, 0, 9188, 0, 10340, 8875],
        [0, 0, 9.9, 12.2, 8, 10.4, 0, 0, 13.9, 0, 11.2, 15.1, 0, 13.3, 0, 12.7, 0, 13.1, 12.2],
        [0, 0, 10080, 10828, 8977, 11242, 0, 12729, 0, 0, 10080, 4603, 0, 11123.73, 0, 4160.85, 0, 11124, 5257],
        [0, 0, 11.9, 12.9, 11.4, 13.9, 0, 13.9, 0, 0, 11.9, 8.4, 0, 12.9, 0, 8.1, 0, 12.9, 9],
        [0, 0, 8935.22, 9689.66, 8407.4, 10316, 0, 10438.94, 2342, 0, 9209.83, 2035.97, 0, 10137.01, 0, 2001, 0,
         10137.01, 5523.98],
        [0, 0, 12.4, 13.4, 11.5, 14.1, 0, 14.8, 6.9, 0, 12.5, 6.1, 0, 13.8, 0, 6.7, 0, 13.9, 8.6],
        [0, 0, 14064, 15559.12, 1773, 23415, 0, 8098, 10080, 0, 0, 28549, 0, 17956.8, 0, 7823, 0, 6485, 5616],
        [0, 0, 5.3, 6.1, 3.2, 7.1, 0, 11.2, 11.9, 0, 0, 12.9, 0, 7.2, 0, 10.5, 0, 8.5, 8.4],
        [0, 0, 30989, 34360, 27152, 36645, 0, 37547, 11791, 0, 28549, 0, 0, 35309, 0, 1341, 0, 36645, 15203],
        [0, 0, 13, 14, 12.8, 15, 0, 15.1, 9.7, 0, 12.9, 0, 0, 15, 0, 6.8, 0, 14.9, 10],
        [0, 0, 11730.84, 15069.69, 15069.69, 9836, 0, 36879.17, 23651.3, 0, 18396.03, 26072.61, 0, 16202.09, 0,
         26990.93, 0, 14416.38, 14217.06],
        [0, 0, 11.5, 9.4, 9.3, 8.7, 0, 16.1, 12, 0, 10.3, 12.6, 0, 9.7, 0, 12.8, 0, 11.9, 9.2],
        [0, 0, 7121, 4840.4, 15559.12, 7876, 0, 36879.17, 29929.72, 0, 17956.8, 30756.51, 0, 0, 0, 31920.66, 0, 6703,
         20395.82],
        [0, 0, 4.8, 3.5, 6.5, 5.8, 0, 13.3, 10.9, 0, 7.2, 11.4, 0, 0, 0, 11.6, 0, 5.8, 8],
        [0, 0, 8130.57, 8935.22, 7536.32, 9209.83, 0, 11408.2, 2342, 0, 8407.4, 4782.08, 0, 9209.83, 0, 4419.62, 0,
         9209.83, 4212.19],
        [0, 0, 12.3, 13.3, 11.6, 13.7, 0, 17.1, 8, 0, 12.6, 9, 0, 13.7, 0, 8.8, 0, 13.8, 8.6],
        [0, 0, 32017, 30756.51, 7478, 37547, 0, 9188, 4160.85, 0, 7823, 1341, 0, 31920.66, 0, 0, 0, 11638, 6891],
        [0, 0, 8.8, 10.8, 7.9, 9.7, 0, 12.7, 8.1, 0, 10.5, 6.8, 0, 11.6, 0, 0, 0, 13.8, 8.8],
        [0, 0, 4891.34, 5648.73, 4161.99, 6108.63, 0, 7937.16, 3581.88, 0, 4891.34, 4161.99, 0, 5842.14, 0, 4339.9, 0,
         6108.63, 622.03],
        [0, 0, 6.8, 7.7, 5.9, 8.5, 0, 11.3, 9.2, 0, 6.8, 5.9, 0, 8.2, 0, 6.1, 0, 8.3, 2.9],
        [0, 0, 9509, 8283.1, 6234, 6409, 0, 10340, 11124, 0, 6485, 36645, 0, 6703, 0, 11638, 0, 0, 24598],
        [0, 0, 4.3, 5.9, 6.2, 4.1, 0, 13.1, 12.9, 0, 8.5, 14.9, 0, 5.8, 0, 13.8, 0, 0, 8.5],
        [0, 0, 18600, 19344.62, 5407, 24598, 0, 8875, 5257, 0, 5616, 15203, 0, 20395.82, 0, 6591, 0, 24598, 0],
        [0, 0, 6.5, 7.2, 6.1, 7.1, 0, 12.2, 9, 0, 8.4, 10, 0, 8, 0, 8.8, 0, 8.5, 0],
        [0, 0, 4477.2, 5285.54, 1897.92, 6108.63, 0, 6108.63, 6337.48, 0, 2112.71, 5842.14, 0, 5842.14, 0, 5842.14, 0,
         6108.63, 4339.9],
        [0, 0, 6.9, 7.8, 4.6, 9.1, 0, 9.1, 9.4, 0, 5.8, 8.6, 0, 8.5, 0, 8.6, 0, 8.9, 6.7]]
profit = [25041.74, 28529.2, 22764.95, 2651.32, 18104.86, 10556.84, 300, 300, 2720.74, 8889.6, 10702, 8170.2, 12182.88,
     14142.48, 17368.08]
T = [14.4, 12, 11, 4.3, 11.1, 8.8, 7.7, 7, 4.4, 8.1, 7.7, 7.1, 8.5, 8.6, 9]
x_min = [1000, 100, 700, 0, 0, 0, 0, 0, 800, 0, 0, 0, 750, 750, 0]
x_max = [2126, 1290, 1000, 200, 200, 500, 315, 100, 2000, 1500, 150, 290, 1236, 1235, 725]
num_car = [105, 78, 284, 239, 65, 247, 61, 71, 917, 357, 635, 36, 76, 792, 469, 39, 40, 29, 33, 364]
orders = [[9, 9, 9, 16, 12, 19, 14, 4, 3, 6, 18, 5, 11, 11, 8], [14, 13, 4, 12, 7, 9, 9, 9, 1, 20, 17, 2, 15, 10, 15]]
order_aim = [3, 4, 5, 6, 8, 9, 11, 12, 14, 16, 18, 19]

k = 15
m = 12
n = 20

z = k + n * m - m

dC = [[0 for i in range(m)] for j in range(n)]
dT = [[0 for i in range(m)] for j in range(n)]


for i in range(n):
    for j in range(m):
        try:
            dC[i][j] = dCdT[i * 2][order_aim[j]-1]
            dT[i][j] = dCdT[i * 2 - 1][order_aim[j]-1]
        except:
            print i, j, order_aim[j]-1,  "hash"
# print "dCdT", p.DataFrame(dCdT)
# print "dC", p.DataFrame(dC)
# print "dT", p.DataFrame(dT)

c = [0 for i in range(z)]

for i in range(k):
    c[i] = profit[i]

v = k
for i in range(n):
    for j in range(m):
        if dC[i][j] != 0:
            c[v] = -dC[i][j]
            v = v + 1

for i in range(len(c)):
    c[i] = -c[i]

LB = x_min
for i in range(z - k):
    x_min.append(0)
UB = x_max
for i in range(z - k):
    x_max.append(None)
    # x_max.append(None)


total = 0
for i in range(n):
     total = total + num_car[i]
print total

b = [total * 30]
for i in range(len(num_car)):
    b.append(num_car[i])

a = [[0 for x in range(z)] for y in range(n+1)]

for i in range(k):
     a[0][i] = T[i]

v = k + 0
for i in range(n):
    for j in range(m):
        if dC[i][j] != 0:
            a[0][v] = dT[i][j]
            v = v + 1

for i in range(k):
    a[orders[0][i]-1][i] = 1
    a[orders[1][i]][i] = -1

# print p.DataFrame(a)


num_koef = k
for i in range(n):
    for j in range(m):
        if dC[i][j] != 0:
            a[i+1][num_koef] = 1
            a[order_aim[j]-1][num_koef] = -1
            num_koef = num_koef+1

arr = []
for i in range(len(LB)):
    arr.append([LB[i], UB[i]])


bnds = tuple(map(tuple, arr))
print "c   ", len(c),  c
print "b    ", len(b), b
print "a    ", len(a), p.DataFrame(a)
print "bnds", bnds

from scipy.optimize import linprog

res = linprog(c, A_ub=a, b_ub=b, bounds=bnds, options={"disp": False}, method="simplex")
# print res
print res.x


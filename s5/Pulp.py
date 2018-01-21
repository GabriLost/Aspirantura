from pulp import *
import time

start = time.time()
x1 = pulp.LpVariable("x1", lowBound=0, upBound=90, cat='Integer')
x2 = pulp.LpVariable("x2", lowBound=0, cat='Integer')
problem = pulp.LpProblem('0' ,pulp.LpMaximize)
problem += 30*x1 +x2, "fun"
problem += 90*x1+ 5*x2 <= 10000, "1"
problem +=x2 ==3*x1, "2"
problem.solve()

print ("result")
for variable in problem.variables():
    print (variable.name, "=", variable.varValue)
print ("+")
print (value(problem.objective))
stop = time.time()
print ("time ")
print(stop - start)
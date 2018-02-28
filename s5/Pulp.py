from pulp import *
import time

start = time.time()
x1 = pulp.LpVariable("x1", lowBound=0, upBound=90, cat='Integer')
x2 = pulp.LpVariable("x2", lowBound=0, cat='Integer')
prob = pulp.LpProblem('0', pulp.LpMinimize)
prob += 30 * x1 + x2, ""
prob += 90 * x1 + 5 * x2 <= 10000, ""
prob += x2 == 3 * x1, "0"
prob.solve()
print ("result")
for variable in prob.variables():
    print (variable.name, "=", variable.varValue)
print ("obj")
print (value(prob.objective))

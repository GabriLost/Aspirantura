from pulp import *
import pandas as p

transfer_costs = {"1-2": 20,
         "2-1": 20,
         "1-0": 30,
         "2-0": 20,
         "0-2": 20,
         "0-1": 20}
order_costs = {"1-2": 500}
order_values = {"1-2": 20}
stations = {"1": 30,
            "2": 0,
            "3": 0}
tax = {"1": 10,
       "2": 10,
       "3": 2}
days = {"1": 1,
        "2": 1,
        "3": 1,
        "4": 1,
        "5": 1}
# print tax.keys()
# print days.keys()
# print sum(stations.values())
prob = pulp.LpProblem("Problem Name", pulp.LpMaximize)
total_x = len(days)*(len(transfer_costs)+len(stations))
print total_x
x = pulp.LpVariable.dicts("x", range(total_x), lowBound=0, cat=LpInteger)

# total for 1 day
for i in range(len(days)):
    for j in range(total_x/len(days)):
        prob += lpSum([x[j + i * total_x/len(days)]]) <= sum(stations.values()), "Max_in_1_day_%d" % (j + i * total_x/len(days))
prob += lpSum([x[i] for i in range(total_x)]) <= sum(stations.values()) * len(days), "total sum"
# cost for 1 day

for cost in transfer_costs:
    print day, cost
        # condition = lpSum(k[i*len(costs) + j] * costs[j][l] for l in range(len(cost[j])))
        # title = ("Cost_1_day_%d " % i)+" %d" % j
        # prob += condition, title
# end
prob.solve()

print "Status:", pulp.LpStatus[prob.status]
for v in prob.variables():
    print v.name, "=", v.varValue
print "Total Cost =", pulp.value(prob.objective)

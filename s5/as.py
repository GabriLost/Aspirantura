from pylab import dot, random
n = 50
a = 2. * random(n) - 1.
B = 2. * random((n, n)) - 1.
f = lambda (i, x): a[i] + dot(B[i], x)
objective = lambda x: max([f(i, x) for i in range(n)])

import pulp

def solve_minmax(n, a, B, x_min=None, x_max=None):
    x = pulp.LpVariable.dicts("x", range(n + 1), x_min, x_max)
    lp_prob = pulp.LpProblem("Minmax Problem", pulp.LpMinimize)
    lp_prob += pulp.lpSum([x[n]]), "Minimize_the_maximum"

    for i in range(n):
        label = "Max_constraint_%d" % i
        dot_B_x = pulp.lpSum([B[i][j] * x[j] for j in range(n)])
        condition = pulp.lpSum([x[n]]) >= a[i] + dot_B_x
        lp_prob += condition, label

    lp_prob.writeLP("MinmaxProblem.lp")  # optional
    lp_prob.solve()

    print "Status:", pulp.LpStatus[lp_prob.status]
    for v in lp_prob.variables():
        print v.name, "=", v.varValue
    print "Total Cost =", pulp.value(lp_prob.objective)

# solve_minmax(n, a, B, x_min=None, x_max=None)
x = pulp.LpVariable.dicts("x", range(20), (1, 2), (2000, 5000), cat='Integer')

print x

print x[0]

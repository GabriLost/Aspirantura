import sys
from ortools.constraint_solver import pywrapcp


def main():
    solver = pywrapcp.Solver('coloring')

    #
    # data
    #

    # max number of colors
    # [we know that 4 suffices for normal maps]
    nc = 5

    # number of nodes
    n = 11

    # set of nodes
    V = range(n)

    num_edges = 20

    #
    # Neighbours
    #
    # This data correspond to the instance myciel3.col from:
    # http://mat.gsia.cmu.edu/COLOR/instances.html
    #
    # Note: 1-based (adjusted below)
    E = [[1, 2],
         [1, 4],
         [1, 7],
         [1, 9],
         [2, 3],
         [2, 6],
         [2, 8],
         [3, 5],
         [3, 7],
         [3, 10],
         [4, 5],
         [4, 6],
         [4, 10],
         [5, 8],
         [5, 9],
         [6, 11],
         [7, 11],
         [8, 11],
         [9, 11],
         [10, 11]]

    #
    # declare variables
    #

    # x[i,c] = 1 means that node i is assigned color c
    x = {}
    for v in V:
        for j in range(nc):
            x[v, j] = solver.IntVar(0, 1, 'v[%i,%i]' % (v, j))
    x_flat = [x[(v, j)] for v in V for j in range(nc)]

    # u[c] = 1 means that color c is used, i.e. assigned to some node
    u = [solver.IntVar(0, 1, 'u[%i]' % i) for i in range(nc)]

    # number of colors used, to minimize
    obj = solver.Sum(u)

    #
    # constraints
    # a

    # each node must be assigned exactly one color
    for i in V:
        solver.Add(solver.Sum([x[i, c] for c in range(nc)]) == 1)

    # adjacent nodes cannot be assigned the same color
    # (and adjust to 0-based)
    for i in range(num_edges):
        for c in range(nc):
            solver.Add(x[E[i][0] - 1, c] + x[E[i][1] - 1, c] <= u[c])

    # objective
    objective = solver.Minimize(obj, 1)

    # #
    # # solution and search
    # #
    solution = solver.Assignment()
    solution.Add(x_flat)
    solution.Add(obj)
    solution.AddObjective(obj)

    db = solver.Phase(x_flat,
                      solver.INT_VAR_DEFAULT,
                      solver.ASSIGN_MIN_VALUE)

    solver.NewSearch(db, [objective])
    num_solutions = 0
    # while solver.NextSolution():
    #     # print "number of colors:", int(solver.Objective().Value())
    #     # print "colors used:", [int(u[i].SolutionValue()) for i in range(nc)]
    #
    # #
    #     # for v in V:
    #     #     print 'v%i' % v, ' color ',
    #     #     for c in range(nc):
    #     #         if int(x[v, c].SolutionValue()) == 1:
    #     #             print c
    #     num_solutions += 1

    solver.EndSearch()
    print
    print "num_solutions:", num_solutions
    print "failures:", solver.Failures()
    print "branches:", solver.Branches()
    print "WallTime:", solver.WallTime()


if __name__ == '__main__':
    main()

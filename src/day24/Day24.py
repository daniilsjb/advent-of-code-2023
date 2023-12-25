# For Part 1, see the Kotlin implementation.

import sympy

hailstones = []

with open('./Day24.txt', 'r') as file:
    for line in file:
        lhs, rhs = line.split(' @ ')

        hpx, hpy, hpz = tuple(map(int, lhs.split(',')))
        hvx, hvy, hvz = tuple(map(int, rhs.split(',')))

        hailstones.append((hpx, hpy, hpz, hvx, hvy, hvz))

px, py, pz = sympy.symbols('px, py, pz')
vx, vy, vz = sympy.symbols('vx, vy, vz')

equations = []

for hpx, hpy, hpz, hvx, hvy, hvz in hailstones:
    equations.append((px - hpx) * (hvy - vy) - (py - hpy) * (hvx - vx))
    equations.append((py - hpy) * (hvz - vz) - (pz - hpz) * (hvy - vy))

solutions = sympy.solve(equations)
print(solutions[0][px] + solutions[0][py] + solutions[0][pz])

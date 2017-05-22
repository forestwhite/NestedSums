# NestedSums
A collection of summation methods for large nested series to support the
batch calculation of quantum electrodynamics model statistics, such as 
linear entropy (second order estimate from Taylor expansion)
between potentially entangled coherent microwave fields.

Some series require optimized processes to produce a double precision
result in human-scale time given sufficiently large initial conditions.
* Singletons provide memoization optimization for calculating 
  coefficients
* ConcurrentSeries parallelizes nested sums with many terms

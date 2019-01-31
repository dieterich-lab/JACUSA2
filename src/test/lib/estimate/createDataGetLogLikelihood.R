source("dirmult_utils.R")

# data input
l <- list(
  list(c(10, 5, 1, 0), c(4, 3, 2, 1)),
  list(c(1, 1, 1, 1), c(2, 2, 2, 2), c(3, 3, 3, 3)))
m <- my_lmatrix(l)

# alpha(s)
a <- list(
  c(1, 1, 1, 1),
  c(10, 10, 10, 10),
  c(10, 5, 1, 1))

# replicate
len_m <- length(m)
len_a <- length(a)

m <- rep(m, len_a)
a <- rep(a, len_m)

# convert to data.frame
l <- my_mconvert(m, a, my_mloglik(m, a))

# write to file
my_write.csv(l, "dataGetLogLikelihood.csv")
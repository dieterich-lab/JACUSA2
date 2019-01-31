source("dirmult_utils.R")

# data input
l <- list(
  list(c(1, 1, 1, 1), c(2, 2, 2, 2)),
  list(c(10, 1, 1, 10), c(5, 1, 1, 5)))
m <- my_lmatrix(l)

ia <- lapply(m, my_init_alpha)

# estimate alpha and log-likelihood
res <- my_ldirmult(m)

l <- my_mconvert(m, 
                 ia = ia,
                 a = lapply(res, '[[', name = "a"),
                 LL = lapply(res, '[[', name = "LL"))

# write to file
my_write.csv(l, "dataMaximizeLogLikelihood.csv")
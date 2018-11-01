require("dirmult")

# format of csv files:
# data m
# alpha
# loglik

# replicates categories data[replicates*categories] alpha[categories] LL

# reads csv file with variable number of elements per row
my_read.csv <- function(file, sep = ",") {
  con = file(file, "r")
  l <- list()
  while (TRUE) {
    line = readLines(con, n = 1)
    if (length(line) == 0) {
      break
    }
    cols <- strsplit(line, split = sep)[[1]]
    values <-as.numeric(cols)
    categories = values[1]
    replicates = values[2]
    data_size <- categories * replicates
    values_length <- length(values)
    
    # add categories and replicates  
    row <- list(
      categories = categories,
      replicates = replicates)
    # add matrices
    row$m <- matrix(values[3:(2 + data_size)], ncol = categories, byrow = T)
    
    # add optional columns
    if (2 + data_size == values_length) { # no alpha, no LL
      # nothing to be done
    } else if (2 + data_size + 1 == values_length) { # add LL
      row$LL <- values[values_length]
    } else if (2 + data_size + categories == values_length) { # add alpha
      row$a <- values[(2 + data_size + 1):(2 + data_size + categories)]
    } else if (2 + data_size + categories + 1 == values_length) { # add LL, categories
      row$a <- values[(2 + data_size + 1):(2 + data_size + categories)]
      row$LL <- values[values_length]
    } else {
      browser()
      stop("Wrong number of columns")
    }
    l <- c(l, row)
  }
  close(con)
  l
}

# write csv file with variable number of elements per row
my_write.csv <- function(l, file, sep = ",") {
  cat("", file = file, append = F, fill = F)
  tmp <- lapply(l, function(row) {
    line <-c(row$categories,
             row$replicates,
             c(t(row$m))) 
    if (! is.null(row$ia)) {
      line <- c(line, row$ia)
    }
    if (! is.null(row$a)) {
      line <- c(line, row$a)
    }
    if (! is.null(row$LL)) {
      line <- c(line, row$LL)
    }
    cat(line, file = file, append = T, sep = sep, fill = F)
    cat("\n", file = file, append = T, sep = sep, fill = F)
  })
}

my_loglik <- function(m, a) {
  dirmult:::loglik(m, a)
}

my_lmatrix <- function(l) {
  lapply(l, function(x) { do.call(rbind, x) })
}

my_dirmult <- function(m) {
  l <- dirmult(m, trace = F)
  a <- l$gamma
  categories <- length(a)
  new_l <- list()
  new_l$a <- a
  new_l$LL <- l$loglik
  new_l
}

my_ldirmult <- function(m) {
  lapply(m, my_dirmult)
}

my_mloglik <- function(m, a) {
  mapply(my_loglik, m, a, SIMPLIFY = T)
}

my_convert <- function(m, ia = c(), a = c(), LL = c()) {
  categories <- ncol(m)
  replicates <- nrow(m)
  l <- list(
    categories = categories,
    replicates = replicates,
    m = m)
  if (length(ia) > 0) {
    l$ia <- ia
  }
  if (length(a) > 0) {
    l$a <- a
  }
  if (length(LL) > 0) {
    l$LL <- LL
  }
  l
}

my_init_alpha <- function(data) {
  mom <- weirMoM(data)
  if (mom <= 0) 
    mom <- 0.005
  initscalar <- (1 - mom)/mom
  colSums(data)/sum(data) * initscalar
}

my_mconvert <- function(m, ia = list(), a = list(), LL = list()) {
  args <- list()
  if (length(ia) > 0) {
    args$ia <- ia
  }
  if (length(a) > 0) {
    args$a <- a
  }
  if (length(LL) > 0) {
    args$LL <- LL
  }

  if (length(args) == 0) {
    lapply(m, my_convert)
  } else {
    args$SIMPLIFY <- F
    args$m <- m
    args$FUN <- my_convert
    do.call(mapply, args)
  }
}
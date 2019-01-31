require("dirmult")

COLUMN_SEP = "\t"
VALUE_SEP = ","

# format of csv files:
# data m
# alpha
# loglik

# replicates categories data[replicates*categories] alpha[categories] LL

# reads csv file with variable number of elements per row
my_read.csv <- function(file, column_sep = COLUMN_SEP, value_sep = VALUE_SEP) {
  con = file(file, "r")
  l <- list()
  while (TRUE) {
    line = readLines(con, n = 1)
    if (length(line) == 0) {
      break
    }
    cols <- strsplit(line, split = column_sep)[[1]]
    cols_length <- length(cols)
    values <- as.numeric(cols[1])
    values_length <- length(values)
    categories = values[1]
    data <- as.numeric(strsplit(values[2:values_length], split = value_sep)[[1]])
    replicates <- length(data) / categories

        # add categories and replicates  
    row <- list(
      categories = categories,
      replicates = replicates)
    # add matrices
    row$m <- matrix(data, ncol = categories, byrow = T)
    
    # add optional columns
    if (cols_length == 1) { # no alpha, no LL
      # nothing to be done
    } else if (cols_length == 3) { # add LL
      row$a <- as.numeric(strsplit(cols[2], split = value_sep)[[1]])
      row$LL <- as.numeric(cols[3])
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
my_write.csv <- function(l, file, column_sep = COLUMN_SEP, value_sep = VALUE_SEP) {
  cat("", file = file, append = F, fill = F)
  tmp <- lapply(l, function(row) {
    line <- paste(c(row$categories, t(row$m)), collapse = value_sep)
    if (! is.null(row$ia)) {
      line <- c(line, paste(row$ia, collapse = value_sep))
    }
    if (! is.null(row$a)) {
      line <- c(line, paste(row$a, collapse = value_sep))
    }
    if (! is.null(row$LL)) {
      line <- c(line, row$LL)
    }
    cat(line, file = file, append = T, sep = column_sep, fill = F)
    cat("\n", file = file, append = T, sep = column_sep, fill = F)
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
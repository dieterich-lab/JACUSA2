# function

QUALITY_START <- 0
QUALITY_END <- 41
BASES <- c("A", "C", "G", "T")

createPileup <- function(bases = list(), 
                       bcc = c("A" = 0, "C" = 0, "G" = 0, "T" = 0),
                       bcqc = list("A" = c(), "C" = c(), "G" = c(), "T" = c()),
                       l = list()) {
  
  # check only BASES are keys
  stopifnot(names(bcc) %in% BASES)
  stopifnot(names(bcqc) %in% BASES)
  # set defaults
  bcc <- ifelse(BASES %in% names(bcc), bcc, 0)
  names(bcc) <- BASES
  bcqc <- ifelse(BASES %in% names(bcqc), bcqc, list())
  names(bcqc) <- BASES
  # check "enough" BQs are provided
  for (base in BASES) {
    stopifnot(bcc[base] == length(bcqc[[base]]))
  }

  l$bases <- c(l$bases, list(bases))
  for (base in BASES) {
    bc <- paste0("c", base)
    l[[bc]] <- c(l[[bc]], bcc[[base]])
    bq <- paste0("q", base)
    l[[bq]] <- c(l[[bq]], list(bcqc[[base]]))
  }
  l
}

createSamplePileups <- function() {
  l <- createPileup(bases = BASES)
  l <- createPileup(bases = BASES, 
                    bcc = c("A" = 1), 
                    bcqc = list("A" = c(40)), 
                    l = l)
  l <- createPileup(bases = BASES, 
                    bcc = c("G" = 3), 
                    bcqc = list("G" = c(40, 40, 40)), 
                    l = l)
  l <- createPileup(bases = BASES, 
                    bcc = c("A" = 1, "C" = 2, "G" = 3, "T" = 4), 
                    bcqc = list("A" = c(10), "C" = c(20, 30), "G" = rep(30, 3), "T" = c(10, 10, 20, 30)), 
                    l = l)
  l
}

createP <- function(start = QUALITY_START, end = QUALITY_END) {
  df <- createErrorP(start, end)
  df$P <- 1.0 - df$errorP
  df$errorP <-NULL
  df
}

createErrorP <- function(start = QUALITY_START, end = QUALITY_END) {
  data.frame(byte = start:end, errorP = 10^(-(start:end / 10)), stringsAsFactors = F)
}

createColSumCount <- function() {
  l <- createSamplePileups()

    expected <- mapply(function(bases, cA, cC, cG, cT) {
    res <- c(0, 0, 0, 0)
    count <- c(cA, cC, cG, cT)
    i <- sort(match(bases, BASES))
    res[i] <- count[i]
    names(res) <- BASES
    res
  }, l$bases, l$cA, l$cC, l$cG, l$cT, USE.NAMES = T, SIMPLIFY = T)
  for (base in BASES) {
    e <- paste0("e", base)
    l[[e]] <- c(l[[e]], expected[base, ])
  }
  l
}

getNonRefBases <- function(base) {
  i <- BASES %in% base
  stopifnot(any(i))
  BASES[! i]
}

createColSumProb <- function() {
  l <- createSamplePileups()
  # phred 2 prob
  P <- createP()
  # calculate ColSumProb
  expected <- mapply(function(bases, qA, qC, qG, qT) {
    # norm. error prob.
    errorP <- createErrorP()
    errorP$errorP <- errorP$errorP / (length(bases) - 1)
    # container for output - prob.
    prob <- c("A" = 0, "C" = 0, "G" = 0, "T" = 0)
    # container for intput - phred
    phred <- list("A" = qA, "C" = qC, "G" = qG, "T" = qT)
    for (base in bases) {
      if (length(phred[[base]]) == 0) {
        next        
      }
      prob[base] <- prob[base] + sum(P[phred[[base]] + 1, "P"])
      nonRefBases <- getNonRefBases(base)
      for (nonRefBase in nonRefBases) {
        prob[nonRefBase] <- prob[nonRefBase] + sum(errorP[phred[[base]] + 1, "errorP"])
      }
    }
    prob
  }, l$bases, l$qA, l$qC, l$qG, l$qT, USE.NAMES = T, SIMPLIFY = F)
  expected <- do.call(rbind, expected)
  for (base in BASES) {
    e <- paste0("e", base)
    p <- paste0("p", base)
    l[[e]] <- c(l[[e]], expected[, base])
  }
  l
}

createColSumErrorProb <- function() {
  l <- createSamplePileups()
  # calculate ColSumProb
  expected <- mapply(function(bases, qA, qC, qG, qT) {
    # norm. error prob.
    errorP <- createErrorP()
    errorP$errorP <- errorP$errorP / (length(bases) - 1)
    # container for output - prob.
    prob <- c("A" = 0, "C" = 0, "G" = 0, "T" = 0)
    # container for intput - phred
    phred <- list("A" = qA, "C" = qC, "G" = qG, "T" = qT)
    for (base in bases) {
      if (length(phred[[base]]) == 0) {
        next        
      }
      nonRefBases <- getNonRefBases(base)
      for (nonRefBase in nonRefBases) {
        prob[nonRefBase] <- prob[nonRefBase] + sum(errorP[phred[[base]] + 1, "errorP"])
      }
    }
    prob
  }, l$bases, l$qA, l$qC, l$qG, l$qT, USE.NAMES = T, SIMPLIFY = F)
  expected <- do.call(rbind, expected)
  for (base in BASES) {
    e <- paste0("e", base)
    p <- paste0("p", base)
    l[[e]] <- c(l[[e]], expected[, base])
  }
  l
}

createColMeanErrorProb <- function() {
  l <- createColSumErrorProb()
  
  # calculate ColSumProb
  expected <- mapply(function(bases, cA, cC, cG, cT, eA, eC, eG, eT) {
    coverage <- cA + cC + cG + cT
    prob <- c("A" = eA, "C" = eC, "G" = eG, "T" = eT)
    for (base in bases) {
      prob[base] <- prob[base] / coverage
    }
    prob
  }, l$bases, l$cA, l$cC, l$cG, l$cT, l$eA, l$eC, l$eG, l$eT, USE.NAMES = T, SIMPLIFY = F)
  expected <- do.call(rbind, expected)
  for (base in BASES) {
    e <- paste0("e", base)
    p <- paste0("p", base)
    l[[e]] <- expected[, base]
  }
  l
}

createColMeanProb <- function() {
  l <- createColSumProb()
  
  # calculate ColSumProb
  expected <- mapply(function(bases, eA, eC, eG, eT) {
    sum <- eA + eC + eG + eT
    prob <- c("A" = eA, "C" = eC, "G" = eG, "T" = eT)
    for (base in bases) {
      prob[base] <- prob[base] / sum
    }
    prob
  }, l$bases, l$eA, l$eC, l$eG, l$eT, USE.NAMES = T, SIMPLIFY = F)
  expected <- do.call(rbind, expected)
  for (base in BASES) {
    e <- paste0("e", base)
    p <- paste0("p", base)
    l[[e]] <- expected[, base]
  }
  l
}

myWriteWrapper <- function(df, file) {
  write.table(df, file, sep = ",", dec = ".", quote = F, row.names = F, col.names = F)  
}

myWrite <- function(l, file, sep = "\t", sep2 = ",", sep3 = ";") {
  cat("", file = file, append = F, fill = F)
  zero <- mapply(function(bases, 
                          cA, cC, cG, cT, 
                          qA, qC, qG, qT, 
                          eA, eC, eG, eT) {

    line <- c(paste(bases, collapse = sep2),
              paste(cA, cC, cG, cT, sep = sep2),
              paste(ifelse(length(qA) > 0, paste0(qA, collapse = sep3), NA),
                    ifelse(length(qC) > 0, paste0(qC, collapse = sep3), NA),
                    ifelse(length(qG) > 0, paste0(qG, collapse = sep3), NA),
                    ifelse(length(qT) > 0, paste0(qT, collapse = sep3), NA),
                    sep = sep2),
              paste(eA, eC, eG, eT, sep = sep2) )
    cat(line, file = file, append = T, sep = sep, fill = F)
    cat("\n", file = file, append = T, sep = sep, fill = F)
  }, 
  l$bases, 
  l$cA, l$cC, l$cG, l$cT, 
  l$qA, l$qC, l$qG, l$qT, 
  l$eA, l$eC, l$eG, l$eT, 
  USE.NAMES = F, SIMPLIFY = F)
}

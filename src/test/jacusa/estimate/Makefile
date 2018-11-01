SCRIPTS := $(wildcard createData*.R)
CSV_DATA := $(patsubst %.R, %.csv, $(SCRIPTS))
CSV_DATA := $(patsubst createData%, data%, $(CSV_DATA))

DEPENDENT := dirmult_utils.R

.PHONY : all clean

all : $(CSV_DATA)

data%.csv : createData%.R $(DEPENDENT)
	Rscript $< 2>&1 | tee data$*.log

clean :
	rm -f $(CSV_DATA) $(CSV_DATA:%.csv=%.log)

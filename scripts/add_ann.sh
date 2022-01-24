#!/bin/bash

if ! command -v bedtools &> /dev/null
then
	echo "bedtools could not be found - install them or update PATH."
	exit
fi

if ! command -v awk &> /dev/null
then
	echo "awk could not be found - install it or create a symlink to awk."
	exit
fi

usage() {
	echo "Add gene name and feature to the info field of a JACUSA2 result file (header required)."
	echo -e "\nusage: $0 -j <JACUSA2> -a <GTF> > STDOUT"
	exit
}

if [[ $# -le 1 ]]
then
	usage
fi

while getopts j:a: flag
do
	case "${flag}" in
		j) jacusa=${OPTARG};;
		a) annotation=${OPTARG};;
		*) exit 1;;
	esac
done

if [[ ! -r $jacusa ]]
then
	echo "[ERROR] JACUSA file does not exist or cannot be read." >&2
	exit 1
fi

if [[ ! -r $annotation  ]]
then
	echo "[ERROR] GTF file does not exist or cannot be read." >&2
	exit 1
fi

bedtools intersect -s -header -loj -a $jacusa -b <(
	bedtools intersect -wo -s -a $annotation -b $jacusa | \
		awk -v FS="\t" -v OFS="\t" ' { $9=gensub(/.*gene_name "([^;]+)";.*/, "\\1", "g", $9) ; $9=$9"__"$3 ; print $10,$11,$12,$9,".",$15} ' | \
		sort -u | \
		bedtools sort -i - | \
		bedtools merge -s -i - -c 4,5,6 -o distinct,distinct,distinct
	) | \
	awk -v FS="\t" -v OFS="\t" -f <(cat - <<-"EOF"

	BEGIN 	{
			INFO = -1
			LAST = -1
			ATTRIBUTE = -1
	}

	$0 ~ /^#contig/ {
			for (i=1; i<=NF; i++) {
				if ($i == "info") {
					INFO=i
				}
			}
			LAST=INFO + 2
			ATTRIBUTE=LAST + 4
			print
	}

	INFO > 1 && $0 !~ /^#contig/ {
		if (! ($(ATTRIBUTE) == "." || $(ATTRIBUTE) == -1)) {
			if ($(INFO) == "*") {
				$(INFO) = ""
			} else {
				$(INFO) = $(INFO)";"
			}
			$(INFO) = $(INFO)"ann="$(ATTRIBUTE)
		}
		for (i=1; i<=LAST; i++) {
			printf("%s%s", $(i), i<LAST ? OFS : "\n")
		}
	}

	$0 ~ /^##/ { print }

EOF
)


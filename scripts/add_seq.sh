#!/bin/bash

if ! command -v bedtools &> /dev/null
then
	echo "bedtools could not be found - install them or update PATH."
	exit
fi

usage() {
	echo "Add +2bp downstream sequence of a SNV to the info field of a JACUSA2 result file (header required)."
	echo -e "\nusage: $0 -j <JACUSA2> -f <FASTA> > STDOUT"
	exit
}

if [[ $# -le 1 ]]
then
	usage
fi

while getopts j:f: flag
do
	case "${flag}" in
		j) jacusa=${OPTARG};;
		f) fasta=${OPTARG};;
		*) exit 1;;
	esac
done

if [[ ! -r $jacusa ]]
then
	echo "[ERROR] JACUSA file does not exist or cannot be read." >&2
	exit 1
fi

if [[ ! -r $fasta  ]]
then
	echo "[ERROR] FASTA file does not exist or cannot be read." >&2
	exit 1
fi

if [[ ! -r "$fasta.fai"  ]]
then
	echo "[ERROR] FASTA Index does not exist or cannot be read." >&2
	echo "[ERROR] Run: 'samtools faidx $fasta'" >&2
	exit 1
fi

bedtools intersect -header -s -loj -a $jacusa -b <(
	bedtools slop -s -i <(cut -f1-6 $jacusa | awk -v OFS="\t" ' { print $0,$1,$2,$3,"original",".",$6 } ') -g <(cut -f1-2 "$fasta.fai") -l 0 -r 2 | \
	bedtools getfasta -s -fi $fasta -bed - -bedOut | \
	awk -v OFS="\t" ' { print $7,$8,$9,$13,$11,$12 } '
) | \
	awk -v FS="\t" -v OFS="\t" -f <(cat - <<-"EOF"

	BEGIN 	{
			INFO = -1
			LAST = -1
			MOTIF = -1
	}

	$0 ~ /^#contig/ {
			for (i=1; i<=NF; i++) {
				if ($i == "info") {
					INFO=i
				}
			}
			LAST=INFO + 2
			SEQ=LAST + 4
			print
	}

	INFO > 1 && $0 !~ /^#contig/ {
		if (! ($(SEQ) == "." || $(SEQ) == -1)) {
			if ($(INFO) == "*") {
				$(INFO) = ""
			} else {
				$(INFO) = $(INFO)";"
			}
			$(INFO) = $(INFO)"seq="$(SEQ)
		}
		for (i=1; i<=LAST; i++) {
			printf("%s%s", $(i), i<LAST ? OFS : "\n")
		}
	}

	$0 ~ /^##/ { print }

EOF
)


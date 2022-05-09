#!/bin/bash


left=2
right=2

jacusa_results="/storage/JACUSA2_TestField/Nanopore/KO_vs_IVT_RC22_call2_result.out /storage/JACUSA2_TestField/Nanopore/WT_vs_IVT_RC22_call2_result.out /storage/JACUSA2_TestField/Nanopore/WT_vs_KO_RC22_call2_result.out"
fasta="/storage/JACUSA2_TestField/GRCh38_96.fa"
fasta_index="$fasta.fai"

cut -f 1-6 $jacusa_results | \
	head -n 1000 | \
	awk -v OFS="\t" ' { $4="site" ; $5=0 ; print } ' | \
	sort -u | \
	bedtools sort -i - | \
	bedtools slop -i - -g $fasta_index -s -l $left -r $right | \
	bedtools intersect -sorted -s -names KO_vs_IVT WT_vs_IVT WT_vs_KO -loj -a - -b $jacusa_results


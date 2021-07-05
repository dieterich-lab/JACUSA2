# Prepare data for m6A detection

select all sites of interest i.e. sites with an A in the reference
outside of a homopolymer region (filter Y)
```bash
sort -k5,5gr  WT_vs_KO_2samp_RC22_call2_result.out |awk '($13=="A" && $12!~/Y/)' > WT_vs_KO_2samp_RC22_call2_result.txt
sort -k5,5gr  WT100_vs_WT0_RC22_call2_result.out |awk '($14=="A" && $13!~/Y/)' >  WT100_vs_WT0_RC22_call2_result.txt

```
aggregate to capture all sites
```bash
cut -f 1-6 WT_vs_KO_2samp_RC22_call2_result.txt WT100_vs_WT0_RC22_call2_result.txt | awk '{print $1"\t"$2"\t"$3"\tCand\t0\t"$6;}' |sort -u > allSites.bed
```

## extend to to 5mer NNANN
```bash
srun bedtools slop -i allSites2.bed -g hg38.genome -b 2 > allSitesExt2.bed
```

## collect all information of 5mer context
```bash
INP="WT_vs_KO_2samp_RC22_call2_result.out WT100_vs_WT0_RC22_call2_result.out"
srun bedtools intersect -filenames -loj -a allSitesExt2.bed -b ${INP} > call2_SitesExt2.bed
```

## reformat
```bash
cat call2_SitesExt2.bed |perl HEK293_data_prep.pl > call2_SitesExt2_indel_slim2.txt
```

## Run R script
HEK293_data_prep.R

## Run prediction in R
HEK293_data_prep_V2.R

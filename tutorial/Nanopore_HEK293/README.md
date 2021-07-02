# Prepare data for m6A detection

select all sites of interest i.e. sites with an A in the reference
outside of a homopolymer region (filter Y)
```bash
sort -k5,5gr  WT_vs_KO_2samp_RC22_call2_result.out |awk '($13=="A" && $12!~/Y/)' > WT_vs_KO_2samp_RC22_call2_result.txt
sort -k5,5gr  WT100_vs_WT0_RC22_call2_result.out |awk '($14=="A" && $13!~/Y/)' >  WT100_vs_WT0_RC22_call2_result.txt

```
aggregate to capture all sites
```bash
cut -f 1-6 *_RC22_call2_result.txt *RC22_rtarrest_plain_result.txt | awk '{print $1"\t"$2"\t"$3"\tCand\t0\t"$6;}' |awk '($6=="+")' | sort -u > allSites.bed
```

## special for rRNA annotation
```bash
srun bedtools intersect -a allSites.bed -b rnam.bed -loj |awk -F"\t" '{print $1"\t"$2"\t"$3"\t"$10"\t"$5"\t"$6;}' > allSites2.bed
srun bedtools slop -i allSites2.bed -g rRNA.genome -l 0 -r 1 -s > allSitesExt2.bed
```

## call2
```bash
INP="rmDup_SIIIRTMn_RC22_call2_result.out rmDup_SIIIRTMg_RC22_call2_result.out rmDup_HIVRT_RC22_call2_result.out"
srun bedtools intersect -filenames -loj -a allSitesExt2.bed -b ${INP} -s > call2_SitesExt2.bed
```
```bash
INP="rmDup_SIIIRTMn_RC22_rtarrest_plain_result.out rmDup_SIIIRTMg_RC22_rtarrest_plain_result.out rmDup_HIVRT_RC22_rtarrest_plain_result.out"
srun bedtools intersect -filenames -loj -a allSitesExt2.bed -b ${INP} -s > rtarrest_SitesExt2.bed
```

## reformat
```bash
cat call2_SitesExt2.bed |perl ./rRNA_HEK293_data_prep.pl > call2_SitesExt2_indel_slim2.txt

cat rtarrest_SitesExt2.bed |perl ./rRNA_HEK293_arrest_data_prep.pl > rtArrest_sitesExt2_slim2.txt
```

## Run R script
Tao_data_prep.R

## Run Neural network in R
Tao_data_NN_v3.R

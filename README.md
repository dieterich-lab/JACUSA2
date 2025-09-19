# JACUSA2
JAVA framework for accurate Variant assessment (JACUSA2) is a one-stop solution to detect single
nucleotide variants (SNVs) and reverse transcriptase induced arrest events in Next-generation 
sequencing (NGS) data.

[JACUSA2](https://github.com/dieterich-lab/JACUSA2) is a direct successor of 
[JACUSA1](https://github.com/dieterich-lab/JACUSA) --- JACUSA1 is hereby deprecated and won't be 
continued. All methods (*call-1*, *call-2*, and *pileup*) from JACUSA1 are available in JACUSA2.
The new release of JACUSA2 features great performance enhancements (~3 faster) for existing methods 
and adds new methods *rt-arrest* and *lrt-arrest* (EXPERIMENTAL) that enable identification of  read arrest events.

Check the [manual](https://github.com/dieterich-lab/JACUSA2/blob/master/manual/manual.pdf) for further details.

## Requirements
JACUSA2 does not require any configuration but needs a correctly configured Java environment.
We developed and tested JACUSA2 with Java v1.8. If you encounter any Java related problems please
consider to change to Java v1.8.

## Installation
The latest version of JACUSA2 can be obtained from [all releases](https://github.com/dieterich-lab/JACUSA2/releases).

## Usage
Available methods in JACUSA2:

```
$ java -jar jacusa2.jar
usage: JACUSA2 <METHOD> <METHOD-OPTIONs> <BAMs>
  METHOD     DESCRIPTION
  call-1     Call variants - 1 condition
  call-2     Call variants - 2 conditions
  pileup     SAMtools like mpileup - 2 conditions
  rt-arrest  Reverse Transcription Arrest - 2 conditions
  lrt-arrest Linkage arrest to base substitution - 2 conditions
  [...]
```

Get supported options for a method (e.g.: call-1):
 
```
$ java -jar jacusa2.jar call-1
usage: JACUSA call-1 [OPTIONS] BAM1_1[,BAM1_2,...]
 -A                    Show all sites - including sites without variants
 -a <FEATURE-FILTER>   [...] Use -h to see extended help
 -B <READ-TAG>         Tag reads by base substitution.
                       Count non-reference base substitution per read and stratify.
                       Requires stranded library type.
                       (Format for T to C mismatch: T2C; use ',' to separate substitutions)
                       Default: none
 -b <BED>              BED file to scan for variants
 -c <MIN-COVERAGE>     filter positions with coverage < MIN-COVERAGE
                       default: 5
  [...]
```

Replicates or multiple bam files are separated by ",": 

```
java -jar jacusa.jar call-2 -r JACUSA.out -a H:1 \
  gDNA.bam \
  cDNA_replicate_1.bam,cDNA_replicate_2,bam,cDNA_replicate_3.bam
```

Check [manual](https://github.com/dieterich-lab/JACUSA2/blob/master/manual/manual.pdf) for detailed method specific options.

### Required input
JACUSA2 requires indexed BAM files. 
In order to create a BAM file index for an existing file `align.bam`, use [samtools](http://www.htslib.org/) and execute the following:

```
$ samtools index align.bam
```

For further details and sam->bam conversion, please check the [samtools howtos](http://www.htslib.org/doc/#howtos).

Some methods and options require the "MD"-field of a BAM file to be correctly populated.
The "MD"-field stores information on mismatched and deleted reference bases.
It allows to reconstruct the original reference sequence from alignments stored in a BAM file.

Given the reference sequence `reference.fasta` from the mapping step, 
use the following command to populate the "MD"-field of an existing `align.bam`:

```
$ samtools calmd -b align.bam reference.fasta > align_md.bam
```

Check [samtools calmd](http://www.htslib.org/doc/samtools-calmd.html) for more details.

### General output format
JACUSA2 writes its output to a user specified file. When using multiple threads, JACUSA2 will
create a temporary file for each allocated thread in the temp directory that is provided by the 
JAVA Virtual Machine. Check the manual of your JAVA Virtual machine on how to change the temp directory.

Chosen command line parameters and current genomic position are printed to the command prompt and
serve as a status guard.

Output format of JACUSA2 is controlled by the `-f <FORMAT>` command line option. Support for output 
formats depends on the used method. 

A "##" prefixed header that contains JACUSA2 runtime specific data such as version info and command line options is added to the default output format.
The default output format is a combination of
[BED6](http://genome.ucsc.edu/FAQ/FAQformat.html#format1) with
JACUSA2 methods specific columns and common info columns: "info", "filter", and "ref". 
The actual number of columns depends on the JACUSA2 method and the number of provided BAM files.

Columns     | 1-6  |  7 - (N-3)      | (N-2) - N
----------- | ---- | --------------- | ------------------------------------------------ 
Description | BED6 | Method specific | (General) info, filter, and ref(erence) specific

## Identifying variants
Robust identification of variants has proven to be a daunting task due to artefacts specific for 
NGS-data and employed mapping strategies. 
We implement various artefact/feature filters (check [manual](https://github.com/dieterich-lab/JACUSA2/blob/master/manual/manual.pdf) for "-a [...]) 
that reduce the number of false positives.

JACUSA2 supports two modes of sample setups of variant calling: 
* single (*call-1*) or 
* paired samples (*call-2*).

### call-1
The method *call-1* identifies variants against the reference sequence.
BAM files with a properly populated "MD"-field are required - check [JACUSA2 - Required input](#required-input) and [SAM Tags specification](https://samtools.github.io/hts-specs/SAMtags.pdf).

```
$ java -jar jacusa2.jar call-1 results_call1.out alignments.bam
```

### call-2
The method *call-2* identifies variants in 2 conditions.

```
$ java -jar jacusa2.jar call-2 results_call2.out condition1.bam condition2.bam
```


## Identifying arrest events
JACUSA2 supports two methods to identify arrest events by means of comparing counts of arrest and through 
reads: *rt-arrest* and *lrt-arrest*. Beyond read counts, JACUSA2 shows base counts from arrest and through reads.
This allows to inspect arrest events and variant calling simultaneously. 
It is mandatory to provide the library type by "-P" or "-P1" and "-P2"!

Check the section on arrest events in the [manual](https://github.com/dieterich-lab/JACUSA2/blob/master/manual/manual.pdf).

### rt-arrest
In this method, base call counts of arrest and read through reads are modelled by a Beta-Binomial distribution and 
differences between conditions are to be identified by means of a likelihood ratio test. Subsequent approximiation 
with $\chi^2$ distribution to compute a pvalue.

Sites are considered candidate arrest sites, if in all BAM files there is at least one read through AND one  
read arrest event. Otherwise, there would be no difference between the conditions. 
Furthermore, coverage filter and minBASQ of base call apply that will affect the output. 

```
$ java -jar jacusa2.jar rt-arrest -P FR-SECONDSTRAND -o results_rt_arrest.out condition1_1.bam,...,condition1_N.bam condition2_1.bam,...,condition2_M.bam
```


### lrt-arrest (EXPERIMENTAL)
*lrt-arrest* allows to link pileups to their arrest position. Output consists of read arrest and read through counts and 
a references to the associated arrest positions. There are cases, where currently an arrest position cannot be defined, 
e.g.: non properly paired reads.

## JACUSA2helper
There is also a new version of [JACUSA2helper](https://github.com/dieterich-lab/JACUSA2helper) 
to support downstream analysis of JACUSA2 output. 
Additonally, some artefact filters have been removed from JACUSA1 in favour of the rewritten R helper package.
The old version of JACUSAhelper has been declared deprecated and won't be maintained anymore.

## Changelog
* JACUCA1 to JACUSA2
    * General
        * Complete code rework - ~3x faster than JACUSA2
        * Added "##" prefixed header line that captures CLI arguments
    * CLI changes
        * ALL two dash options "--option ..." have been removed
        * Use "-filterNH" and "-filterNM" instead of "--filterNH" and "--filterNM"
        * Library type option has changed: JACUSA1: "-P Lib1,Lib2", JACUSA2: "-P1 Lib1 -P2 Lib2"
    * New methods and options
        * Added *rt-arrest* method - Reverse Transcription Arrest - 2 conditions
        * Added *lrt-arrest* method - Linkage arrest to base substitution - 2 conditions
        * Added "-B <BASE-SUB>" option that to partition reads based on base substitution
        * Added "-I" or "-D" options to add insertion or deletion counts
    * Artefact filter
        * Added Exclude Site Filter (option: E)
        * Moved some filter to [JACUSA2helper](https://github.com/dieterich-lab/JACUSA2helper)
    * Library changes
        * Upgraded [commons-cli](https://commons.apache.org/proper/commons-cli) v1.2 to v1.4
        * Upgraded [commons-math3](https://commons.apache.org/proper/commons-math) v3.3 to v3.6.1
        * Ueplaced sam v1.92 with [htsjdk](https://github.com/samtools/htsjdk) v2.12.0
        * Removed ssj dependence

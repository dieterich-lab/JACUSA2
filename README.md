# JACUSA2
JAVA framework for accurate Variant assessment (JACUSA2) is a one-stop solution to detect single nucleotide variants (SNVs) and reverse transcriptase induced arrest events in Next-generation
sequencing (NGS) data.

JACUSA2 features great performance enhancements (~3 faster) for existing methods
and adds new methods *rt-arrest* and *lrt-arrest* (EXPERIMENTAL) to identify read arrest events.

Check the [manual](https://github.com/dieterich-lab/JACUSA2/blob/master/manual/manual.pdf) for further details.

## Requirements
JACUSA2 does not require any configuration but needs a correctly configured Java environment.
We developed and tested JACUSA2 with Java v17. If you encounter any Java related problems please consider to change to Java v17.

## Installation
The latest version of JACUSA2 can be obtained from [all releases](https://github.com/dieterich-lab/JACUSA2/releases).

### Compilation from source

JACUSA2 is built using [maven](https://maven.apache.org/).

Java 17 and Maven 3.0+ are required to compile JACUSA2.
JACUSA2 JAR will be available in `target/JACUSA2-<VERSION>.jar`.

Get source:

```
git clone git@github.com:dieterich-lab/JACUSA2.git
```

Built from source and packaged into a jar:

```
cd JACUSA2
mvn clean install
```
The final jar will be in `target/JACUSA2-<VERSION>.jar`.

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
Count non-reference base substitutions per read and stratify.
Requires a stranded library type.
(Format for T to C mismatch: T2C; use ',' to separate substitutions)
Default: none
-b <BED>              BED file to scan for variants
-c <MIN-COVERAGE>     filter positions with coverage < MIN-COVERAGE
default: 5
[...]
```

Replicates or multiple BAM files are separated by ",":

```
java -jar jacusa.jar call-2 -r JACUSA.out -a H:1 \
gDNA.bam \
cDNA_replicate_1.bam,cDNA_replicate_2,bam,cDNA_replicate_3.bam
```

Check [manual](https://github.com/dieterich-lab/JACUSA2/blob/master/manual/manual.pdf) for detailed method-specific options.

### Required input
JACUSA2 requires indexed BAM files.
To create a BAM file index for an existing file `align.bam`, use [samtools](http://www.htslib.org/) and execute the following:

```
$ samtools index align.bam
```

For further details and sam->bam conversion, please check the [samtools howtos](http://www.htslib.org/doc/#howtos).

Some methods and options require a BAM file's "MD" field to be correctly populated.
The "MD"-field stores information on mismatched and deleted reference bases.
It allows reconstructing the original reference sequence from alignments stored in a BAM file.

Given the reference sequence `reference.fasta` from the mapping step,
use the following command to populate the "MD"-field of an existing `align.bam`:

```
$ samtools calmd -b align.bam reference.fasta > align_md.bam
```

Check [samtools calmd](http://www.htslib.org/doc/samtools-calmd.html) for more details.

### General output format
JACUSA2 writes its output to a user-specified file. When using multiple threads, JACUSA2 creates a temporary file for each allocated thread in the temp directory provided by the JAVA Virtual Machine. Check your JAVA Virtual Machine manual for instructions on how to change the temp directory.

Chosen command line parameters and current genomic position are printed to the command prompt and
serve as a status guard.

The output format of JACUSA2 is controlled by the `-f <FORMAT>` command line option. Support for output
formats depend on the used method.

The default output format now includes a "##" prefixed header containing JACUSA2 runtime-specific data, such as version information and command line options.
The default output format is a combination of
[BED6](http://genome.ucsc.edu/FAQ/FAQformat.html#format1) with
JACUSA2 methods specific columns and common info columns: "info", "filter", and "ref".
The number of columns depends on the JACUSA2 method and the number of provided BAM files.

Columns     | 1-6  |  7 - (N-3)      | (N-2) - N
----------- | ---- | --------------- | ------------------------------------------------
Description | BED6 | Method specific | (General) info, filter, and ref(erence) specific

## Identifying variants
Robust identification of variants has proven daunting due to artefacts specific to NGS data and employed mapping strategies.
We implement various artefact/feature filters (check [manual](https://github.com/dieterich-lab/JACUSA2/blob/master/manual/manual.pdf) for "-a [...]) that reduce the number of false positives.

JACUSA2 supports two modes of sample setups for variant calling:
* single (*call-1*) or 
* paired samples (*call-2*).

### call-1
The method *call-1* identifies variants against the reference sequence.
BAM files with a correctly populated "MD" field are required - check [JACUSA2 - Required input](#required-input) and [SAM Tags specification](https://samtools.github.io/hts-specs/SAMtags.pdf).

```
$ java -jar jacusa2.jar call-1 results_call1.out alignments.bam
```

### call-2
The method *call-2* identifies variants in 2 conditions.

```
$ java -jar jacusa2.jar call-2 results_call2.out condition1.bam condition2.bam
```


## Identifying arrest events
JACUSA2 supports two methods to identify arrest events by comparing arrest counts and through reads: *rt-arrest* and *lrt-arrest*. Beyond read counts, JACUSA2 shows base counts from arrest and through reads.
This allows for the simultaneous inspection of arrest events and variant calling.
It is mandatory to provide the library type by "-P" or "-P1" and "-P2"!

Check the section on arrest events in the [manual](https://github.com/dieterich-lab/JACUSA2/blob/master/manual/manual.pdf).

### rt-arrest
In this method, base call counts of arrest and read through reads are modelled by a Beta-Binomial distribution, and differences between conditions are identified using a likelihood ratio test. Subsequent approximation with the $\chi^2$ distribution to compute a p-value.

Sites are considered candidate arrest sites if there is at least one read-through AND one read-arrest event in all BAM files. Otherwise, there would be no difference between the conditions.
Furthermore, the coverage filter and minBASQ of base call apply, which will affect the output.

```
$ java -jar jacusa2.jar rt-arrest -P FR-SECONDSTRAND -o results_rt_arrest.out condition1_1.bam,...,condition1_N.bam condition2_1.bam,...,condition2_M.bam
```


### lrt-arrest (EXPERIMENTAL)
*lrt-arrest* allows pileups to be linked to their arrest position. Output consists of read arrest and read through counts and references to the associated arrest positions. An arrest position cannot be defined in the case of non properly paired reads.

## JACUSA2helper
There is also a new version of [JACUSA2helper](https://github.com/dieterich-lab/JACUSA2helper)
to support downstream analysis of JACUSA2 output.
Additionally, some artefact filters have been removed from JACUSA1 in favour of the rewritten R helper package.
The old version of JACUSAhelper has been declared deprecated and won't be maintained anymore.

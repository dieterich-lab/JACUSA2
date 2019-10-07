# JACUSA2
JAVA framework for accurate Variant assessment (JACUSA2) is a one-stop solution to detect single
nucleotide variants (SNVs) and reverse transcriptase induced arrest events in Next-generation 
sequencing (NGS) data.

[JACUSA2](https://github.com/dieterich-lab/JACUSA2) is a direct successor of 
[JACUSA1](https://github.com/dieterich-lab/JACUSA) --- JACUSA1 is hereby deprecated and won't be 
continued. All methods (call-1, call-2, and pileup) from JACUSA1 are available in JACUSA2.
The new release of JACUSA2 features great performance enhancements (~3 faster) for existing methods 
and adds new methods rt-arrest and lrt-arrest that enable to identify read arrest events.

Check the [manual](https://github.com/dieterich-lab/JACUSA2/manual/manual.pdf) for further details.

## Requirements
JACUSA2 does not require any configuration but needs a correctly configured Java environment.
We developed and tested JACUSA2 with Java v1.8. If you encounter any Java related problems please
consider to change to Java v1.8.

## Installation
The latest version of JACUSA2 can be obtained from [all releases](https://github.com/dieterich-lab/JACUSA2/releases).

## Identifying variants
TODO
call
output

## Identifying arrest events
### rt-arrest
### lrt-arrest
TODO
call output

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
        * Added "rt-arrest" method - Reverse Transcription Arrest - 2 conditions
        * Added "lrt-arrest" method - Linkage arrest to base substitution - 2 conditions
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

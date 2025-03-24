package jacusa.io.format.call;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.GenotypeBuilder;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.variantcontext.writer.Options;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder.OutputType;
import htsjdk.variant.vcf.VCFConstants;
import htsjdk.variant.vcf.VCFFilterHeaderLine;
import htsjdk.variant.vcf.VCFFormatHeaderLine;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLine;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFStandardHeaderLines;
import jacusa.filter.FilterConfig;
import jacusa.filter.factory.FilterFactory;
import lib.cli.parameter.ConditionParameter;
import lib.data.ParallelData;
import lib.data.count.basecall.BaseCallCount;
import lib.data.result.Result;
import lib.io.ResultWriter;
import lib.util.AbstractTool;
import lib.util.Base;
import lib.util.coordinate.Coordinate;

public class VCFcallWriter 
implements ResultWriter  {

	private final String outputFileName;
	private final FilterConfig filterConfig;
	private final SAMSequenceDictionary dictionary;
	
	private final VariantContextWriter vcw;

	private final String source;

	public VCFcallWriter(
			final String outputFileName, 
			final FilterConfig filterConfig,
			final SAMSequenceDictionary dictionary) {

		this.outputFileName = outputFileName;
		this.filterConfig 	= filterConfig;
		this.dictionary 	= dictionary;
				
		final VariantContextWriterBuilder vcwb = new VariantContextWriterBuilder();
		vcwb.modifyOption(Options.DO_NOT_WRITE_GENOTYPES, false);
		vcwb.modifyOption(Options.WRITE_FULL_FORMAT_FIELD, true);
		vcwb.setOutputFile(new File(outputFileName));
		vcwb.setOutputFileType(OutputType.VCF);
		vcwb.setReferenceDictionary(dictionary);
		vcw = vcwb.build();
		
		source = AbstractTool.getLogger().getTool().getCall();
	}
	
	public String getInfo() {
		return outputFileName;
	}
	
	// date 
	private String getFileDate() {
		final StringBuilder sb = new StringBuilder();
		final int year = Calendar.getInstance().get(Calendar.YEAR);
		final int month = Calendar.getInstance().get(Calendar.MONTH);
		final int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		sb.append(year);
		if (month < 10)
			sb.append(0);	
		sb.append(month);
		if (day < 10)
			sb.append(0);
		sb.append(day);
		return sb.toString();
	}
	
	@Override
	public void writeHeader(List<ConditionParameter> conditionParameters) {
		final VCFHeader header = new VCFHeader();
		header.setSequenceDictionary(dictionary);
		header.addMetaDataLine(new VCFHeaderLine("source", 
				AbstractTool.getLogger().getTool().getCall()
			)
		);
		header.addMetaDataLine(new VCFHeaderLine("fileDate", getFileDate()));

		// add default info fields to header
		for (final String ID : new String[] {VCFConstants.DEPTH_KEY, VCFConstants.GENOTYPE_KEY}) {
			header.addMetaDataLine(VCFStandardHeaderLines.getFormatLine(ID));
		}
		
		// add additional format fields to header
		header.addMetaDataLine(new VCFFormatHeaderLine("BC", 4, VCFHeaderLineType.Integer, "Base call counts A,C,G,T"));
		
		// add filter descriptions to header
		for (final FilterFactory filterFactory : filterConfig.getFilterFactories()) {
			header.addMetaDataLine(new VCFFilterHeaderLine(Character.toString(filterFactory.getID()), filterFactory.getDesc()));
		}

		for (int conditionIndex = 0; conditionIndex < conditionParameters.size(); conditionIndex++) {
			final int replicates = conditionParameters.get(conditionIndex).getReplicateSize();
			for (int replicateIndex = 0; replicateIndex < replicates; replicateIndex++) {	
				final String sampleName = (conditionIndex + 1) + "" + (replicateIndex + 1);
				header.getGenotypeSamples().add(sampleName);
			}
		}
		
		vcw.setHeader(header);
		vcw.writeHeader(header);
	}

	@Override
	public void writeResult(final Result result) {
		final ParallelData parallelData = result.getParellelData();
		final Coordinate coordinate = parallelData.getCoordinate();
		
		final BaseCallCount bcc = parallelData.getCombPooledData().getPileupCount().getBCC();
		final Set<Base> observedBases = bcc.getAlleles();
		
		final Collection<Allele> alleles = new ArrayList<>(observedBases.size());
		final Base refBase = parallelData.getCombPooledData().getUnstrandedRefBase();
		alleles.add(Allele.create(refBase.getByte(), true));
		for (final Base base : parallelData.getCombPooledData().getPileupCount().getBCC().getAlleles()) {
			if (base != refBase) {
				alleles.add(Allele.create(base.getByte()));
			}
		}

		final VariantContextBuilder vcb = new VariantContextBuilder(
						source, 
						coordinate.getContig(), coordinate.get1Start(), coordinate.get1End(),
						alleles);

		if (! result.isFiltered()) {
			vcb.passFilters();
		}

		for (final FilterFactory filterFactory : filterConfig.getFilterFactories()) {
			final char filterID = filterFactory.getID();
			if (result.getFilterInfo().contains(filterID)) {
				vcb.filter(Character.toString(filterID));
			}
		}

		final int conditions = parallelData.getConditions();
		final List<Genotype> genotypes = new ArrayList<>(conditions);

		for (int conditionIndex = 0; conditionIndex < conditions; conditionIndex++) {
			final int replicates = parallelData.getReplicates(conditionIndex);
			for (int replicateIndex = 0; replicateIndex < replicates; replicateIndex++) {	
				final String sampleName = (conditionIndex + 1) + "" + (replicateIndex + 1);
				
				final BaseCallCount tmpBCC = 
						parallelData.getDataContainer(conditionIndex, replicateIndex)
							.getPileupCount().getBCC();
				final List<Allele> tmpAlleles = new ArrayList<>(tmpBCC.getAlleles().size());

				for (final Base base : tmpBCC.getAlleles()) {
					tmpAlleles.add(Allele.create(base.getByte(), base == refBase));
				}	

				final Genotype genotype = new GenotypeBuilder(sampleName, tmpAlleles)
						.noAD()
						.noGQ()
						.noPL()
						.DP(tmpBCC.getCoverage())
						.attribute("BC", tmpBCC.toString().replace(";", ",").replace("*", "0,0,0,0")) // ugly
						.make();
				genotypes.add(genotype);
			}
		}
		vcb.genotypes(genotypes);
		final VariantContext vc = vcb.make();
		vcw.add(vc);
	}

	@Override
	public void close() throws IOException {
		vcw.close();
	}
	
}

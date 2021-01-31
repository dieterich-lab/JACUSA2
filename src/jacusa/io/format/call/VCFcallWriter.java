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
		vcwb.setOutputFileType(OutputType.VCF);
		vcwb.setOutputFile(new File(outputFileName));
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
		header.addMetaDataLine(new VCFHeaderLine("source", AbstractTool.getLogger().getTool().getName() + "-" + AbstractTool.getLogger().getTool().getVersion()));
		header.addMetaDataLine(new VCFHeaderLine("fileDate", getFileDate()));
		
		// add default info fields to header
		for (final String ID : new String[] {VCFConstants.DEPTH_KEY}) {
			VCFStandardHeaderLines.getInfoLine(ID);
		}
		// add default format fields to header
		for (final String ID : new String[] {VCFConstants.DEPTH_KEY}) {
			VCFStandardHeaderLines.getFormatLine(ID);
		}

		// add additional format fields to header
		header.addMetaDataLine(new VCFFormatHeaderLine("BC", 4, VCFHeaderLineType.Integer, "Base call counts A,C,G,T"));

		// add filter descriptions to header
		for (final FilterFactory filterFactory : filterConfig.getFilterFactories()) {
			header.addMetaDataLine(new VCFFilterHeaderLine(Character.toString(filterFactory.getID()), filterFactory.getDesc()));
		}

		// filename of condition and replicate BAMs
		for (final ConditionParameter conditionParameter : conditionParameters) {
			for (String recordFilename : conditionParameter.getRecordFilenames())  {
				header.getGenotypeSamples().add(recordFilename);
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
		
		for (final Base base : Base.getNonRefBases(refBase)) {
			alleles.add(Allele.create(base.getByte()));
		}
		
		final VariantContextBuilder vcb = new VariantContextBuilder(
						source, 
						coordinate.getContig(), coordinate.get1Start(), coordinate.get1End(),
						alleles);
		
		if (! result.isFiltered()) {
			vcb.passFilters();
		}
		
		for (final FilterFactory filterFactory : filterConfig.getFilterFactories()) {
			final String c = Character.toString(filterFactory.getID());
			if (result.getFilterInfo(0).contains(c)) {
				vcb.filter(c);
			}
		}

		final int conditions = parallelData.getConditions();
		final List<Genotype> genotypes = new ArrayList<>(conditions);

		for (int condI = 0; condI < conditions; condI++) {
			final int replicates = parallelData.getReplicates(condI);
			for (int replicateI = 0; replicateI < replicates; replicateI++) {	
				final String sampleName = "sample " +  condI + replicateI;
				final BaseCallCount tmpBCC = 
						parallelData.getDataContainer(condI, replicateI)
							.getPileupCount().getBCC();
				final List<Allele> tmpAlleles = new ArrayList<>(tmpBCC.getAlleles().size());

				for (final Base base : tmpBCC.getAlleles()) {
					tmpAlleles.add(Allele.create(base.getByte()));
				}	

				final GenotypeBuilder genoTypeBuilder = new GenotypeBuilder(sampleName, tmpAlleles);
				genoTypeBuilder.DP(tmpBCC.getCoverage());
			
				final Genotype genotype = genoTypeBuilder.make();
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

package jacusa.io.format.call;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.VCFFilterHeaderLine;
import htsjdk.variant.vcf.VCFFormatHeaderLine;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFInfoHeaderLine;
import jacusa.filter.FilterConfig;
import jacusa.filter.factory.AbstractFilterFactory;
import lib.cli.parameter.AbstractConditionParameter;
import lib.data.AbstractData;
import lib.data.ParallelData;
import lib.data.has.HasPileupCount;
import lib.data.result.Result;
import lib.io.ResultWriter;
import lib.util.AbstractTool;
import lib.util.coordinate.Coordinate;

public class VCFcallWriter<T extends AbstractData & HasPileupCount, R extends Result<T>> 
implements ResultWriter<T, R>  {

	private final String filename;
	private final FilterConfig<T> filterConfig;

	private final VariantContextWriter vcw;

	private final String source;
	
	public VCFcallWriter(final String filename, final FilterConfig<T> filterConfig) {
		this.filename = filename;
		this.filterConfig = filterConfig;
				
		final VariantContextWriterBuilder vcwb = new VariantContextWriterBuilder();
		vcw = vcwb.build();
		
		source = AbstractTool.getLogger().getTool().getCall();
	}
	
	@Override
	public void writeHeader(List<AbstractConditionParameter<T>> conditionParameter) {
		final VCFHeader header = new VCFHeader();
		// add additional fields to header
		header.addMetaDataLine(new VCFInfoHeaderLine("BC", 4, VCFHeaderLineType.Integer, "Base counts A,C,G,T"));
		header.addMetaDataLine(new VCFFormatHeaderLine("DP", 1, VCFHeaderLineType.Integer, "Read Depth"));
		
		// add filter descriptions to header
		for (final AbstractFilterFactory<T> filterFactory : filterConfig.getFilterFactories()) {
			header.addMetaDataLine(new VCFFilterHeaderLine(Character.toString(filterFactory.getC()), filterFactory.getDesc()));
		}
		
		// TODO which on to use?
		vcw.setHeader(header);
		// vcw.writeHeader(header);
	}

	// TODO
	@Override
	public void writeResult(R result) {
		final ParallelData<T> parallelData = result.getParellelData();

		final Coordinate coordinate = parallelData.getCoordinate();
		
		final Collection<Allele> alleles = new ArrayList<Allele>(1);
		final byte refBase = parallelData.getCombinedPooledData().getReferenceBase();
		alleles.add(Allele.create(refBase, true));
		
		final VariantContextBuilder vcb = 
				new VariantContextBuilder(
						source, 
						coordinate.getContig(), 
						coordinate.getStart() + 1, 
						coordinate.getEnd(), 
						alleles);
		
		if (! result.isFiltered()) {
			vcb.passFilters();
		}
		
		for (final AbstractFilterFactory<T> filterFactory : filterConfig.getFilterFactories()) {
			final String c = Character.toString(filterFactory.getC());
			if (result.getFilterInfo().contains(c)) {
				vcb.filter(c);
			}
		}

		final VariantContext vc = vcb.make();
		vcw.add(vc);
	}

	@Override
	public String getInfo() {
		return filename;
	}

	@Override
	public void writeLine(final String line) {
		// FIXME change interface of ResultWriter and remove writeLine
		// not needed
	}

	@Override
	public void close() throws IOException {
		vcw.close();
	}
	
}

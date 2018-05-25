package jacusa.method.pileup;

import jacusa.cli.options.librarytype.OneConditionLibraryTypeOption;
import jacusa.cli.parameters.PileupParameter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.factory.HomozygousFilterFactory;
import jacusa.filter.factory.MaxAlleleCountFilterFactory;
import jacusa.filter.factory.basecall.CombinedFilterFactory;
import jacusa.filter.factory.basecall.INDEL_FilterFactory;
import jacusa.filter.factory.basecall.ReadPositionDistanceFilterFactory;
import jacusa.filter.factory.basecall.SpliceSiteFilterFactory;
import jacusa.io.format.pileup.BED6pileupDebugFormat;
import jacusa.io.format.pileup.BED6pileupResultFormat;
import jacusa.io.format.pileup.PileupLikeFormat;
import jacusa.worker.PileupWorker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lib.cli.options.BedCoordinatesOption;
import lib.cli.options.DebugModusOption;
import lib.cli.options.FilterConfigOption;
import lib.cli.options.FilterModusOption;
import lib.cli.options.HelpOption;
import lib.cli.options.MaxThreadOption;
import lib.cli.options.ReferenceFastaFilenameOption;
import lib.cli.options.ResultFileOption;
import lib.cli.options.ResultFormatOption;
import lib.cli.options.ShowReferenceOption;
import lib.cli.options.ThreadWindowSizeOption;
import lib.cli.options.WindowSizeOption;
import lib.cli.options.condition.MaxDepthConditionOption;
import lib.cli.options.condition.MinBASQConditionOption;
import lib.cli.options.condition.MinCoverageConditionOption;
import lib.cli.options.condition.MinMAPQConditionOption;
import lib.cli.options.condition.filter.FilterFlagConditionOption;
import lib.cli.options.condition.filter.FilterNHsamTagOption;
import lib.cli.options.condition.filter.FilterNMsamTagOption;
import lib.data.PileupData;
import lib.data.builder.factory.PileupDataBuilderFactory;
import lib.data.cache.extractor.ReferenceBaseSetter;
import lib.data.generator.PileupDataGenerator;
import lib.data.result.DefaultResult;
import lib.data.validator.paralleldata.MinCoverageValidator;
import lib.data.validator.paralleldata.ParallelDataValidator;
import lib.io.AbstractResultFormat;
import lib.method.AbstractMethodFactory;
import lib.util.AbstractTool;

import org.apache.commons.cli.ParseException;

public class PileupFactory 
extends AbstractMethodFactory<PileupData, DefaultResult<PileupData>> {
	
	public PileupFactory(PileupParameter pileupParameter) {
		super("pileup", "SAMtools like mpileup",
				pileupParameter, new PileupDataBuilderFactory(pileupParameter), new PileupDataGenerator());
	}

	public void initGeneralParameter(int conditionSize) {
		if (conditionSize == 0) {
			conditionSize = 3;
		}
		setParameter(new PileupParameter(conditionSize));
	}
	
	protected void initGlobalACOptions() {
		// result format option only if there is a choice
		if (getResultFormats().size() > 1 ) {
			addACOption(new ResultFormatOption<PileupData, DefaultResult<PileupData>>(getParameter(), getResultFormats()));
		}

		addACOption(new FilterModusOption(getParameter()));
		// addACOption(new BaseConfigOption(getParameter()));
		addACOption(new FilterConfigOption<PileupData>(getParameter(), getFilterFactories()));
		
		addACOption(new ShowReferenceOption(getParameter()));
		addACOption(new ReferenceFastaFilenameOption(getParameter()));
		addACOption(new HelpOption(AbstractTool.getLogger().getTool().getCLI()));
		
		addACOption(new MaxThreadOption(getParameter()));
		addACOption(new WindowSizeOption(getParameter()));
		addACOption(new ThreadWindowSizeOption(getParameter()));
		
		addACOption(new BedCoordinatesOption(getParameter()));
		addACOption(new ResultFileOption(getParameter()));
		
		addACOption(new DebugModusOption(getParameter()));
	}

	protected void initConditionACOptions() {
		// for all conditions
		addACOption(new MinMAPQConditionOption<PileupData>(getParameter().getConditionParameters()));
		addACOption(new MinBASQConditionOption<PileupData>(getParameter().getConditionParameters()));
		addACOption(new MinCoverageConditionOption<PileupData>(getParameter().getConditionParameters()));
		addACOption(new MaxDepthConditionOption<PileupData>(getParameter().getConditionParameters()));
		addACOption(new FilterFlagConditionOption<PileupData>(getParameter().getConditionParameters()));

		addACOption(new FilterNHsamTagOption<PileupData>(getParameter().getConditionParameters()));
		addACOption(new FilterNMsamTagOption<PileupData>(getParameter().getConditionParameters()));
		
		addACOption(new OneConditionLibraryTypeOption<PileupData>(getParameter().getConditionParameters(), getParameter()));
		
		// condition specific
		for (int conditionIndex = 0; conditionIndex < getParameter().getConditionsSize(); ++conditionIndex) {
			addACOption(new MinMAPQConditionOption<PileupData>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new MinBASQConditionOption<PileupData>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new MinCoverageConditionOption<PileupData>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new MaxDepthConditionOption<PileupData>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new FilterFlagConditionOption<PileupData>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			
			addACOption(new FilterNHsamTagOption<PileupData>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new FilterNMsamTagOption<PileupData>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			
			addACOption(new OneConditionLibraryTypeOption<PileupData>(
					conditionIndex, 
					getParameter().getConditionParameters().get(conditionIndex),
					getParameter()));
		}
	}

	public Map<Character, AbstractResultFormat<PileupData, DefaultResult<PileupData>>> getResultFormats() {
		final Map<Character, AbstractResultFormat<PileupData, DefaultResult<PileupData>>> outputFormats = 
				new HashMap<Character, AbstractResultFormat<PileupData, DefaultResult<PileupData>>>();

		AbstractResultFormat<PileupData, DefaultResult<PileupData>> outputFormat = 
				new PileupLikeFormat<PileupData>(getParameter());
		outputFormats.put(outputFormat.getC(), outputFormat);

		outputFormat = new BED6pileupResultFormat<PileupData, DefaultResult<PileupData>>(getParameter());
		outputFormats.put(outputFormat.getC(), outputFormat);

		outputFormat = new BED6pileupDebugFormat<PileupData, DefaultResult<PileupData>>(getParameter());
		outputFormats.put(outputFormat.getC(), outputFormat);
		
		return outputFormats;
	}

	public Map<Character, AbstractFilterFactory<PileupData>> getFilterFactories() {
		final Map<Character, AbstractFilterFactory<PileupData>> abstractPileupFilters = 
				new HashMap<Character, AbstractFilterFactory<PileupData>>();

		final List<AbstractFilterFactory<PileupData>> filterFactories = 
				new ArrayList<AbstractFilterFactory<PileupData>>(10);
		
		filterFactories.add(new CombinedFilterFactory<PileupData>());
		filterFactories.add(new INDEL_FilterFactory<PileupData>());
		filterFactories.add(new ReadPositionDistanceFilterFactory<PileupData>());
		filterFactories.add(new SpliceSiteFilterFactory<PileupData>());
		filterFactories.add(new HomozygousFilterFactory<PileupData>(getParameter()));
		filterFactories.add(new MaxAlleleCountFilterFactory<PileupData>());
		
		// FIXME filterFactories.add(new HomopolymerFilterFactory<PileupData>());

		for (final AbstractFilterFactory<PileupData> filterFactory : filterFactories) {
			abstractPileupFilters.put(filterFactory.getC(), filterFactory);
		}

		return abstractPileupFilters;
	}

	@Override
	public PileupParameter getParameter() {
		return (PileupParameter) super.getParameter();
	}
	

	@Override
	public boolean parseArgs(String[] args) throws Exception {
		if (args == null || args.length < 1) {
			throw new ParseException("BAM File is not provided!");
		}

		return super.parseArgs(args); 
	}

	@Override
	public List<ParallelDataValidator<PileupData>> getParallelDataValidators() {
		final List<ParallelDataValidator<PileupData>> validators = super.getParallelDataValidators();
		validators.add(new MinCoverageValidator<PileupData>(getParameter().getConditionParameters()));
		return validators;
	}
	
	@Override
	public PileupWorker createWorker(final int threadId) {
		return new PileupWorker(
				new ReferenceBaseSetter<PileupData>(),
				getWorkerDispatcher(), 
				threadId,
				getParameter().getResultFormat().createCopyTmp(threadId),
				getParallelDataValidators(),
				getParameter());
	}

}

package jacusa.method.pileup;

import jacusa.cli.options.librarytype.OneConditionLibraryTypeOption;
import jacusa.cli.parameters.PileupParameter;
import jacusa.filter.factory.AbstractFilterFactory;
import jacusa.filter.factory.CombinedDistanceFilterFactory;
import jacusa.filter.factory.HomopolymerFilterFactory;
import jacusa.filter.factory.HomozygousFilterFactory;
import jacusa.filter.factory.INDEL_DistanceFilterFactory;
import jacusa.filter.factory.MaxAlleleCountFilterFactory;
import jacusa.filter.factory.ReadPositionDistanceFilterFactory;
import jacusa.filter.factory.SpliceSiteDistanceFilterFactory;
import jacusa.io.copytmp.FileCopyTmpResult;
import jacusa.io.writer.BED6callResultFormat;
import jacusa.io.writer.BED6pileupResultFormat;
import jacusa.io.writer.PileupFormat;
import jacusa.worker.PileupWorker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lib.cli.options.BedCoordinatesOption;
import lib.cli.options.DebugModusOption;
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
import lib.data.AbstractData;
import lib.data.BaseCallData;
import lib.data.builder.factory.PileupDataBuilderFactory;
import lib.data.generator.BaseCallDataGenerator;
import lib.data.generator.DataGenerator;
import lib.data.has.hasBaseCallCount;
import lib.data.has.hasPileupCount;
import lib.data.has.hasReferenceBase;
import lib.data.result.DefaultResult;
import lib.data.validator.MinCoverageValidator;
import lib.data.validator.ParallelDataValidator;
import lib.io.AbstractResultFileWriter;
import lib.io.AbstractResultFormat;
import lib.method.AbstractMethodFactory;
import lib.util.AbstractTool;

import org.apache.commons.cli.ParseException;

public class PileupFactory<T extends AbstractData & hasBaseCallCount & hasPileupCount & hasReferenceBase> 
extends AbstractMethodFactory<T, DefaultResult<T>> {
	
	public PileupFactory(PileupParameter<T> pileupParameter, final DataGenerator<T> dataGenerator) {
		super("pileup", "SAMtools like mpileup",
				pileupParameter, new PileupDataBuilderFactory<T>(pileupParameter), dataGenerator);
	}

	public void initGeneralParameter(int conditionSize) {
		if (conditionSize == 0) {
			conditionSize = 3;
		}
		setParameter(new PileupParameter<T>(conditionSize));
	}
	
	protected void initGlobalACOptions() {
		// result format
		if (getResultFormats().size() == 1 ) {
			Character[] a = getResultFormats().keySet().toArray(new Character[1]);
			getParameter().setResultFormat(getResultFormats().get(a[0]));
		} else {
			getParameter().setResultFormat(getResultFormats().get(BED6callResultFormat.CHAR));
			addACOption(new ResultFormatOption<T, DefaultResult<T>>(getParameter(), getResultFormats()));
		}
		
		
		// addACOption(new FilterModusOption(getParameter()));
		// addACOption(new BaseConfigOption(getParameter()));
		// addACOption(new FilterConfigOption<T>(getParameter(), getFilterFactories()));
		
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
		addACOption(new MinMAPQConditionOption<T>(getParameter().getConditionParameters()));
		addACOption(new MinBASQConditionOption<T>(getParameter().getConditionParameters()));
		addACOption(new MinCoverageConditionOption<T>(getParameter().getConditionParameters()));
		addACOption(new MaxDepthConditionOption<T>(getParameter().getConditionParameters()));
		addACOption(new FilterFlagConditionOption<T>(getParameter().getConditionParameters()));
		
		addACOption(new FilterNHsamTagOption<T>(getParameter().getConditionParameters()));
		addACOption(new FilterNMsamTagOption<T>(getParameter().getConditionParameters()));
		
		addACOption(new OneConditionLibraryTypeOption<T>(getParameter().getConditionParameters(), getParameter()));
		
		// condition specific
		for (int conditionIndex = 0; conditionIndex < getParameter().getConditionsSize(); ++conditionIndex) {
			addACOption(new MinMAPQConditionOption<T>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new MinBASQConditionOption<T>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new MinCoverageConditionOption<T>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new MaxDepthConditionOption<T>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new FilterFlagConditionOption<T>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			
			addACOption(new FilterNHsamTagOption<T>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			addACOption(new FilterNMsamTagOption<T>(conditionIndex, getParameter().getConditionParameters().get(conditionIndex)));
			
			addACOption(new OneConditionLibraryTypeOption<T>(
					conditionIndex, 
					getParameter().getConditionParameters().get(conditionIndex),
					getParameter()));
		}
	}

	public Map<Character, AbstractResultFormat<T, DefaultResult<T>>> getResultFormats() {
		final Map<Character, AbstractResultFormat<T, DefaultResult<T>>> outputFormats = 
				new HashMap<Character, AbstractResultFormat<T, DefaultResult<T>>>();

		AbstractResultFormat<T, DefaultResult<T>> outputFormat = 
				new PileupFormat<T>(getParameter());
		outputFormats.put(outputFormat.getC(), outputFormat);

		outputFormat = new BED6pileupResultFormat<T, DefaultResult<T>>(getParameter());
		outputFormats.put(outputFormat.getC(), outputFormat);
		
		return outputFormats;
	}

	public Map<Character, AbstractFilterFactory<T>> getFilterFactories() {
		final Map<Character, AbstractFilterFactory<T>> abstractPileupFilters = 
				new HashMap<Character, AbstractFilterFactory<T>>();

		final List<AbstractFilterFactory<T>> filterFactories = 
				new ArrayList<AbstractFilterFactory<T>>(10);
		
		final DataGenerator<BaseCallData> dataGenerator = new BaseCallDataGenerator();
		filterFactories.add(new CombinedDistanceFilterFactory<T, BaseCallData>(dataGenerator));
		filterFactories.add(new INDEL_DistanceFilterFactory<T, BaseCallData>(dataGenerator));
		filterFactories.add(new ReadPositionDistanceFilterFactory<T, BaseCallData>(dataGenerator));
		filterFactories.add(new SpliceSiteDistanceFilterFactory<T, BaseCallData>(dataGenerator));
		filterFactories.add(new HomozygousFilterFactory<T>(getParameter()));
		filterFactories.add(new MaxAlleleCountFilterFactory<T>());
		filterFactories.add(new HomopolymerFilterFactory<T, BaseCallData>(dataGenerator));

		for (final AbstractFilterFactory<T> filterFactory : filterFactories) {
			abstractPileupFilters.put(filterFactory.getC(), filterFactory);
		}

		return abstractPileupFilters;
	}

	@Override
	public PileupParameter<T> getParameter() {
		return (PileupParameter<T>) super.getParameter();
	}
	

	@Override
	public boolean parseArgs(String[] args) throws Exception {
		if (args == null || args.length < 1) {
			throw new ParseException("BAM File is not provided!");
		}

		return super.parseArgs(args); 
	}

	@Override
	public List<ParallelDataValidator<T>> getParallelDataValidators() {
		final List<ParallelDataValidator<T>> validators = super.getParallelDataValidators();
		validators.add(new MinCoverageValidator<T>(getParameter().getConditionParameters()));
		return validators;
	}
	
	@Override
	public PileupWorker<T> createWorker(final int threadId) {
		return new PileupWorker<T>(getWorkerDispatcher(), threadId,
				new FileCopyTmpResult<T, DefaultResult<T>>(threadId, 
						(AbstractResultFileWriter<T, DefaultResult<T>>)getParameter().getResultWriter(), getParameter().getResultFormat()),
				getParallelDataValidators(), getParameter());
	}

}

package lib.stat.dirmult;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import lib.io.ResultFormat;
import lib.stat.AbstractStat;
import lib.stat.AbstractStatFactory;
import lib.stat.DirichletFactoryUtil;
import lib.stat.DirichletParameter;

public class DirMultRobustCompoundErrorFactory
extends AbstractStatFactory {

	private final DirichletFactoryUtil facturyUtil;
	
	public DirMultRobustCompoundErrorFactory(final ResultFormat resultFormat) {
		super(Option.builder("DirMult")
				.desc(DirMultCompoundErrorFactory.DESC + "\n"+
						"Adjusts variant condition")
				.build());

		facturyUtil = new DirichletFactoryUtil(resultFormat);
	}

	@Override
	public AbstractStat newInstance(final int conditions) {
		final DirichletParameter dirichletParameter = facturyUtil.getDirichletParameter();
		DirMultSampleProvider dirMultPileupCountProvider;
		switch (conditions) {
		case 1:
			dirMultPileupCountProvider = new InSilicoDirMultPileupCountProvider(
					dirichletParameter.getMinkaEstimateParameter().getMaxIterations(),
					dirichletParameter.getEstimatedError());
			break;
			
		case 2:
			dirMultPileupCountProvider = new RobustDirMultPileupCountProvider(
					dirichletParameter.getMinkaEstimateParameter().getMaxIterations(),
					dirichletParameter.getEstimatedError()); 
			break;

		default:
			throw new IllegalStateException("Number of conditions not supported: " + conditions);
		}
		return new DirMult(this, dirMultPileupCountProvider, dirichletParameter);
	}

	@Override
	public void processCLI(CommandLine cmd) {
		facturyUtil.processCLI(cmd);
	}

	@Override
	protected Options getOptions() {
		return facturyUtil.getOptions();
	}
	
}
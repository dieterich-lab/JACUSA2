package lib.stat.dirmult;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import lib.io.ResultFormat;
import lib.stat.AbstractStatFactory;
import lib.stat.DirichletFactoryUtil;
import lib.stat.DirichletParameter;

public class DirMultCompoundErrorFactory
extends AbstractStatFactory {

	private static final String NAME 	= "DirMultCE";
	public static final String DESC 	= "Compound Error (estimated error {" + DirichletParameter.ESTIMATED_ERROR + "} + phred score)";
	
	private final DirichletFactoryUtil dirichletFactory;
	
	public DirMultCompoundErrorFactory(final ResultFormat resultFormat) {

		super(Option.builder(NAME)
				.desc(DESC)
				.build());
		dirichletFactory = new DirichletFactoryUtil(resultFormat);
	}

	@Override
	public DirMult newInstance(final int conditions) {
		final DirichletParameter dirichletParameter = dirichletFactory.getDirichletParameter();
		DirMultSampleProvider dirMultPileupCountProvider;
		switch (conditions) {
		case 1:
			dirMultPileupCountProvider = new InSilicoDirMultPileupCountProvider(
					dirichletParameter.getMinkaEstimateParameter().getMaxIterations(),
					dirichletParameter.getEstimatedError());
			break;
			
		case 2:
			dirMultPileupCountProvider = new DefaultDirMultPileupCountProvider(
					dirichletParameter.getMinkaEstimateParameter().getMaxIterations(),
					dirichletParameter.getEstimatedError()); 
			break;

		default:
			throw new IllegalStateException("Number of conditions not supported: " + conditions);
		}
		return new DirMult(this, dirMultPileupCountProvider, dirichletParameter);
	}

	@Override
	protected Options getOptions() {
		return dirichletFactory.getOptions();
	}
	
	@Override
	public void processCLI(final CommandLine cmd) {
		dirichletFactory.processCLI(cmd);
	}
	
}
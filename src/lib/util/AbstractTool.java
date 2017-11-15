package lib.util;

import java.io.PrintStream;
import java.util.Map;

import lib.cli.CLI;
import lib.method.AbstractMethodFactory;
import lib.worker.WorkerDispatcher;

public abstract class AbstractTool {

	private final String name;
	private final String version;
	
	private final String[] args;
	
	// command line interface
	private final CLI cli;
	private WorkerDispatcher<?> workerDispatcher;
	
	private static Logger logger;

	protected AbstractTool(final String name, final String version, final String[] args) {
		this.name = name;
		this.version = version;
		this.args = args;

		cli = new CLI(getMethodFactories());
		
		final PrintStream ps = System.err;
		logger = new Logger(ps, this);
		/*
		try {
			//PrintStream ps = new PrintStream(new File("JACAUSA2.log"));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		*/
	}

	public void run() throws Exception {
		// prolog printed in logger

		getLogger().addProlog(getProlog());
		
		// parse CLI
		if (! cli.processArgs(args)) {
			System.exit(1);
		}
		
		// instantiate chosen method
		final AbstractMethodFactory<?> methodFactory = cli.getMethodFactory();
				
		// run the method...
		workerDispatcher = methodFactory.getWorkerDispatcher();
		workerDispatcher.run();

		getLogger().addEpilog(getEpilog());
		
		// TODO close
	}

	protected abstract Map<String, lib.method.AbstractMethodFactory<?>> getMethodFactories();
	protected abstract String getEpilog();

	protected String getProlog() {
		final StringBuilder sb = new StringBuilder();
		final String lineSep = "--------------------------------------------------------------------------------";
		
		sb.append(lineSep);
		sb.append('\n');

		sb.append(name);
		sb.append(" Version: ");
		sb.append(version);
		for(final String arg : args) {
			sb.append(" " + arg);
		}
		sb.append('\n');
		sb.append(lineSep);
		return sb.toString();
	}
	
	public String getName() {
		return name;
	}
	
	public String getVersion() {
		return version;
	}
	
	public static Logger getLogger() {
		return logger;
	}

	public CLI getCLI() {
		return cli;
	}

}

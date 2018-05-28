package lib.util;

import java.io.File;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
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
	private WorkerDispatcher<?, ?> workerDispatcher;
	
	private int comparisons;
	
	private static Logger logger;

	protected AbstractTool(final String name, final String version, final String[] args) {
		this.name = name;
		this.version = version;
		this.args = args;

		cli = new CLI(getMethodFactories());
		
		comparisons = 0;
		
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
		// parse CLI
		if (! cli.processArgs(args)) {
			System.exit(1);
		}

		// prolog printed in logger
		getLogger().addProlog(getProlog());
		
		// instantiate chosen method
		final AbstractMethodFactory<?, ?> methodFactory = cli.getMethodFactory();
				
		// run the method...
		workerDispatcher = methodFactory.getWorkerDispatcher();
		comparisons = workerDispatcher.run();

		getLogger().addEpilog(getEpilog());
	}

	protected abstract Map<String, AbstractMethodFactory<?, ?>> getMethodFactories();
	protected abstract String getEpilog();

	protected String getProlog() {
		final StringBuilder sb = new StringBuilder();
		final String lineSep = "--------------------------------------------------------------------------------";
		
		sb.append(lineSep);
		sb.append('\n');
		sb.append(getCall());
		sb.append('\n');
		sb.append(lineSep);
		return sb.toString();
	}
	
	public String getCall() {
		final StringBuilder sb = new StringBuilder();
		final File file = getFile(); 
		if (file == null) {
			sb.append(name);
		} else {
			sb.append(file.getPath());
		}
		sb.append(" Version: ");
		sb.append(version);
		for(final String arg : args) {
			sb.append(" " + arg);
		}
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

	public int getComparisons() {
		return comparisons;
	}

	protected abstract Class<?> getMainClass();

	public File getFile() {
		File f = null;
		URI u;
		/*
		try {
			u = getMainClass().getProtectionDomain().getCodeSource().getLocation().toURI();
			f = new File(u);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		 */		
		
		return f;
	}
	
}

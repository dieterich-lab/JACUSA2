package cern.jet.stat.quantile;

import hep.aida.bin.DynamicBin1D;
import hep.aida.bin.QuantileBin1D;

import java.text.DecimalFormat;
import java.util.Date;

import cern.jet.random.Uniform;
import cern.jet.random.engine.DRand;

/**
 * A class to test the QuantileBin1D code.
 * The command line is "java Quantile1Test numExamples N"
 * where numExamples is the number of random (Gaussian) numbers to
 * be presented to the QuantileBin1D.add method, and N is
 * the absolute maximum number of examples the QuantileBin1D is setup
 * to receive in the constructor.  N can be set to "L", which will use
 * Long.MAX_VALUE, or to "I", which will use Integer.MAX_VALUE, or to 
 * any positive long value.
 */
public class Quantile1Test {
    public static void main(String[] argv) {
	/*
	 * Get the number of examples from the first argument
	 */
	int numExamples = 0;
	try {
	    numExamples = Integer.parseInt(argv[0]);
	}
	catch (Exception e) {
	    System.err.println("Unable to parse input line count argument");
	    System.err.println(e.getMessage());
	    System.exit(1);
	}
	System.out.println("Got numExamples=" + numExamples);

	/*
	 * Get N from the second argument
	 */
	long N = 0;
	try {
	    if (argv[1].equals("L")) {
		N = Long.MAX_VALUE;
	    } else if (argv[1].equals("I")) {
		N = (long)Integer.MAX_VALUE;
	    } else {
		N = Long.parseLong(argv[1]);
	    }
	}
	catch (Exception e) {
	    System.err.println("Error parsing flag for N");
	    System.err.println(e.getMessage());
	    System.exit(1);
	}
	System.out.println("Got N=" + N);

	/*
	 * Set up the QuantileBin1D object
	 */
	DRand rand = new DRand(new Date());
	QuantileBin1D qAccum = new QuantileBin1D(false,
						 N,
						 1.e-4,
						 1.e-3,
						 200,
						 rand,
						 false,
						 false,
						 2);

	DynamicBin1D dbin = new DynamicBin1D();
	
	/*
	 * Use a new random number generator to generate numExamples
	 * random gaussians, and add them to the QuantileBin1D
	 */
	Uniform dataRand = new Uniform(new DRand(7757));
	for (int i = 1; i <= numExamples; i++) {
		double gauss = dataRand.nextDouble();
	    qAccum.add(gauss);
	    dbin.add(gauss);
	}
	
	/*
	 * print out the percentiles
	 */
	DecimalFormat fmt = new DecimalFormat("0.00");
	System.out.println();
	//int step = 1;
	int step = 10;
	for (int i = 1; i < 100; ) {
	    double percent = ((double)i) * 0.01;
	    double quantile = qAccum.quantile(percent);
	    System.out.println(fmt.format(percent) + "  " + quantile + ",  " + dbin.quantile(percent)+ ",  " + (dbin.quantile(percent)-quantile));
	    i = i + step;
	}
}
}




/*
Copyright � 1999 CERN - European Organization for Nuclear Research.
Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
is hereby granted without fee, provided that the above copyright notice appear in all copies and 
that both that copyright notice and this permission notice appear in supporting documentation. 
CERN makes no representations about the suitability of this software for any purpose. 
It is provided "as is" without expressed or implied warranty.
*/
package cern.colt.matrix.linalg;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import EDU.oswego.cs.dl.util.concurrent.FJTask;
/**
Parallel implementation of the Basic Linear Algebra System for symmetric multi processing boxes.
Currently only a few algorithms are parallelised; the others are fully functional, but run in sequential mode.
Parallelised are:
<ul>
<li>{@link #dgemm dgemm} (matrix-matrix multiplication)</li>
<li>{@link #dgemv dgemv} (matrix-vector multiplication)</li>
<li>{@link #assign(DoubleMatrix2D,cern.colt.function.DoubleFunction) assign(A,function)} (generalized matrix scaling/transform): Strong speedup only for expensive functions like logarithm, sin, etc.</li>
<li>{@link #assign(DoubleMatrix2D,DoubleMatrix2D,cern.colt.function.DoubleDoubleFunction) assign(A,B,function)} (generalized matrix scaling/transform): Strong speedup only for expensive functions like pow etc.</li>
</ul>
In all cases, no or only marginal speedup is seen for small problem sizes; they are detected and the sequential algorithm is used.

<h4>Usage</h4>
Call the static method {@link #allocateBlas} at the very beginning of your program, supplying the main parameter for SmpBlas, the number of available CPUs.
The method sets the public global variable <tt>SmpBlas.smpBlas</tt> to a blas using a maximum of <tt>CPUs</tt> threads, each concurrently processing matrix blocks with the given sequential blas algorithms.
Normally there is no need to call <tt>allocateBlas</tt> more than once.
Then use <tt>SmpBlas.smpBlas.someRoutine(...)</tt> to run <tt>someRoutine</tt> in parallel.
E.g.
<table>
<td class="PRE"> 
<pre>
int cpu_s = 4;
SmpBlas.allocateBlas(cpu_s, SeqBlas.seqBlas);
...
SmpBlas.smpBlas.dgemm(...)
SmpBlas.smpBlas.dgemv(...)
</pre>
</td>
</table>
Even if you don't call a blas routine yourself, it often makes sense to allocate a SmpBlas, because other matrix library routines sometimes call the blas.
So if you're lucky, you get parallel performance for free.
<h4>Notes</h4>
<ul>
<li>Unfortunately, there is no portable means of automatically detecting the
number of CPUs on a JVM, so there is no good way to automate defaults.</li>
<li>Only improves performance on boxes with > 1 CPUs and VMs with <b>native threads</b>.</li>
<li>Currently only improves performance when working on dense matrix types. On sparse types, performance is likely to degrade (because of the implementation of sub-range views)!</li>
<li>Implemented using Doug Lea's fast lightweight task framework ({@link EDU.oswego.cs.dl.util.concurrent}) built upon Java threads, and geared for parallel computation.</li>
</ul>

@see EDU.oswego.cs.dl.util.concurrent.FJTaskRunnerGroup
@see EDU.oswego.cs.dl.util.concurrent.FJTask
@author wolfgang.hoschek@cern.ch
@version 0.9, 16/04/2000
*/
public class SmpBlas implements Blas {
	/**
	* The public global parallel blas; initialized via {@link #allocateBlas}.
	* Do not modify this variable via other means (it is public).
	*/
	public static Blas smpBlas = SeqBlas.seqBlas;
		
	protected Blas seqBlas; // blocks are operated on in parallel; for each block this seq algo is used.

	protected Smp smp;
		
	protected int maxThreads;

	protected static int NN_THRESHOLD = 30000;
/**
Constructs a blas using a maximum of <tt>maxThreads<tt> threads; each executing the given sequential algos.
*/
protected SmpBlas(int maxThreads, Blas seqBlas) {
	this.seqBlas = seqBlas;
	this.maxThreads = maxThreads;
	this.smp = new Smp(maxThreads);
	//Smp.smp = new Smp(maxThreads);
}
/**
Sets the public global variable <tt>SmpBlas.smpBlas</tt> to a blas using a maximum of <tt>maxThreads</tt> threads, each executing the given sequential algorithm; <tt>maxThreads</tt> is normally the number of CPUs.
Call this method at the very beginning of your program. 
Normally there is no need to call this method more than once.
@param maxThreads the maximum number of threads (= CPUs) to be used
@param seqBlas the sequential blas algorithms to be used on concurrently processed matrix blocks.
*/
public static void allocateBlas(int maxThreads, Blas seqBlas) {
	if (smpBlas instanceof SmpBlas) { // no need to change anything?
		SmpBlas s = (SmpBlas) smpBlas;
		if (s.maxThreads == maxThreads && s.seqBlas == seqBlas) return;
	}

	if (maxThreads<=1) 
		smpBlas = seqBlas;
	else {
		smpBlas = new SmpBlas(maxThreads, seqBlas);
	}
}
public void assign(DoubleMatrix2D A, final cern.colt.function.DoubleFunction function) {
	run(A,false,
		new Matrix2DMatrix2DFunction() {
			public double apply(DoubleMatrix2D AA, DoubleMatrix2D BB) {
				seqBlas.assign(AA,function);
				return 0;
			}
		}
	);
}
public void assign(DoubleMatrix2D A, DoubleMatrix2D B, final cern.colt.function.DoubleDoubleFunction function) {
	run(A,B,false,
		new Matrix2DMatrix2DFunction() {
			public double apply(DoubleMatrix2D AA, DoubleMatrix2D BB) {
				seqBlas.assign(AA,BB,function);
				return 0;
			}
		}
	);
}
public double dasum(DoubleMatrix1D x) {
	return seqBlas.dasum(x);
}
public void daxpy(double alpha, DoubleMatrix1D x, DoubleMatrix1D y) {
	seqBlas.daxpy(alpha,x,y);
}
public void daxpy(double alpha, DoubleMatrix2D A, DoubleMatrix2D B) {
	seqBlas.daxpy(alpha,A,B);
}
public void dcopy(DoubleMatrix1D x, DoubleMatrix1D y) {
	seqBlas.dcopy(x,y);
}
public void dcopy(DoubleMatrix2D A, DoubleMatrix2D B) {
	seqBlas.dcopy(A, B);
}
public double ddot(DoubleMatrix1D x, DoubleMatrix1D y) {
	return seqBlas.ddot(x,y);
}
public void dgemm(final boolean transposeA, final boolean transposeB, final double alpha, final DoubleMatrix2D A, final DoubleMatrix2D B, final double beta, final DoubleMatrix2D C) {
	/*
	determine how to split and parallelize best into blocks
	if more B.columns than tasks --> split B.columns, as follows:
	
			xx|xx|xxx B
			xx|xx|xxx
			xx|xx|xxx
	A
	xxx     xx|xx|xxx C 
	xxx		xx|xx|xxx
	xxx		xx|xx|xxx
	xxx		xx|xx|xxx
	xxx		xx|xx|xxx

	if less B.columns than tasks --> split A.rows, as follows:
	
			xxxxxxx B
			xxxxxxx
			xxxxxxx
	A
	xxx     xxxxxxx C
	xxx     xxxxxxx
	---     -------
	xxx     xxxxxxx
	xxx     xxxxxxx
	---     -------
	xxx     xxxxxxx

	*/
	if (transposeA) {
		dgemm(false, transposeB, alpha, A.viewDice(), B, beta, C);
		return;
	}
	if (transposeB) {
		dgemm(transposeA, false, alpha, A, B.viewDice(), beta, C);
		return;
	}
	int m = A.rows();
	int n = A.columns();
	int p = B.columns();

	if (B.rows() != n)
		throw new IllegalArgumentException("Matrix2D inner dimensions must agree:"+A.toStringShort()+", "+B.toStringShort());
	if (C.rows() != m || C.columns() != p)
		throw new IllegalArgumentException("Incompatibel result matrix: "+A.toStringShort()+", "+B.toStringShort()+", "+C.toStringShort());
	if (A == C || B == C)
		throw new IllegalArgumentException("Matrices must not be identical");

	long flops = 2L*m*n*p;
	int noOfTasks = (int) Math.min(flops / 30000, this.maxThreads); // each thread should process at least 30000 flops
	boolean splitB = (p >= noOfTasks);
	int width = splitB ? p : m;
	noOfTasks = Math.min(width,noOfTasks);
	
	if (noOfTasks < 2) { // parallelization doesn't pay off (too much start up overhead)
		seqBlas.dgemm(transposeA, transposeB, alpha, A, B, beta, C);
		return;
	}
	
	// set up concurrent tasks
	int span = width/noOfTasks;
	final FJTask[] subTasks = new FJTask[noOfTasks];
	for (int i=0; i<noOfTasks; i++) {
		final int offset = i*span;
		if (i==noOfTasks-1) span = width - span*i; // last span may be a bit larger

		final DoubleMatrix2D AA,BB,CC; 
		if (splitB) { 
			// split B along columns into blocks
			AA = A;
			BB = B.viewPart(0,offset,n,span);
			CC = C.viewPart(0,offset,m,span);
		}
		else { 
			// split A along rows into blocks
			AA = A.viewPart(offset,0,span,n);
			BB = B;
			CC = C.viewPart(offset,0,span,p);
		}
				
		subTasks[i] = new FJTask() { 
			public void run() { 
				seqBlas.dgemm(transposeA,transposeB,alpha,AA,BB,beta,CC); 
				//System.out.println("Hello "+offset); 
			}
		};
	}
	
	// run tasks and wait for completion
	try { 
		this.smp.taskGroup.invoke(
			new FJTask() {
				public void run() {	
					coInvoke(subTasks);	
				}
			}
		);
	} catch (InterruptedException exc) {}
}
public void dgemv(final boolean transposeA, final double alpha, DoubleMatrix2D A, final DoubleMatrix1D x, final double beta, DoubleMatrix1D y) {
	/*
	split A, as follows:
	
			x x
			x
			x
	A
	xxx     x y
	xxx     x
	---     -
	xxx     x
	xxx     x
	---     -
	xxx     x

	*/
	if (transposeA) {
		dgemv(false, alpha, A.viewDice(), x, beta, y);
		return;
	}
	int m = A.rows();
	int n = A.columns();
	long flops = 2L*m*n;
	int noOfTasks = (int) Math.min(flops / 30000, this.maxThreads); // each thread should process at least 30000 flops
	int width = A.rows();
	noOfTasks = Math.min(width,noOfTasks);
	
	if (noOfTasks < 2) { // parallelization doesn't pay off (too much start up overhead)
		seqBlas.dgemv(transposeA, alpha, A, x, beta, y);
		return;
	}
	
	// set up concurrent tasks
	int span = width/noOfTasks;
	final FJTask[] subTasks = new FJTask[noOfTasks];
	for (int i=0; i<noOfTasks; i++) {
		final int offset = i*span;
		if (i==noOfTasks-1) span = width - span*i; // last span may be a bit larger

		// split A along rows into blocks
		final DoubleMatrix2D AA = A.viewPart(offset,0,span,n);
		final DoubleMatrix1D yy = y.viewPart(offset,span);
				
		subTasks[i] = new FJTask() { 
			public void run() { 
				seqBlas.dgemv(transposeA,alpha,AA,x,beta,yy); 
				//System.out.println("Hello "+offset); 
			}
		};
	}
	
	// run tasks and wait for completion
	try { 
		this.smp.taskGroup.invoke(
			new FJTask() {
				public void run() {	
					coInvoke(subTasks);	
				}
			}
		);
	} catch (InterruptedException exc) {}
}
public void dger(double alpha, DoubleMatrix1D x, DoubleMatrix1D y, DoubleMatrix2D A) {
	seqBlas.dger(alpha,x,y,A);
}
public double dnrm2(DoubleMatrix1D x) {
	return seqBlas.dnrm2(x);
}
public void drot(DoubleMatrix1D x, DoubleMatrix1D y, double c, double s) {
	seqBlas.drot(x,y,c,s);
}
public void drotg(double a, double b, double rotvec[]) {
	seqBlas.drotg(a,b,rotvec);
}
public void dscal(double alpha, DoubleMatrix1D x) {
	seqBlas.dscal(alpha,x);
}
public void dscal(double alpha, DoubleMatrix2D A) {
	seqBlas.dscal(alpha, A);
}
public void dswap(DoubleMatrix1D x, DoubleMatrix1D y) {
	seqBlas.dswap(x,y);
}
public void dswap(DoubleMatrix2D A, DoubleMatrix2D B) {
	seqBlas.dswap(A,B);
}
public void dsymv(boolean isUpperTriangular, double alpha, DoubleMatrix2D A, DoubleMatrix1D x, double beta, DoubleMatrix1D y) {
	seqBlas.dsymv(isUpperTriangular, alpha, A, x, beta, y);
}
public void dtrmv(boolean isUpperTriangular, boolean transposeA, boolean isUnitTriangular, DoubleMatrix2D A, DoubleMatrix1D x) {
	seqBlas.dtrmv(isUpperTriangular, transposeA, isUnitTriangular, A, x);
}
public int idamax(DoubleMatrix1D x) {
	return seqBlas.idamax(x);
}
protected double[] run(DoubleMatrix2D A, DoubleMatrix2D B, boolean collectResults, Matrix2DMatrix2DFunction fun) {
	DoubleMatrix2D[][] blocks;
	blocks = this.smp.splitBlockedNN(A, B, NN_THRESHOLD, A.rows()*A.columns());
	//blocks = this.smp.splitStridedNN(A, B, NN_THRESHOLD, A.rows()*A.columns());
	int b = blocks!=null ? blocks[0].length : 1;
	double[] results = collectResults ? new double[b] : null;
	
	if (blocks==null) {  // too small --> sequential
		double result = fun.apply(A,B);
		if (collectResults) results[0] = result;
		return results;
	}
	else {  // parallel
		this.smp.run(blocks[0],blocks[1],results,fun);
	}
	return results;
}
protected double[] run(DoubleMatrix2D A, boolean collectResults, Matrix2DMatrix2DFunction fun) {
	DoubleMatrix2D[] blocks;
	blocks = this.smp.splitBlockedNN(A, NN_THRESHOLD, A.rows()*A.columns());
	//blocks = this.smp.splitStridedNN(A, NN_THRESHOLD, A.rows()*A.columns());
	int b = blocks!=null ? blocks.length : 1;
	double[] results = collectResults ? new double[b] : null;
	
	if (blocks==null) { // too small -> sequential
		double result = fun.apply(A,null);
		if (collectResults) results[0] = result;
		return results;
	}
	else { // parallel
		this.smp.run(blocks,null,results,fun);
	}
	return results;
}
/**
 * Prints various snapshot statistics to System.out; Simply delegates to {@link EDU.oswego.cs.dl.util.concurrent.FJTaskRunnerGroup#stats}.
 */
public void stats() {
	if (this.smp!=null) this.smp.stats();
}
private double xsum(DoubleMatrix2D A) {
	double[] sums = run(A,true,
		new Matrix2DMatrix2DFunction() {
			public double apply(DoubleMatrix2D AA, DoubleMatrix2D BB) {
				return AA.zSum();
			}
		}
	);

	double sum = 0;
	for (int i = sums.length; --i >= 0; ) sum += sums[i];
	return sum;
}
}

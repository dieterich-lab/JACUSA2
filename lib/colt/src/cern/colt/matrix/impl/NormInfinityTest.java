package cern.colt.matrix.impl;

import cern.colt.matrix.*;
import cern.colt.matrix.linalg.Algebra;

class NormInfinityTest {

	public static void main(String[] args) {
		DoubleMatrix1D x1 = DoubleFactory1D.dense
				.make(new double[] { 1.0, 2.0});
		DoubleMatrix1D x2 = DoubleFactory1D.dense
				.make(new double[] { 1.0, -2.0});
		DoubleMatrix1D x3 = DoubleFactory1D.dense.make(new double[] { -1.0, -2.0});

		System.out.println(Algebra.DEFAULT.normInfinity(x1));
		System.out.println(Algebra.DEFAULT.normInfinity(x2));
		System.out.println(Algebra.DEFAULT.normInfinity(x3));
	}
}
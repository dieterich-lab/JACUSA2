package test.lib.data.storage.arrest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import lib.data.storage.arrest.LocationInterpreter;
import lib.data.storage.arrest.RF_FIRSTSTRAND_LocationInterpreter;
import lib.util.LibraryType;

class RF_FIRSTSTRAND_LocationInterpreterTest implements LocationInterpreterTest {

	private static final String CONTIG = "LocationInterpreterTest";
	
	private final LibraryType libType;
	
	public RF_FIRSTSTRAND_LocationInterpreterTest() {
		libType = LibraryType.RF_FIRSTSTRAND;
	}
	
	@Override
	public Stream<Arguments> testGetThroughPositionProvider() {
		final List<Arguments> args = new ArrayList<>();

		//123456789012 ref. pos.
		//   01234     win. pos
		//ACGAACGTACGT ref. seq
		//   [   ]     window
		//    !==      read
		//    012      read pos.
		args.add(cThroughSE(
				4,  8,
				5, true, "3M",
				new String[] { "6,1,2", "7,2,3"} ));
		//    ==!
		//    012
		args.add(cThroughSE(
				4,  8,
				5, false, "3M",
				new String[] { "5,0,1", "6,1,2"} ));
		
		//123456789012
		//   01234
		//ACGAACGTACGT
		//   [   ]
		//   != == 
		//   01 01
		args.add(cThroughPE(
				4,  8,
				7, true, "2M",
				4, false, "2M",
				new String[] { "5,1,1" } ));
		//   == =! 
		//   01 01
		args.add(cThroughPE(
				4,  8,
				4, false, "2M",
				7, true, "2M",
				new String[] { "4,0,0", "5,1,1" } ));
		
		return args.stream();
	}

	@Override
	public Stream<Arguments> testGetArrestPosition() {
		final List<Arguments> args = new ArrayList<>(); 

		//123456789012 ref. pos.
		//   01234     win. pos
		//ACGAACGTACGT ref. seq
		//   [   ]     window
		//    !==      read
		//    012      read pos.
		args.add(cArrestSE(
				4,  8,
				5, true, "3M",
				5, 0, 1));

		//123456789012
		//   01234
		//ACGAACGTACGT
		//   [   ]
		//      !==
		//      012
		args.add(cArrestSE(
				4,  8,
				7, true, "3M",
				7, 0, 3));

		//123456789012
		//   01234
		//ACGAACGTACGT
		//   [   ]
		//   != == 
		//   01 01
		args.add(cArrestPE(
				4,  8,
				7, true, "2M",
				4, false, "2M",
				4, 0, 0));
		
		//123456789012
		//   01234
		//ACGAACGTACGT
		//   [   ]
		//   == =! 
		//   01 01
		args.add(cArrestPE(
				4,  8,
				4, false, "2M",
				7, true, "2M",
				8, -1, 4));
		
		return args.stream();
	}
	
	@Override
	public String getContig() {
		return CONTIG;
	}
		
	@Override
	public LibraryType getLibraryType() {
		return libType;
	}
	
	@Override
	public LocationInterpreter createTestInstance() {
		return new RF_FIRSTSTRAND_LocationInterpreter();
	}

}

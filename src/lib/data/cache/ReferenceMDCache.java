package lib.data.cache;

import java.util.Arrays;

import lib.method.AbstractMethodFactory;
import lib.util.Coordinate;

import htsjdk.samtools.util.SequenceUtil;

import lib.data.AbstractData;
import lib.data.builder.SAMRecordWrapper;
import lib.data.has.hasReferenceBase;

public class ReferenceMDCache<T extends AbstractData & hasReferenceBase> 
extends AbstractCache<T> {

	private byte[] referenceBases;

	public ReferenceMDCache(final AbstractMethodFactory<T> methodFactory) {
		super(methodFactory);

		referenceBases = new byte[getActiveWindowSize()];
	}

	@Override
	public void addRecordWrapper(final SAMRecordWrapper recordWrapper) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void addRecordWrapperPosition(final int readPosition, final SAMRecordWrapper recordWrapper) {
		// TODO
	}
	
	@Override
	public void addRecordWrapperRegion(final int readPosition, final int length, final SAMRecordWrapper recordWrapper) {
		// TODO Auto-generated method stub
	}

	@Override
	public T getData(final Coordinate coordinate) {
		final T data = getDataGenerator().createData();
		final int windowPosition = Coordinate.makeRelativePosition(getActiveWindowCoordinate(), coordinate.getPosition());
		data.setReferenceBase(referenceBases[windowPosition]);
		return data;
	}

	/* TODO
	private byte[] parseMDField(final SAMRecord record) {
		if (! record.hasAttribute(SAMTag.MD.name())) {
			return new byte[0]; // no MD field :-(
		}
		// potential missing number(s)
		final String MD = "0" + record.getStringAttribute(SAMTag.MD.name()).toUpperCase();
		
		// init container size with read length
		final byte[] referenceBases = new byte[record.getReadLength()];
		int destPos = 0;
		// copy read sequence to reference container / concatenate mapped segments ignore DELs
		for (int i = 0; i < record.getAlignmentBlocks().size(); i++) {
			if (referenceBases != null) {
				final int srcPos = record.getAlignmentBlocks().get(i).getReadStart() - 1;
				final int length = record.getAlignmentBlocks().get(i).getLength();
				System.arraycopy(
						record.getReadBases(), 
						srcPos, 
						referenceBases, 
						destPos, 
						length);
				destPos += length;
			}
		}

		int position = 0;
		boolean nextInteger = true;
		// change to reference base based on MD string
//		int j = 0;
		for (String e : MD.split("((?<=[0-9]+)(?=[^0-9]+))|((?<=[^0-9]+)(?=[0-9]+))")) {
			if (nextInteger) { // match
				// use read sequence
				int matchLength = Integer.parseInt(e);
				position += matchLength;
				nextInteger = false;	
			} else if (e.charAt(0) == '^') {
				// ignore deletions from reference
				nextInteger = true;
			} else { // mismatch
//				try {
				referenceBases[position] = (byte)e.toCharArray()[0];
//				} catch (ArrayIndexOutOfBoundsException e2) {
//					String[] tmp = MD.split("((?<=[0-9]+)(?=[^0-9]+))|((?<=[^0-9]+)(?=[0-9]+))");
//					System.out.println(e2.toString());
//				}

				position += 1;
				nextInteger = true;
			}
//			++j;
		}
		// resize container if MD < read length
		if (position < referenceBases.length) {
			Arrays.copyOf(referenceBases, position);
		}

		return referenceBases;
	}
	*/

	@Override
	public void clear() {
		Arrays.fill(referenceBases, SequenceUtil.N);
	}

}

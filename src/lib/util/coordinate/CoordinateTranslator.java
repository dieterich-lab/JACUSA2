package lib.util.coordinate;

public interface CoordinateTranslator {

	int getRefPosStart();
	int getRefPosEnd();

	int getLength();

	int convert2windowPosition(int refPos);
	int convert2windowPosition(Coordinate coordinate);
	int convert2referencePosition(int winPos);

}
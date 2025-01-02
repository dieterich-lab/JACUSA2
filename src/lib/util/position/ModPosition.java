package lib.util.position;

import lib.record.Record;
import lib.util.coordinate.CoordinateTranslator;


class ModPosition extends AbstractPosition {

    ModPosition(final ModPosition modPosition) {
        super(modPosition);
    }

    ModPosition(
            final int refPos, final int readPos, final int winPos,
            final Record record) {

        super(refPos, readPos, winPos, record);
    }

    private ModPosition(Builder builder) {
        super(builder);
    }

    @Override
    void increment() {
        refPos++;
        winPos++;
    }

    @Override
    void offset(int offset) {
        refPos 	+= offset;
        winPos	+= offset;
    }

    @Override
    public ModPosition copy() {
        return new ModPosition(this);
    }

    @Override
    public boolean isValidRefPos() {
        return true;
    }

    public static class Builder extends AbstractBuilder<ModPosition> {

        public Builder(
                final int modIndex, final Record record,
                final CoordinateTranslator translator) {

                super(
                        record.getSAMRecord().getReferencePositionAtReadPosition(modIndex),
                        modIndex,
                        translator.ref2winPos(record.getSAMRecord().getReferencePositionAtReadPosition(modIndex)),
                        record
                );
        }

        @Override
        public ModPosition build() {
            return new ModPosition(this);
        }

    }

}

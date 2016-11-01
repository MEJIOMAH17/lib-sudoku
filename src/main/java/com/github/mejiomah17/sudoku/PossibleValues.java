package com.github.mejiomah17.sudoku;

import java.util.Arrays;

/**
 * This class class keep possible values of cell
 */
public class PossibleValues {

    private boolean values[];
    private byte amount; //amount how many values is  possible

    /**
     *
     * @param eachValuePossible
     * true set all values a possible.
     * false, set all values a impossible
     */
    public PossibleValues(boolean eachValuePossible,byte gridSize) {
        values = new boolean[gridSize];
        Arrays.fill(values, eachValuePossible);
        if (eachValuePossible) amount = (byte) values.length;
        else amount = 0;
    }

    /**
     * @param bool in array [0]=true means 1 is possible [8]=false means 8 is impossible
     */
    public PossibleValues(boolean[] bool) {
        values = bool;
        for (boolean tmp : bool) {
            if (tmp) amount++;
        }
    }

    public PossibleValues(PossibleValues pv) {
        values = new boolean[pv.getSize()];
        System.arraycopy(pv.getValues(), 0, values, 0, pv.getSize());
        amount = pv.getAmount();
    }

    /**
     * @return true if value possible, false if impossible
     */
    public boolean isPossible(int possibleValue) {
        return values[possibleValue - 1];
    }


    /**
     * Set value as impossible
     *
     * @param impossibleValue
     */
    public void setImpossible(int impossibleValue) {
        if (values[impossibleValue - 1]) {
            values[impossibleValue - 1] = false;
            amount--;
        }
    }

    /**
     * @return the amount of possible values
     */
    public byte getSize() {
        return (byte)values.length;
    }

    protected boolean[] getValues() {
        return values;
    }

    public byte getAmount() {
        return amount;
    }

    /**
     * @return first possible value, or 0 if all values is impossible
     */
    public int getPossibleValue() {
        for (int i = 0; i < values.length; i++) {
            if (values[i]) return i + 1;
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PossibleValues)) return false;

        PossibleValues that = (PossibleValues) o;

        return Arrays.equals(getValues(), that.getValues());

    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(getValues());
    }

}

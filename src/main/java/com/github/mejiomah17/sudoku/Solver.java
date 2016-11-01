package com.github.mejiomah17.sudoku;

import java.util.Random;
import java.util.Stack;

import static java.lang.Thread.sleep;

/**
 * Solve sudoku, with HumanSolver. If HumanSolver can't solve grid, use depth-first search
 */
public class Solver {
    private Grid grid;
    private Stack<Assumption> assumptions = new Stack<>();

    public Solver(Grid grid) {
        if (grid.isCorrect()) this.grid = grid;
        else throw new IllegalArgumentException("Grid isn't correct");

    }

    private Grid makeAssumption(Grid grid) {
        //This magic for quick generate grid
        Random random = new Random();
        Grid tmpGrid = new Grid(grid);
        Cell tmpCell;
        for (int i = 0; i < grid.getSize() * grid.getSize(); i++) {
            CellAddress address = new CellAddress(Math.abs(random.nextInt() % grid.getSize()), Math.abs(random.nextInt() % grid.getSize()));
            if (!tmpGrid.getCell(address).isEmpty()) continue;
            tmpCell = tmpGrid.getCell(address);
            if (tmpCell.getPossibleValues().getAmount() > 0) {
                tmpCell.setValue(tmpCell.getPossibleValues().getPossibleValue());
                assumptions.push(new Assumption(address, tmpCell.getValue()));
                return tmpGrid;
            }


        }
        //////////////////////////////////////////////////////////
        // if random doesn't work,  need to check that all possible options are checked
        breakLabel:
        for (int i = 0; i < grid.getSize(); i++) {
            for (int j = 0; j < grid.getSize(); j++) {
                if (!grid.getCell(i, j).isEmpty()) continue;
                tmpGrid = new Grid(grid);
                tmpCell = tmpGrid.getCell(i, j);
                if (tmpCell.getPossibleValues().getAmount() > 0) {
                    tmpCell.setValue(tmpCell.getPossibleValues().getPossibleValue());
                    assumptions.push(new Assumption(i, j, tmpCell.getValue()));
                    break breakLabel;
                }


            }
        }
        return tmpGrid;


    }

    public void solve() {
        //trying to solve the grid for Constants.searchTime, if not work starts up again
        Thread thread;
        solveLabel:
        while (!grid.isSolved()) {
            thread = new Thread(new SolveRunnable());
            thread.start();
            for (int i = 0; i < Constants.searchTime; i += 50) {
                if (grid.isSolved()) break solveLabel;
                try {
                    sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            thread.stop();     //If thread hung kill him
        }

//        System.out.println(grid);

    }

    public Grid getGrid() {
        return grid;
    }

    /**
     * Created by mark on 01.09.16.
     */
    static class Assumption {
        private CellAddress address;
        private Integer value;

        public Assumption(CellAddress address, Integer value) {
            this.address = address;
            this.value = value;
        }

        public Assumption(int rowIndex, int columnIndex, Integer value) {
            this(new CellAddress(rowIndex, columnIndex), value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Assumption assumption = (Assumption) o;

            return (value.equals(assumption.value) && address.equals(assumption.address));

        }

        @Override
        public int hashCode() {
            int result = address != null ? address.hashCode() : 0;
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
        }


    }

    private class SolveRunnable implements Runnable {

        @Override
        public void run() {
            // use Depth-first search
            Stack<Grid> grids = new Stack<>();
            grids.push(grid);
            HumanSolver humanSolver;
            Grid tmpGrid;
            Grid tmpGrid2;
            while (!grids.peek().isSolved()) {
                System.out.println(grids.peek());
                humanSolver = new HumanSolver(grids.peek());
                humanSolver.solve();
                tmpGrid = new Grid(humanSolver.getGrid());
                if (tmpGrid.isSolved()) {
                    grid = tmpGrid;
                    return;
                }
                tmpGrid2 = makeAssumption(tmpGrid);
                if (tmpGrid.equals(tmpGrid2)) {//if can't make assumption
                    // go back and make another assumption
                    grids.pop();
                    grids.peek().getCell(assumptions.peek().address).setValueImpossible(assumptions.peek().value); // indicates that the assumption was wrong
                    assumptions.pop();
                    continue;
                } else grids.push(tmpGrid2);
            }
            grid = grids.peek();
        }
    }
}


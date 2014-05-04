package com.company;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kwamina
 * Date: 4/15/14
 * Time: 12:31 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Data class to store and import data. Contains a grid that represents a label for each coordinate depending on whether
 * the coordinate is inside an atom, too near an atom to fit a probe sphere, or completely separate and possibly a
 * cavity.
 */
public class Data {
    private double resolution;
    private CoordinateLabel[][][] grid;
    private HashSet< Atom> atoms;
//    private double[] bounds; //Array that contains the coordinates of the "bounding box" around the protein
    private Bounds bounds;
    public Data(String filename, double resolution) {
        this.resolution = resolution;
        try {
            atoms = importAtoms(filename);
        }
        catch(FileNotFoundException e) {
            System.err.printf("Error parsing file");
            e.printStackTrace();
        }
        bounds = proteinBounds(atoms);
        int xwidth = (int)Math.ceil((bounds.maxX - bounds.minX)/resolution);
        int ywidth = (int)Math.ceil((bounds.maxY - bounds.minY)/resolution);
        int zwidth = (int)Math.ceil((bounds.maxZ - bounds.minZ)/resolution);
        grid = new CoordinateLabel[xwidth][ywidth][zwidth];
//        System.out.println("this");
    }

    /*finds the bounds of the box surround the protein and denoting the outside. it does this by iterating through atoms
     **and finding the mximum and minimum of each coordinate
    */
    private Bounds proteinBounds(HashSet<Atom> atoms) {
        Iterator<Atom> it = atoms.iterator();
        double xmin, ymin, zmin, xmax, ymax, zmax;
        xmin = ymin = zmin = xmax = ymax = zmax = 0;
        boolean first = true;
        Bounds bounds = new Bounds();
        while(it.hasNext()) {
            Atom currentAtom = it.next();
            if (first) {
                first = false;
                xmin = currentAtom.myX;
                xmax = currentAtom.myX;
                ymin = currentAtom.myY;
                ymax = currentAtom.myY;
                zmin = currentAtom.myZ;
                zmax = currentAtom.myZ;
            }
            else {
                xmin = currentAtom.myX < xmin ? currentAtom.myX : xmin;
                xmax = currentAtom.myX > xmax ? currentAtom.myX : xmax;
                ymin = currentAtom.myX < ymin ? currentAtom.myY : ymin;
                ymax = currentAtom.myX > ymax ? currentAtom.myY : ymax;
                zmin = currentAtom.myX < zmin ? currentAtom.myZ : zmin;
                zmax = currentAtom.myX > zmax ? currentAtom.myZ : zmax;
            }
        }
//        double[] ret =   {xmin, xmax, ymin, ymax, zmin, zmax};
//        return ret;
        bounds.minX = xmin;
        bounds.minY = ymin;
        bounds.minZ = zmin;
        bounds.maxX = xmax;
        bounds.maxY = ymax;
        bounds.maxZ = zmax;
        return bounds;
    }

    /*Uses scanner to read the input file and add the atom after parsing the line*/
    private HashSet<Atom> importAtoms(String filename) throws FileNotFoundException {
        HashSet<Atom> newSet = new HashSet<Atom>();
        Scanner infile = new Scanner(new FileInputStream(filename));
        while(infile.hasNextLine()) {
            String inputLine = infile.nextLine();
            newSet.add(parseAtomFromLine(inputLine));
        }
        return newSet;
    }

    /*Parses each line from the input and returns a new atom based on the details contained in the line*/
    private Atom parseAtomFromLine(String line) {
        String[] arr = line.split("\\s+");
        return new Atom(Double.parseDouble(arr[9]), Double.parseDouble(arr[5]), Double.parseDouble(arr[6]), Double.parseDouble(arr[7]));
    }

    /*returns the length of the grid in the x dimension*/
    public int xLength() {
        return grid.length;
    }

    /*returns the length of the grid in the y dimension*/
    public int yLength() {
            return grid[0].length;
    }
    /*returns the length of the grid in the z dimension*/
    public int zLength() {
        return grid[0][0].length;
    }

    /**
     * Returns the list of all atoms imported.
     * @return
     */
    public ArrayList<Atom> getAtoms() {
        ArrayList<Atom> ret = new ArrayList<Atom>();
        Iterator<Atom> it = atoms.iterator();
        while(it.hasNext()) {
            ret.add(it.next());
        }
        return ret;
    }

    /**
     * Returns the coordinate value corresponding to the given x index
     * @param indexValue x index
     * @return coordinate value
     */
    public double xIndexToCoordinate(int indexValue) {
        return bounds.minX + resolution * indexValue;
    }

    /**
     * Returns the coordinate value corresponding to the given y index
     * @param indexValue y index
     * @return coordinate value
     */
    public double yIndexToCoordinate(int indexValue) {
        return bounds.minY + resolution * indexValue;
    }

    /**
     * Returns the coordinate value corresponding to the given z index
     * @param indexValue z index
     * @return coordinate value
     */
    public double zIndexToCoordinate(int indexValue) {
        return bounds.minZ + resolution * indexValue;
    }

    /**
     *
     * Gets the label at a given location in the grid. Contains bounds checking.
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     * @return CoordinateLabel value of the location in the grid.
     */
    public CoordinateLabel getLabel(int x, int y, int z) {
        if (x >= grid.length || x < 0 || y >= grid[0].length || y < 0 || z >= grid[0][0].length || z < 0) {
            return null;
        }
        return grid[x][y][z];
    }

    /**
     * Sets the label at a given location in the grid to a given value. Contains bounds checking.
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     * @param newVal new value for this location
     * @return true if possible, false otherwise (i.e. if location was outside of grid)
     */
    public boolean setLabel(int x, int y, int z, CoordinateLabel newVal) {
        if (x >= grid.length || x < 0 || y >= grid[0].length || y < 0 || z >= grid[0][0].length || z < 0) {
            return false;
        }
        grid[x][y][z] = newVal;
        return true;
    }

    /**
     * For a given x coordinate value, returns the index that corresponds to its location in the grid.
     * @param coordinateValue x-coordinate
     * @return x index in grid
     */
    public int getXIndex(double coordinateValue) {
        return (int)((coordinateValue - bounds.minX) / resolution);
    }

    /**
     * For a given y coordinate value, returns the index that corresponds to its location in the grid.
     * @param coordinateValue y-coordinate
     * @return y index in grid
     */
    public int getYIndex(double coordinateValue) {
        return (int)((coordinateValue - bounds.minY) / resolution);
    }

    /**
     * For a given Z coordinate value, returns the index that corresponds to its location in the grid.
     * @param coordinateValue z-coordinate
     * @return z index in grid
     */
    public int getZIndex(double coordinateValue) {
        return (int)((coordinateValue - bounds.minY) / resolution);
    }

    /**
     * Private class that stores a representation of the bounding box of a protein.
     */
    private class Bounds {
        public double minX;
        public double minY;
        public double minZ;
        public double maxX;
        public double maxY;
        public double maxZ;
    }
}



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

    private HashSet<Atom> importAtoms(String filename) throws FileNotFoundException {
        HashSet<Atom> newSet = new HashSet<Atom>();
        Scanner infile = new Scanner(new FileInputStream(filename));
        while(infile.hasNextLine()) {
            String inputLine = infile.nextLine();
            newSet.add(parseAtomFromLine(inputLine));
        }
        return newSet;
    }
    private Atom parseAtomFromLine(String line) {
        String[] arr = line.split("\\s+");
        return new Atom(Double.parseDouble(arr[9]), Double.parseDouble(arr[5]), Double.parseDouble(arr[6]), Double.parseDouble(arr[7]));
    }

//    public CoordinateLabel[][][] getGrid() {
//        return grid;
//    }
    public int xLength() {
        return grid.length;
    }
    public int yLength() {
            return grid[0].length;
    }
    public int zLength() {
        return grid[0][0].length;
    }

    public ArrayList<Atom> getAtomsXSorted() {
        ArrayList<Atom> ret = new ArrayList<Atom>();
        Iterator<Atom> it = atoms.iterator();
        while(it.hasNext()) {
            ret.add(it.next());
        }
        Collections.sort(ret, new Comparator<Atom>() {
            @Override
            public int compare(Atom atom, Atom atom2) {
                return atom.myX > atom2.myX ? 1 : -1;
            }
        });
        return ret;
    }

    public ArrayList<Atom> getAtomsYSorted() {
        ArrayList<Atom> ret = new ArrayList<Atom>();
        Iterator<Atom> it = atoms.iterator();
        while(it.hasNext()) {
            ret.add(it.next());
        }
        Collections.sort(ret, new Comparator<Atom>() {
            @Override
            public int compare(Atom atom, Atom atom2) {
                return atom.myY > atom2.myY ? 1 : -1;
            }
        });
        return ret;
    }

    public ArrayList<Atom> getAtomsZSorted() {
        ArrayList<Atom> ret = new ArrayList<Atom>();
        Iterator<Atom> it = atoms.iterator();
        while(it.hasNext()) {
            ret.add(it.next());
        }
        Collections.sort(ret, new Comparator<Atom>() {
            @Override
            public int compare(Atom atom, Atom atom2) {
                return atom.myZ > atom2.myZ ? 1 : -1;
            }
        });
        return ret;
    }

    public double getResolution() {
        return resolution;
    }

    public double xIndexToCoordinate(int indexValue) {
        return bounds.minX + resolution * indexValue;
    }

    public double yIndexToCoordinate(int indexValue) {
        return bounds.minY + resolution * indexValue;
    }

    public double zIndexToCoordinate(int indexValue) {
        return bounds.minZ + resolution * indexValue;
    }

    public CoordinateLabel getLabel(int x, int y, int z) {
        if (x >= grid.length || x < 0 || y >= grid[0].length || y < 0 || z >= grid[0][0].length || z < 0) {
            return null;
        }
        return grid[x][y][z];
    }
    public boolean setLabel(int x, int y, int z, CoordinateLabel newVal) {
        if (x >= grid.length || x < 0 || y >= grid[0].length || y < 0 || z >= grid[0][0].length || z < 0) {
            return false;
        }
        grid[x][y][z] = newVal;
        return true;
    }

    public int getXIndex(double coordinateValue) {
        return (int)((coordinateValue - bounds.minX) / resolution);
    }
    public int getYIndex(double coordinateValue) {
        return (int)((coordinateValue - bounds.minY) / resolution);
    }
    public int getZIndex(double coordinateValue) {
        return (int)((coordinateValue - bounds.minY) / resolution);
    }

    private class Bounds {
        public double minX;
        public double minY;
        public double minZ;
        public double maxX;
        public double maxY;
        public double maxZ;
    }
}



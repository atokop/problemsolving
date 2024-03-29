package com.company;

import java.util.ArrayList;
import java.util.HashSet;

/**
/**
 * Created with IntelliJ IDEA.
 * User: kwamina
 * Date: 4/18/14
 * Time: 11:43 PM
 *
 * Generator class that runs the majority of the algorithm. Labels each coordinate with the appropriate tag depending
 * on the proximity of nearby atoms. The a recursive "floodfill" method is then run on each of the void spaces to
 * determine whether it touches the outside. If so, these points are not returned as cavities.
 */
public class SphereSetGenerator {
    private ArrayList<Atom> xsorted;
    double probeRadius;
    private Data data;
    public SphereSetGenerator(Data data, double probeRadius) {
        xsorted = data.getAtoms();
        this.probeRadius = probeRadius;
        this.data = data;
    }

    /**
     * Labels the grid according to coordinates' proximity to atoms.
     */
    private void labelGrid() {
        for (int i=0; i<data.xLength(); i++) {
             for (int j=0; j<data.yLength(); j++) {
                 for (int k=0; k<data.zLength(); k++) {
                     data.setLabel(i, j, k, CoordinateLabel.EMPTY_VOID);
                 }
             }
        }
        for (Atom atom : xsorted) {
            labelNearbyPoints(atom);
        }
    }

    /**
     * Returns the set of all cavities
     * @return HashSet containing a string representation of cavities.
     */
    public HashSet<String> allCavities() {
        labelGrid();
        return removeNonCavitySpaces();
    }

    /**
     * Removes void spaces that are not cavities (i.e. touch the edges)
     * @return cavityCoordinates returns all the cavity coordinates after removing non-cavity voids.
     */
    private HashSet<String> removeNonCavitySpaces() {
        HashSet<String> allVoidLocations = new HashSet<String>();
        for(int x=0; x<data.xLength(); x++) {
            for (int y=0; y<data.yLength(); y++) {
                for (int z=0; z<data.zLength(); z++) {
                    if (data.getLabel(x, y, z) == CoordinateLabel.EMPTY_VOID) {
                        allVoidLocations.add(gridLocationToStringLocation(x, y, z));
                    }
                }
            }
        }


        HashSet<String> cavityCoordinates = new HashSet<String>();
        while (!allVoidLocations.isEmpty()) {
            String nextVoid = allVoidLocations.iterator().next();
            int x = stringLocationToGridLocation(nextVoid)[0];
            int y = stringLocationToGridLocation(nextVoid)[1];
            int z = stringLocationToGridLocation(nextVoid)[2];
            removeNonCavitySpacesHelper(x, y, z, cavityCoordinates, allVoidLocations, false);
        }
        return cavityCoordinates;
    }

    /**
     * Returns a string representation of a location in the grid
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     * @return string representation of the location.
     */
    private String gridLocationToStringLocation(int x, int y, int z) {
        return "" + x + "," + y + "," + z;
    }

    /**
     * Returns a string representation of a location in the grid, overloaded for doubles.
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     * @return string representation of the location.
     */
    private String gridLocationToString(double x, double y, double z) {
        return "" + x + "," + y + "," + z;
    }
    private int[] stringLocationToGridLocation(String location) {
        String[] strings = location.split(",");
        if (strings.length != 3) {
            System.err.printf("Invalid grid location given");
            return null;
        }
        int[] ret = new int[3];
        for (int i=0; i<3; i++) {
            ret[i] = Integer.parseInt(strings[i]);
        }
        return ret;
    }

    /**
     * Helper method to remove all the non-cavity spaces from the set.
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     * @param cavitiesSet set that the void is added to if it is a cavity.
     * @param allVoids all voids being tested. voids are removed as helper method progresses
     * @param touchesEdge boolean input that tells whether a previous, neighbouring void has already been shown to
     *                    lead to the edge
     * @return touchesEdge value corresponding to whether this void has a path that leads to the edge.
     */
    private boolean removeNonCavitySpacesHelper(int x, int y, int z, HashSet<String> cavitiesSet, HashSet<String> allVoids, boolean touchesEdge) {
        String locationString = gridLocationToStringLocation(x, y, z);
        allVoids.remove(locationString);
        if (x==0 || x==data.xLength()-1 || y == 0 || y == data.yLength()-1 || z==0 || z==data.zLength()-1) {
            touchesEdge = true;
        }

        for (int i=-1; i<=1; i++) {
            for (int j=-1; j<=1; j++) {
                for (int k=-1; k<=1; k++) {
                    if ((data.getLabel(x+i, y+j, z+k) == CoordinateLabel.EMPTY_VOID
                            || data.getLabel(x+i, y+j, z+k) == CoordinateLabel.EMPTY_NONVOID)
                            && allVoids.contains(gridLocationToStringLocation(x+i, y+j, z+k))) {
                        touchesEdge = removeNonCavitySpacesHelper(x+i, y+j, z+k, cavitiesSet, allVoids, touchesEdge) || touchesEdge;
                    }
                }
            }
        }

        if (!touchesEdge) {
            double xcoord, ycoord, zcoord;
            xcoord = data.xIndexToCoordinate(x);
            ycoord = data.yIndexToCoordinate(y);
            zcoord = data.zIndexToCoordinate(z);
            cavitiesSet.add(gridLocationToString(xcoord, ycoord, zcoord));
        }
        return touchesEdge;
    }


    /**
     * For a given atom, labels nearby points according to whether they are empty but too close to contain a probe,
     * or within the atom itself.
     * @param atom the atom for which the nearby points are labelled.
     */
    private void labelNearbyPoints(Atom atom) {
        int minX = data.getXIndex(atom.myX - (atom.radius + probeRadius));
        int maxX = data.getXIndex(atom.myX + (atom.radius + probeRadius));
        int minY = data.getYIndex(atom.myY - (atom.radius + probeRadius));
        int maxY = data.getYIndex(atom.myY + (atom.radius + probeRadius));
        int minZ = data.getZIndex(atom.myZ - (atom.radius + probeRadius));
        int maxZ = data.getZIndex(atom.myZ + (atom.radius + probeRadius));

        for (int x=minX; x <= maxX; x++) {
            for (int y=minY; y<= maxY; y++) {
                for (int z=minZ; z<=maxZ; z++) {
                    CoordinateLabel oldValue = data.getLabel(x, y, z);
                    if (oldValue == CoordinateLabel.ATOM || oldValue == null) {
                        continue;
                    }
                    double xdiff = atom.myX - data.xIndexToCoordinate(x);
                    double ydiff = atom.myY - data.yIndexToCoordinate(y);
                    double zdiff = atom.myZ - data.zIndexToCoordinate(z);
                    double distanceSquared = xdiff*xdiff + ydiff*ydiff + zdiff*zdiff;
                    if (oldValue == CoordinateLabel.EMPTY_VOID) {
                        if (distanceSquared < (atom.radius+probeRadius) * (atom.radius+probeRadius)) {
                            data.setLabel(x, y, z, CoordinateLabel.EMPTY_NONVOID);
                        }
                        if (distanceSquared < atom.radius * atom.radius) {
                            data.setLabel(x, y, z, CoordinateLabel.ATOM);
                        }
                    }
                    else if (oldValue == CoordinateLabel.EMPTY_NONVOID) {
                        if (distanceSquared < atom.radius * atom.radius) {
                            data.setLabel(x, y, z, CoordinateLabel.ATOM);
                        }
                    }
                }
            }
        }
    }



}

package com.company;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created with IntelliJ IDEA.
 * User: kwamina
 * Date: 4/18/14
 * Time: 11:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class SphereSetGenerator {
    private ArrayList<Atom> xsorted;
    double probeRadius;
    private Data data;
    public SphereSetGenerator(Data data, double probeRadius) {
        xsorted = data.getAtomsXSorted();
        this.probeRadius = probeRadius;
        this.data = data;
    }

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
    public HashSet<String> allCavities() {
        labelGrid();
        return removeNonCavitySpaces();
    }

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

    private String gridLocationToStringLocation(int x, int y, int z) {
        return "" + x + "," + y + "," + z;
    }

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

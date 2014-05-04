package com.company;

/**
 * Created with IntelliJ IDEA.
 * User: kwamina
 * Date: 4/15/14
 * Time: 12:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class Atom {
    /**
     * Variable radius is the radius of an atom
     * Variable name is the name of the atom
     * Variables myX, myY, and myZ are the coordinates of an atom
     */
    public double radius;
    public double name;
    public double myX, myY, myZ;

    /**
     *
     * @param radius = radius of the atom
     * @param x = x cooridnate of the atom
     * @param y = y cooridnate of the atom
     * @param z = z cooridnate of the atom
     */
    public Atom(double radius, double x, double y, double z) {
        myX = 0;
        myX = x;
        myY = y;
        myZ = z;
        this.radius = radius;
    }

    /**
     *
     * @param radius = radius of the atom
     * @param x = x coordinate of the atom
     * @param y = y coordinate of the atom
     * @param z = z coordinate of the atom
     * @param name = name of an atom
     */
    public Atom(double radius, double x, double y, double z, double name) {
        myX = x;
        myY = y;
        myZ = z;
        this.radius = radius;
        this.name = name;
    }
}

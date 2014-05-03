package com.company;

/**
 * Created with IntelliJ IDEA.
 * User: kwamina
 * Date: 4/15/14
 * Time: 12:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class Atom {
    public double radius;
    public double name;
    public double myX, myY, myZ;
    public Atom(double radius, double x, double y, double z) {
        myX = 0;
        myX = x;
        myY = y;
        myZ = z;
        this.radius = radius;
    }
    public Atom(double radius, double x, double y, double z, double name) {
        myX = x;
        myY = y;
        myZ = z;
        this.radius = radius;
        this.name = name;
    }
}

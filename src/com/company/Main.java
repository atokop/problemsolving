package com.company;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        if (args.length != 8) {
            System.err.printf("Invalid number of arguments!");
            System.err.printf("Usage e.g.: cavityFinder -i inputfile -o outputfile -probe 1.7 -resolution 0.25");
            return;
        }

        String infile, outfile;
        double probeRadius, resolution;

        List argsList = Arrays.asList(args);
        int infileIndex = argsList.indexOf("-i");
        int outfileIndex = argsList.indexOf("-o");
        int probeIndex = argsList.indexOf("-probe");
        int resolutionIndex = argsList.indexOf("-resolution");

        infile = infileIndex != -1 ? args[infileIndex + 1] : null;
        outfile = outfileIndex != -1 ? args[outfileIndex + 1] : null;
        probeRadius = probeIndex != -1 ? Double.parseDouble(args[probeIndex + 1]) : -1;
        resolution = resolutionIndex != -1 ? Double.parseDouble(args[resolutionIndex + 1]) : -1;

        if (infile == null || outfile == null || probeRadius == -1 || resolution == -1) {
            System.err.printf("Invalid usage!");
            return;
        }

        Data im = new Data(infile, resolution);
        SphereSetGenerator gen = new SphereSetGenerator(im, probeRadius);
        HashSet<String> set = gen.allCavities();

        Scanner instream;
        PrintWriter output;
        try {
            output = new PrintWriter(outfile);
            instream = new Scanner(new FileInputStream(infile));
            while(instream.hasNextLine()) {
                String inputLine = instream.nextLine();
                output.println(inputLine);
            }
            int index = 0;
            for (String s : set) {
                output.println(atomFormat(s, index, probeRadius));
            }
            output.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
	// write your code here
    }

    private static String atomFormat(String s, int index, double radius) {
        String[] coords = s.split(",");
        return "ATOM\t1000"+(index+1)+"\tMC\tCAV\t500"+(index+1)+"\t"+coords[0]+"\t"+coords[1]+"\t"+coords[2]+"\t1\t"+radius;
    }

}

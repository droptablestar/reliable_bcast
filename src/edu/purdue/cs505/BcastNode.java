package edu.purdue.cs505;

import java.io.*;
import java.util.Iterator;
import java.util.ArrayList;
import java.net.*;

public class BcastNode {
    private static final int MAX_PROCS = 100;
    
    public static void main(String args[]) {
        ArrayList<Process> processes = new ArrayList<Process>();
        ArrayList<RBroadcast> bcasts = new ArrayList<RBroadcast>();

        // String localhost = (InetAddress.getLoopbackAddress()).toString();
        String localhost = "127.0.0.1";
        // System.out.println(localhost);
        for (int i=0; i<MAX_PROCS; i++) {
            Process p = new Process(localhost, 6666+i);
            processes.add(p);
        }

        for (int i=0; i<MAX_PROCS; i++) {
            RBroadcast b = new RBroadcast();
            bcasts.add(b);
            Process p = processes.get(i);
            b.init(p);

            for (int j=0; j<MAX_PROCS; j++){
                if (i != j) {
                    Process pp = processes.get(j); 
                    b.addProcess(pp);
                }
            }
        }

        // System.exit(1);

        // for (int i=0; i<MAX_PROCS; i++) {
        //     System.out.println("Process list for: " + i);
        //     bcasts.get(i).printProcesses();
        //     System.out.println();
        // }

        for (int i=0; i<MAX_PROCS; i++) {
            bcasts.get(i).rblisten(new BcastReceiver());
        }

        bcasts.get(0).rbroadcast(new Message(new Integer(0).toString(),
                                             processes.get(0)));
        for (int i=0; i<MAX_PROCS; i++) {
            // bcasts.get(i).haltR();
            // bcasts.get(i).halt();
        }
    }
}

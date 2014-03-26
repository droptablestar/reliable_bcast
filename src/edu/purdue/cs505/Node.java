package edu.purdue.cs505;

import java.io.*;
import java.util.Iterator;

public class Node {
    public static void main(String args[]) {
        if (args.length < 3) {
            System.out.println("Usage: java -jar dist/rchannel.jar "+
                               "<destinationIP> <sendPort> <receivePort>");
            System.exit(-1);
        }
                               
            
        int sendPort = Integer.parseInt(args[1]);

        RChannelReceiver rcr = new RChannelReceiver();
        RChannel channel = new RChannel(Integer.parseInt(args[2]));
        channel.init(args[0], sendPort);
        channel.rlisten(rcr);
        if (sendPort == 6666) {
            for (int i=0; i<100000; i++)
                channel.rsend(new RMessage(new Integer(i).toString()));
            // channel.rsend((new RMessage(
            //                    new Integer(100001).toString())
            //                ).makeEOT(1));
        }
        boolean done = false;
        while (!done) {
            // // System.out.println(channel.sThread.messageQueueSize());
            // if (channel.sThread.messageQueueSize() <= 0) {
            //     System.out.println("HALTING!");
            //     channel.halt();
            //     done = true;
            // }
            // System.out.println("SENDER: " + channel.sThread.isDone());
            // System.out.println("RECEIVER: " + channel.rThread.isDone());
            if (channel.sThread.isDone() && channel.rThread.isDone()) {
                channel.halt();
                channel.haltR();
                done = true;
                // System.out.println("NODE DONE!");
            }
            try { Thread.sleep(100); }
            catch (InterruptedException e) {System.out.println("sleep: "+e);}
        }
    }
}

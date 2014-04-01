package edu.purdue.cs505;

import java.io.*;
import java.util.Iterator;

public class Node {
    public static void main(String args[]) {
        int id = Integer.parseInt(args[2]);
        RChannel channel = new RChannel(id);

        channel.init(args[0], Integer.parseInt(args[1]));
        channel.rlisten(new RChannelReceiver());
        Process p = new Process("localhost", id);
        if (id == 6667) {
            for (int i=0; i<100000; i++) {
                Message m = new Message(new Integer(i).toString(), p);
                m.toSend(args[0], Integer.parseInt(args[1]));
                channel.rsend(m);
            }
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
                System.out.println("NODE DONE!");
            }
            try { Thread.sleep(100); }
            catch (InterruptedException e) {System.out.println("sleep: "+e);}
        }
        // BufferedReader br =
        //     new BufferedReader(new InputStreamReader(System.in));
        // if (id == 0) {
        //     try {
        //         String msg;
        //         while ((msg = br.readLine()) != null)
        //             channel.rsend(new RMessage(msg));
        //     } catch(IOException e) {
        //         System.err.println("IO err: " + e);
        //     }
        // }

        // channel.halt();

        // channel.init(args[0], 6666);
        // if (id == 0) {
        //     try {
        //         String msg;
        //         while ((msg = br.readLine()) != null)
        //             channel.rsend(new RMessage(msg));
        //         System.out.print("PRESS ENTER FOO!! ");
        //         br.readLine();
        //     } catch(IOException e) {
        //         System.err.println("IO err: " + e);
        //     }
        // }
        // System.out.print("\nHALT\n");
        // channel.halt();
    }
}

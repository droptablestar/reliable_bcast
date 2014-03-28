package edu.purdue.cs505;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.ArrayList;
import java.util.Iterator;

public class BReceiveThread extends Thread {

    /** Channel used by the broadcast */
    private RChannel channel;

    /** RChannelReceiver object to use as a callback. */
    private BcastReceiver bcr;
    
    /** Hashmap of messages already seen. */
    private HashMap<String,Integer> seenMsgs;

    /** Queue of received messages, filled by RChannelReceiver. */
    private BlockingQueue<Message> receivedQueue; 

    /** List of processes involved in the broadcast. */
    private ArrayList<Process> processList;
    
    /** Stopped? */
    private boolean stopped;
    private boolean done;
    
    /** Constructor for the breceive thread. 
     *
     * @param bcr RcastReceiver callback object.
     * @param seenMsgs Hashmap of seen messages.
     */
    public BReceiveThread(BcastReceiver bcr, BlockingQueue<Message> rcvQ,
                            ArrayList<Process> pList, RChannel channel) {
        this.bcr = bcr;
        this.stopped = false;
        this.done = false;
        this.seenMsgs = new HashMap<String, Integer>();
        this.receivedQueue = rcvQ;
        this.processList = pList;
        this.channel = channel;
    } // BReceiveThread()

    /** Main method for breceive thread. Waits for new messages then passes
     * each received message to the callback function for processing.
     */
    public void run() {
        while (!stopped) {
            Message msg = receivedQueue.peek();
            while (msg != null){
                msg = receivedQueue.poll();
                if (!seenMsgs.containsKey(msg.getProcessID())){
                    seenMsgs.put(msg.getProcessID(), 1);
                    for(Iterator<Process> pi=processList.iterator();
                             pi.hasNext();) {
                        Process p = pi.next();
                        if(!(p.getIP() == msg.getSourceIP() &&
                                     p.getPort() == msg.getSourcePort())){
                            channel.rsend(msg);
                        }
                    }
                    bcr.rdeliver(msg);
                }
            }
        }
    } // run()

    /**
     * Sets a boolean in the thread to exit the run() loop
     */
    public void kill() {
        this.stopped = true;
    } // kill()

    public boolean isDone() {
        // System.out.println("TO: " + toAck.size() + " AL: " +ackList.size());
        return done;
    }
}

package edu.purdue.cs505;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.HashMap;

public class BReceiveThread extends Thread {

    /** RChannelReceiver object to use as a callback. */
    private BcastReceiver bcr;
    
    /** Hashmap of messages already seen. */
    private HashMap<String,Integer> seenMsgs;
    
    /** Stopped? */
    private boolean stopped;
    
    /** Constructor for the breceive thread. 
     *
     * @param bcr RcastReceiver callback object.
     * @param seenMsgs Hashmap of seen messages.
     */
    public BReceiveThread(BcastReceiver bcr, Hashmap<String,Integer> seenMsgs) {
        this.bcr = rcr;
        this.seenMsgs = seenMsgs;
        this.stopped = false;
    } // BReceiveThread()

    /** Main method for breceive thread. Waits for new messages then passes
     * each received message to the callback function for processing.
     */
    public void run() {
        try {
            while (!stopped) {
                // do some stuff
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

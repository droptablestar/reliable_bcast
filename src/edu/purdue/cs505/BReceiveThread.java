package edu.purdue.cs505;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Comparator;

import java.util.concurrent.BlockingQueue;

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
    private boolean isFIFO;
    private int expectedSeqNum;

    private ArrayList<Message> delayedQueue;

    /** Constructor for the breceive thread. 
     *
     * @param bcr RcastReceiver callback object.
     * @param seenMsgs Hashmap of seen messages.
     */
    public BReceiveThread(BcastReceiver bcr, BlockingQueue<Message> rcvQ,
                          ArrayList<Process> pList, RChannel channel,
                          boolean isFIFO) {
        this.bcr = bcr;
        this.stopped = false;
        this.done = false;
        this.seenMsgs = new HashMap<String, Integer>();
        this.receivedQueue = rcvQ;
        this.processList = pList;
        this.channel = channel;
        this.isFIFO = isFIFO;
        this.expectedSeqNum = 0;
        this.delayedQueue = new ArrayList<Message>();
    } // BReceiveThread()

    /** Main method for breceive thread. Waits for new messages then passes
     * each received message to the callback function for processing.
     */
    public void run() {
        while (!stopped) {
            Message msg = receivedQueue.peek();
            while (msg != null) {
                msg = receivedQueue.poll();

                if (msg.isOBS()) obs(msg);
                else if (isFIFO) receiveFIFO(msg);
                else receiveBcast(msg);

                    
                msg = receivedQueue.peek();
            }
            if (FRBroadcast.srbOn) {
                long now = System.currentTimeMillis();
                for (Iterator<Message> mi=delayedQueue.iterator();
                     mi.hasNext(); ) {
                    Message m = mi.next();
                    if ((now - m.getTimeout()) > FRBroadcast.deliveryDelay) {
                        bcr.rdeliver(m);
                        mi.remove();
                    }
                    else
                        break;
                }
            }
        }
    } // run()

    private void receiveFIFO(Message msg) {
        if (!seenMsgs.containsKey(msg.getProcessID())
            && msg.getSeqNum() == expectedSeqNum) {
            seenMsgs.put(msg.getProcessID(), 1);
            
            for(Iterator<Process> pi=processList.iterator(); pi.hasNext();) {
                Process p = pi.next();
                if (p.getIP() != msg.getSourceIP()
                    && p.getPort() != msg.getDestPort()) {
                    Message m = new Message(msg.getDestIP(),msg.getDestPort(),
                                            p.getIP(), p.getPort(),
                                            Header.NACK, msg.getSeqNum(),
                                            msg.getContents());

                    m.setProcessID(msg.getProcessID());
                    channel.rsend(m);
                }
            }
            if (FRBroadcast.srbOn) {
                msg.setTimeout();
                delayedQueue.add(msg);
            }
            else 
                bcr.rdeliver(msg);
            expectedSeqNum++;
        }
        else if (msg.getSeqNum() > expectedSeqNum) {
            try { receivedQueue.put(msg); }
            catch (InterruptedException e) {
                System.out.println("Error putting message in receivedQueue.");
                System.out.println(e);
            }
        }
    }
    
    private void receiveBcast(Message msg) {
        if (!seenMsgs.containsKey(msg.getProcessID())) {
            seenMsgs.put(msg.getProcessID(), 1);
            for (Iterator<Process> pi=processList.iterator(); pi.hasNext();) {
                Process p = pi.next();
                if (p.getIP() != msg.getSourceIP()
                    && p.getPort() != msg.getDestPort()) {
                    Message m = new Message(msg.getDestIP(),msg.getDestPort(),
                                            p.getIP(), p.getPort(),
                                            Header.NACK, msg.getSeqNum(),
                                            msg.getContents());
                    m.setProcessID(msg.getProcessID());
                    channel.rsend(m);
                }
            }
            bcr.rdeliver(msg);
        }
    }

    private void obs(Message msg) {
        for (Iterator<Message> mi=delayedQueue.iterator(); mi.hasNext(); ) {
            Message m = mi.next();
            if (m.getSeqNum() <= msg.getSeqNum())
                mi.remove();
        }
    }
    
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

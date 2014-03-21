package edu.purdue.cs505;

import java.io.*;
import java.net.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

public class SendThread extends Thread {
    public static final int MAX_QUEUE = 2000;
    
    /** amount of  time (in ms) to wait before resending a message */
    private final long TIMEOUT = 100;

    /** A list which contains messages the sender needs to send. */
    private PriorityBlockingQueue<RMessage> messageQueue;

    /** A list which contains messages the receiver has ACK'd. This is how
     * messages will be removed from the sender's queue. Shared across
     * threads. */
    private List<RMessage> ackList;

    /** A list which contains messages the sender thread needs to send ACKs
     *  for. Shared across threads. */
    private List<RMessage> toAck;

    private List<RMessage> waitList;

    /** The socket to be used for communication in this thread. */
    private DatagramSocket socket;

    /** The destination IP messages will be sent to. */
    private InetAddress destIP;

    /** The destination port the receiver is using. */
    private int destPort;

    /** Value determining whether or not to continue execution. */
    private boolean stopped;

    /** Constructor for this thread. Initializes all vairables and sets up
     * the socket for communication.
     *
     * @param destIP destination of IP to be used to send
     * @param destPort port number destination host is using
     * @param messageQueue priorityQueue of messages, sorted on timeout
     * @param ackList list of message whos ACK's have already been received
     * but are still in the sending queue
     * @param toAck messages that have been received but need ACKs to be sent
     */
    public SendThread(String destIP, int destPort,
                      PriorityBlockingQueue<RMessage> messageQueue,
                      List<RMessage> ackList, List<RMessage> toAck,
                      List<RMessage> waitList) {
        this.stopped = false;
        this.messageQueue = messageQueue;
        this.ackList = ackList;
        this.toAck = toAck;
        this.waitList = waitList;
        this.destPort = destPort;
        
        try {
            this.destIP = InetAddress.getByName(destIP);
            socket = new DatagramSocket();
            socket.connect(this.destIP, this.destPort);
        } catch (UnknownHostException e) {
            System.err.println("SENDER -- Host name error: " + e);
            System.exit(1);
        } catch (SocketException e) {
            System.err.println("SENDER -- socket error: " + e);
            System.exit(1);
        }
    } // SendThread()

    /** Main execution method for send thread. Handles sending all messages. 
     */
    public void run() {
        while (!stopped) {
            long now = System.currentTimeMillis();
            RMessage msg = messageQueue.peek();
            while (msg != null && (now - msg.getTimeout()) >= TIMEOUT) {
                // send message, update timeout, and put it back in queue
                msg = messageQueue.poll();
                
                // if this message has already been ACK'd, dont send it
                // again. remove it from the queue
                if (removeACK(msg)) {
                    msg = messageQueue.peek();
                    continue;
                }
                
                send(msg);
                msg.setTimeout();
                // System.out.print("Retransmitting: "); msg.printMsg();
                messageQueue.offer(msg);
                msg = messageQueue.peek();
            }
            int mq_size = messageQueue.size();
            if (mq_size < MAX_QUEUE && waitList.size() > 0) {
                int diff = MAX_QUEUE - messageQueue.size();
                Iterator<RMessage> wi = waitList.iterator();
                while (wi.hasNext() && (diff--) >= 0) {
                    messageQueue.offer(wi.next());
                    wi.remove();
                }
            }
            /* send ACKs. they are stored in toAck. */
            synchronized(toAck) {
                Iterator<RMessage> ai = toAck.iterator();
                while (ai.hasNext()) {
                    RMessage m = ai.next();
                    // System.out.print("ACKING: ");
                    // m.printMsg();
                    // if (m.isEOT1()) send(m);
                    m.makeACK();
                    send(m);
                    ai.remove();
                    // System.out.print("ACKing: "); m.printMsg();
                }
            }
            Iterator<RMessage> mi=messageQueue.iterator();
            while (mi.hasNext()) {
                RMessage m = mi.next();
                if (removeACK(m))
                    mi.remove();
            }
        }
        System.out.println("SENDER OUT!");
    } // run()

    /**
     * Sets a boolean in the thread to exit the run() loop
     */
    public void kill() {
        this.stopped = true;
    } // kill()

    /**
     * Sends a message.
     *
     * @param msg message to be sent
     */
    private void send(RMessage msg) {
        try {
            String message = msg.getMessageContents();
            byte[] buf = new byte[message.length()];
            buf = message.getBytes();
            DatagramPacket packet =
                new DatagramPacket(buf, buf.length, destIP, destPort);
            socket.send(packet);
        } catch(IOException e) {
            System.err.println("SENDER -- packet send error: " + e);
        }
    } // send()

    /** Removes all instances of an ACK from the list of received ACKs.
     *
     * @param msg message to check for in the ackList
     * @return true if something was removed, otherwise false
     */
    private boolean removeACK(RMessage msg) {
        boolean removed = false;
        synchronized(ackList) {
            for (Iterator<RMessage> ai=ackList.iterator(); ai.hasNext(); ) {
                RMessage m = ai.next();
                // System.out.println("id: "+msg.getMessageID()+" id: "+
                //                    m.getMessageID());
                if (msg.getMessageID() == m.getMessageID()) {
                    ai.remove();
                    // System.out.print("REMOVING!");
                    // System.out.println("msg: "+msg.getMessageID() + " m: " +
                    //                    m.getMessageID());
                    removed = true;
                }
            }
        }
        return removed;
    }

    public int messageQueueSize() { return this.messageQueue.size(); }

    public boolean isDone() {
        // System.out.println("MQ: " + messageQueue.size() + " TO: " +
        //                    toAck.size() + " AL: " + ackList.size());
        return (messageQueue.size() == 0 && toAck.size() == 0) ?  true : false;
    }
}
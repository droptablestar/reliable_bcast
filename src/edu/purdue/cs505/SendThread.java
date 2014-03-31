package edu.purdue.cs505;

import java.io.*;
import java.net.*;
import java.util.PriorityQueue;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

public class SendThread extends Thread {
    public static final int MAX_QUEUE = 2000;
    
    /** amount of  time (in ms) to wait before resending a message */
    private final long TIMEOUT = 100;

    /** A list which contains messages the sender needs to send. */
    private PriorityQueue<Message> messageQueue;

    /** A list which contains messages the receiver has ACK'd. This is how
     * messages will be removed from the sender's queue. Shared across
     * threads. */
    private List<Message> ackList;

    /** A list which contains messages the sender thread needs to send ACKs
     *  for. Shared across threads. */
    private List<Message> toAck;

    private List<Message> waitList;

    /** The socket to be used for communication in this thread. */
    private DatagramSocket socket;

    /** The destination IP messages will be sent to. */
    private InetAddress destIP;

    /** The destination port the receiver is using. */
    private int destPort;

    /** Value determining whether or not to continue execution. */
    private boolean stopped;

    private String hostName;
    
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
                      PriorityQueue<Message> messageQueue,
                      List<Message> ackList, List<Message> toAck,
                      List<Message> waitList) {
        this.stopped = false;
        this.messageQueue = messageQueue;
        this.ackList = ackList;
        this.toAck = toAck;
        this.waitList = waitList;
    
        try {
            this.destIP = InetAddress.getByName(destIP);
            socket = new DatagramSocket();
            // socket.connect(this.destIP, this.destPort);
        } catch (UnknownHostException e) {
            System.err.println("SENDER -- Host name error: " + e);
            System.exit(1);
        } catch (SocketException e) {
            System.err.println("SENDER -- socket error: " + e);
            System.exit(1);
        }
        this.destPort = destPort;
    } // SendThread()

    /** Main execution method for send thread. Handles sending all messages. 
     */
    public void run() {
        while (!stopped) {
            long now = System.currentTimeMillis();
            Message msg = messageQueue.peek();
            while (msg != null && (now - msg.getTimeout()) >= TIMEOUT) {
                //System.out.println("Processessesing a message");
                // send message, update timeout, and put it back in queue
                msg = messageQueue.poll();
                
                // if this message has already been ACK'd, dont send it
                // again. remove it from the queue
                if (removeACK(msg)) {
                    msg = messageQueue.peek();
                    continue;
                }
                
                // System.out.print(destPort + ": Retransmitting: ");
                // msg.printMsg();
                send(msg);
                msg.setTimeout();
                messageQueue.offer(msg);
                msg = messageQueue.peek();
            }
            int mq_size = messageQueue.size();
            if (mq_size < MAX_QUEUE && waitList.size() > 0) {
                int diff = MAX_QUEUE - mq_size;
                synchronized(waitList) {
                    Iterator<Message> wi = waitList.iterator();
                    while (wi.hasNext() && (diff--) >= 0) {
                        // System.out.println("ADDING " + diff);
                        messageQueue.offer(wi.next());
                        wi.remove();
                    }
                }
            }

            /* send ACKs. they are stored in toAck. */
            synchronized(toAck) {
                Iterator<Message> ai = toAck.iterator();
                while (ai.hasNext()) {
                    Message m = ai.next();
                    //System.out.print("ACKING: ");
                    // m.printMsg();
                    // if (m.isEOT1()) send(m);
                    sendACK(m);
                    ai.remove();
                    // System.out.print("ACKing: "); m.printMsg();
                }
            }

            Iterator<Message> mi=messageQueue.iterator();
            while (mi.hasNext()) {
                Message m = mi.next();
                if (removeACK(m)) {
                    // System.out.print("REMOVING: ");
                    // m.printMsg();
                    mi.remove();
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

    /**
     * Sends a message.
     *
     * @param msg message to be sent
     */
    private void send(Message msg) {
        try {
            try {
                destIP = InetAddress.getByName(msg.getDestIP());
            }
            catch (UnknownHostException e) {
                System.out.println("Error send(): "+msg.getDestIP());
                System.out.println(e);
                System.exit(1);
            }
            destPort = msg.getDestPort();
            // System.out.println("sending to "+destIP+":"+destPort);
            // msg.printMsg();

            String message = msg.headerToString()+":"+msg.getContents();
            byte[] buf = new byte[message.length()];
            buf = message.getBytes();
            DatagramPacket packet =
                new DatagramPacket(buf, buf.length, destIP, destPort);
            socket.send(packet);
        } catch(IOException e) {
            System.err.println("SENDER -- packet send error: " + e);
        }
    } // send()

    /**
     * Sends a ACK.
     *
     * @param msg message to be ACK'd
     */
    private void sendACK(Message msg) {
        try {
            InetAddress IP=null;
            try {
                IP = InetAddress.getByName(msg.getSourceIP());
            }
            catch (UnknownHostException e) {
                System.out.println("FAIL: sendACK() --" + msg.getDestIP());
                System.out.println(e);
                System.exit(1);
            }
            Message ack = new Message(msg.getDestIP(), msg.getDestPort(),
                                      msg.getSourceIP(), msg.getSourcePort(),
                                      Header.ACK, msg.getSeqNum(),
                                      msg.getContents());

            ack.setProcessID(msg.getProcessID());
            String message = ack.headerToString();
            byte[] buf = new byte[message.length()];
            buf = message.getBytes();

            DatagramPacket packet =
                new DatagramPacket(buf, buf.length, IP, msg.getSourcePort());
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
    private boolean removeACK(Message msg) {
        boolean removed = false;
        synchronized(ackList) {
            for (Iterator<Message> ai=ackList.iterator(); ai.hasNext(); ) {
                Message m = ai.next();
                // msg.printMsg();
                // m.printMsg();
                // System.out.println("id: "+msg.getProcessID()+" id: "+
                //                    m.getProcessID());
                if (msg.getProcessID().equals(m.getProcessID())) {
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

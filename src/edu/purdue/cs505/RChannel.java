package edu.purdue.cs505;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class RChannel implements ReliableChannel {
    /** The thread that will control the receiver. */
    public ReceiveThread rThread;

    /** The thread that will control the sender. */
    public SendThread sThread;

    /** A list which contains messages the receiver has ACK'd. This is how
     * messages will be removed from the sender's queue. Shared across
     * threads. */
    public List<RMessage> ackList;

    /** A list which contains messages the sender thread needs to send ACKs
     *  for. Shared across threads. */
    public List<RMessage> toAck;

    /** A list which contains messages the sender needs to send. These are
     * sorted on timeout values with the smallest timeout value being the
     * top of the queue.*/
    public PriorityBlockingQueue<RMessage> messageQueue;

    /** The 'machine' id of a node. Used to generate unique port numbers
     * for local testing. */
    private int id;

    /** Constructor which initializes the messageQueue, ackList, and toAck
     * lists  for a Node.
     *
     * @param id unique machine id for this node.
     */
    public RChannel(int id) {
        Comparator<RMessage> comparator = new RMessageComparator();
        messageQueue = new PriorityBlockingQueue<RMessage>(10, comparator);
        ackList = Collections.synchronizedList(new ArrayList<RMessage>());
        toAck = Collections.synchronizedList(new ArrayList<RMessage>());
        this.id = id;
    } // RChannel()

    /** Sets up the sender thread. One thread will be spawned for each
     * destination.
     *
     * @param destinationIP the IP address of the node to be sent to.
     * @param destinationPort the port on the destination node to be sent to.
     */     
    public void init(String destinationIP, int destinationPort) {
        int dest = destinationPort == (6666+id) ? 6667 : 6666;
        sThread = new SendThread(destinationIP, dest, messageQueue,
                                 ackList, toAck);
        sThread.start();
    } // init()

    /** Places a message in the sender threads messageQueue with a timeout
     * value of the current system time.
     *
     * @param m the message to be sent.
     */
    public void rsend(Message m) {
        synchronized(messageQueue) {
            messageQueue.offer((RMessage)m);
        }
    } // rsend()

    /** Spawns a new receiver thread.
     *
     * @param rcr the RChannelReceiver callback to be used.
     */
    public void rlisten(ReliableChannelReceiver rcr) {
        rThread = new ReceiveThread(6666 + this.id, (RChannelReceiver)rcr,
                                    ackList, toAck);
        rThread.start();
    } // rlisten()

    /** Kills the sender thread thus preventing anymore messages from being
     * sent.
     */
    public void halt() {
        sThread.kill();
        try {
            sThread.join();
        } catch(InterruptedException e) {
            System.err.println("halt() " + e);
        }
    } // halt()
    
    /** Kills the sender thread thus preventing anymore messages from being
     * sent.
     */
    public void haltR() {
        rThread.kill();
        try {
            rThread.join();
        } catch(InterruptedException e) {
            System.err.println("halt() " + e);
        }
    } // halt()
} 
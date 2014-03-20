package edu.purdue.cs505;

import java.io.PrintWriter;
import java.io.FileNotFoundException;

import java.util.List;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Iterator;

public class RChannelReceiver implements ReliableChannelReceiver {
    /** List of ACKs received. Shared across threads. */
    private List<RMessage> ackList;

    /** List of ACKs to be sent. Shared across threads. */
    public List<RMessage> toAck;

    /** List of all messages received. */
    private PriorityQueue<Integer> receivedMsgs;

    /** Object used to write messages to a file. Used for testing and
     * debugging. */
    private PrintWriter wr;

    /** Constructor for RChannelReceiver. Sets up printwriter and received
     * message list.
     */
    public RChannelReceiver() {
        Comparator<RMessage> comparator = new RMessageComparator();
        this.receivedMsgs = new PriorityQueue<Integer>();

        try {
            this.wr = new PrintWriter("tests/output.out");
        } catch(FileNotFoundException e) {
            System.err.println("File not found: " + e);
            System.exit(1);
        }
    } // RChannelReceiver()

    /** Callback function used when a message is received.
     *
     * @param m message received.
     */
    public void rreceive(Message m) {
        RMessage msg = (RMessage)m;

        if (msg.isACK()) {   // if message is an ACK add to ackList
            ackList.add(msg);
            // System.out.println("Received ACK for: " + msg.getMessageID());
        }
        else { // else check to see if its already been received
            // System.out.println("Received msg: " + msg.getMessageID());
            if (!receivedMsgs.contains(msg.getMessageID())) {
                // first time a message was received.
                wr.println(msg.getMessageString());
                wr.flush();
                receivedMsgs.offer(msg.getMessageID());
            }
            toAck.add(msg);
        }
    } // rreceive()

    /** Sets the ackList for this object.
     * @param ackList list to be used.
     */
    public void setAckList(List<RMessage> ackList) { this.ackList = ackList; }

    /** Sets the toAck list for this object.
     * @param toAck list to be used.
     */
    public void setToAck(List<RMessage> toAck) { this.toAck = toAck; }
}

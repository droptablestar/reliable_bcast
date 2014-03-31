package edu.purdue.cs505;

import java.io.PrintWriter;
import java.io.FileNotFoundException;

import java.util.List;
import java.util.Comparator;
import java.util.Iterator;

import java.util.concurrent.BlockingQueue;

public class RChannelReceiver implements ReliableChannelReceiver {
    /** Object used to write messages to a file. Used for testing and
     * debugging. */
    private PrintWriter wr;
    private BlockingQueue<Message> receivedQueue;
    private boolean isBcast;

    public RChannelReceiver(BlockingQueue<Message> receivedQueue) {
        this.receivedQueue = receivedQueue;
        isBcast = true;
    }

    /** Constructor for RChannelReceiver. Sets up printwriter and received
     * message list.
     */
    public RChannelReceiver() {
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
        Message msg = (Message) m;
        if (!isBcast) {
            wr.println(msg.getMessageString());
            wr.flush();
        }
        else {
            System.out.print("CALLBACK: "); msg.printMsg();
            try {
                this.receivedQueue.put(msg);
            }
            catch (InterruptedException e) {
                System.out.println("Error rreceive(): ");
                System.out.println(e);
                System.exit(1);
            }
        }
    } // rreceive()
}

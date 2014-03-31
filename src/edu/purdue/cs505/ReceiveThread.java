package edu.purdue.cs505;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

public class ReceiveThread extends Thread {
    /** Socket to use for communication. */
    private DatagramSocket socket;

    /** Port number to bind to. */
    private int portNumber;

    /** RChannelReceiver object to use as a callback. */
    private RChannelReceiver rcr;
    
    /** List of ACKs received. Shared across threads. */
    private List<Message> ackList;

    /** List of ACKs to be sent. Shared across threads. */
    private List<Message> toAck;

    /** Value determining whether or not to continue execution. */
    private boolean stopped;

    /** List of all messages received. */
    private HashMap<String, Integer> receivedMsgs;

    private boolean done;
    
    private BlockingQueue<Message> receivedQueue; 

    /** Constructor for the receive thread. 
     *
     * @param portNumber port to bind to.
     * @param rcr RChannelReceiver callback object.
     * @param ackList list of ACKs received. Modified by rcr.
     * @param toAck list of ACKs to be sent. Modified by rcr.
     */
    public ReceiveThread(int portNumber, RChannelReceiver rcr,
                         List<Message> ackList, List<Message> toAck) {
        this.portNumber = portNumber;
        this.rcr = rcr;
        this.ackList = ackList;
        this.toAck = toAck;
        this.receivedMsgs = new HashMap<String, Integer>();
        this.stopped = false;
        this.done = false;
    } // ReceiveThread()

    public ReceiveThread(int portNumber, RChannelReceiver rcr,
                         List<Message> ackList, List<Message> toAck,
                         BlockingQueue<Message> receivedQueue) {
        this.portNumber = portNumber;
        this.rcr = rcr;
        this.ackList = ackList;
        this.toAck = toAck;
        this.receivedMsgs = new HashMap<String, Integer>();
        this.stopped = false;
        this.done = false;
        this.receivedQueue = receivedQueue;
    } // ReceiveThread()

    /** Main method for receive thread. Waits for new messages then passes
     * each received message to the callback function for processing.
     */
    public void run() {
        try {
            this.socket = new DatagramSocket(portNumber);
            this.socket.setSoTimeout(500);
            while (!stopped) {
                //System.out.println(portNumber + " " + toAck.size() + " " + ackList.size());
                byte[] buf = new byte[65536];
                DatagramPacket packet =
                    new DatagramPacket(buf, buf.length);

                try { socket.receive(packet); }
                catch (SocketTimeoutException s) { continue; }
                String msg =
                    new String(packet.getData(), 0, packet.getLength());

                /* This is for testing. Simulates packets being dropped.
                 */
                // double rn = Math.random();
                // if (rn >= 0.2) {
                //     System.out.println("DROPPED: " +
                //                        msg.substring(0,msg.indexOf(':')));
                //     continue;
                // }

		// build an Message out of this bidniss
		Message finalProduct = new Message();
                finalProduct.setContents(msg);
                finalProduct.stripHeader();
                // if (finalProduct.isEOT()) {
                //     System.out.println("DONE!");
                //     done = true;
                // }

                if (finalProduct.isACK()) { // if msg is ACK add to ackList
                    synchronized(ackList) {
                            ackList.add(finalProduct);
                    }
                    // System.out.print(portNumber + " : Received ACK: ");
                    // finalProduct.printMsg();
                }
                // else if (finalProduct.isEOT1()) {
                //     if (!done) {
                //         // System.out.print(" done: " + done + "
                //         //  Received EOT: ");
                //         // finalProduct.printMsg();
                //         toAck.add(finalProduct);
                //     }
                //     done = true;
                // }
                else { // else check to see if its already been received
                    // System.out.print(portNumber + ": Received msg: ");
                    // finalProduct.printMsg();
                    if (!receivedMsgs.containsKey(finalProduct.getProcessID())) {
                        rcr.rreceive(finalProduct);
                        // first time a message was received.
                        receivedMsgs.put(finalProduct.getProcessID(), 1);
                    }
                    toAck.add(finalProduct);
                }
            }
        } catch (IOException e) {
            System.err.println("Init error: ServerThread()--"+portNumber);
            System.err.println(e);
            System.exit(1);
        }
        System.out.println("receiver FIN.");
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

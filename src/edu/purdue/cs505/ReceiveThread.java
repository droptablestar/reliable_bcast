package edu.purdue.cs505;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.PriorityQueue;

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
    private PriorityQueue<Integer> receivedMsgs;

    private boolean done;
    
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
        this.receivedMsgs = new PriorityQueue<Integer>();
        this.stopped = false;
        this.done = false;
    } // ReceiveThread()

    /** Main method for receive thread. Waits for new messages then passes
     * each received message to the callback function for processing.
     */
    public void run() {
        try {
            this.socket = new DatagramSocket(portNumber);
            this.socket.setSoTimeout(500);
            while (!stopped) {
                System.out.println("listening....." + portNumber);
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
                // if (finalProduct.isEOT()) {
                //     System.out.println("DONE!");
                //     done = true;
                // }

                if (finalProduct.isACK()) { // if msg is ACK add to ackList
                    synchronized(ackList) {
                        // if (ackList.size() <= 2000)
                            ackList.add(finalProduct);
                    }
                    // System.out.print("Received ACK: ");
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
                    // System.out.print("Received msg: ");
                    // finalProduct.printMsg();
                    if (!receivedMsgs.contains(finalProduct.getMessageID())) {
                        rcr.rreceive(finalProduct);
                        // first time a message was received.
                        receivedMsgs.offer(finalProduct.getMessageID());
                    }
                    toAck.add(finalProduct);
                }
            }
        } catch (IOException e) {
            System.err.println("Init error: ServerThread()");
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

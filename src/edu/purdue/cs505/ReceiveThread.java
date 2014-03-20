package edu.purdue.cs505;

import java.io.*;
import java.net.*;
import java.util.List;

public class ReceiveThread extends Thread {
    /** Socket to use for communication. */
    private DatagramSocket socket;

    /** Port number to bind to. */
    private int portNumber;

    /** RChannelReceiver object to use as a callback. */
    private RChannelReceiver rcr;
    
    /** List of ACKs received. Shared across threads. */
    private List<RMessage> ackList;

    /** List of ACKs to be sent. Shared across threads. */
    private List<RMessage> toAck;

    /** Value determining whether or not to continue execution. */
    private boolean stopped;

    private boolean done;
    
    /** Constructor for the receive thread. 
     *
     * @param portNumber port to bind to.
     * @param rcr RChannelReceiver callback object.
     * @param ackList list of ACKs received. Modified by rcr.
     * @param toAck list of ACKs to be sent. Modified by rcr.
     */
    public ReceiveThread(int portNumber, RChannelReceiver rcr,
                         List<RMessage> ackList, List<RMessage> toAck) {
        this.portNumber = portNumber;
        this.rcr = rcr;
        this.rcr.setAckList(ackList);
        this.rcr.setToAck(toAck);
        this.stopped = false;
        this.done = false;
    } // ReceiveThread()

    /** Main method for receive thread. Waits for new messages then passes
     * each received message to the callback function for processing.
     */
    public void run() {
        try {
            this.socket = new DatagramSocket(portNumber);
            while (!stopped) {
                byte[] buf = new byte[65536];
                DatagramPacket packet =
                    new DatagramPacket(buf, buf.length);

                socket.receive(packet);
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

		// build an RMessage out of this bidniss
		RMessage finalProduct = new RMessage();
                finalProduct.setMessageContents(msg);
                // if (finalProduct.isEOT()) {
                //     System.out.println("DONE!");
                //     done = true;
                // }
                rcr.rreceive(finalProduct);
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
        System.out.println("TA: "+ rcr.toAck.size());
        return done;
    }
}

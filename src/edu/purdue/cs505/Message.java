package edu.purdue.cs505;

import java.util.Arrays;
import java.net.*;

public class Message {
    /** Constant used to identify that this message is an ACK. */
    private final String ACK = "0";

    /** Constant used to identify that this message is NOT an ACK. */
    private final String NACK = "1";

    /** Constant used to identify that this message is EOT phase 1. */
    private final String EOT1 = "-1";

    /** Constant used to identify that this message is EOT phase 2. */
    private final String EOT2 = "-2";

    /** Constant used to identify that this message is EOT phase 3. */
    private final String EOT3 = "-3";

    /** Time used to determine when this message was placed in the queue. */
    private long timeout;

    /** Unique ID associated with this message. */
    private static int seqNum = 0;

    /** String value associated with this message. */
    private String contents;

    private int messageNumber;

    private String processID;

    private InetAddress sourceIP;
    private int sourcePort;
    
    /** Constructor which sets the string contents to nothing. */
    public Message() { this.contents = ""; }

    /** Constructor which sets the contents to a string and inserts a timeout
     * value as well as the NACK value.
     *
     * @param contents string contents of the message.
     */
    public Message(String contents, Process p) {
        this.timeout = System.currentTimeMillis();
        this.processID = p.getIP() + ":" + p.getPort();
        this.contents = NACK + "" + (seqNum++) + ":" +
            this.processID + ":" + contents;
    } // Message()

    /** Get the entire message contents, type, seqNum, and payload.
     * @return string version of entire message.
     */
    public String getContents() {
        return contents;
    } // getMessageContents()

    
    /** Sets the message contents.
     * @param contents string value to set this message to.
     */
    public void setContents(String contents) {
        this.contents = contents;
    } // setMessageContents()

    /** Determines if this message is an ACK or not.
     * @return true if this message is an ACK, otherwise false
     */
    public boolean isACK() {
        return contents.substring(0,1).equals(ACK) ? true : false;
    } // isACK()

    /** Gets the unique ID associated with this contents.
     * @return integer value corresponding to this messages sequence number.
     */
    public int getMessageID() {
        return Integer.parseInt(contents.substring(1, contents.indexOf(':')));
    } // getMessageID()

    /** Turns this message into an ACK. */
    public void makeACK() {
        contents = ACK + "" + contents.substring(1,contents.indexOf(':') + 1);
    } // makeACK()

    /** Turns this message into an ACK. */
    public Message makeEOT(int phase) {
        if (phase == 1)
            contents = EOT1+""+contents.substring(1,contents.indexOf(':')+1);
        else if (phase == 2)
            contents = EOT2+""+contents.substring(1,contents.indexOf(':')+1);
        else if (phase == 3)
            contents = EOT3+""+contents.substring(1,contents.indexOf(':')+1);
        return this;
    } // makeEOT()
    
    /** Determines if this message is an EOT phase 1 or not.
     * @return true if this message is an EOT, otherwise false
     */
    public boolean isEOT1() {
        return contents.substring(0,2).equals(EOT1) ? true : false;
    } // isEOT1()

    /** Determines if this message is an EOT phase 2 or not.
     * @return true if this message is an EOT, otherwise false
     */
    public boolean isEOT2() {
        return contents.substring(0,2).equals(EOT2) ? true : false;
    } // isEOT2()

    /** Determines if this contents is an EOT phase 3 or not.
     * @return true if this message is an EOT, otherwise false
     */
    public boolean isEOT3() {
        return contents.substring(0,2).equals(EOT3) ? true : false;
    } // isEOT3()

    /** Prints a message. Used for debugging. */
    public void printMsg() {
        System.out.println("["+contents+"]");
    } // printMsg()

    /** Updates the timeout value of this message to the current system
     * time. */
    public void setTimeout() {
        this.timeout = System.currentTimeMillis();
    } // setTimeout()

    /** Returns the current timeout value for this message.
     * @return long value associated with the timeout corresponding to this
     * message. */
    public long getTimeout() { return this.timeout; }

    /** Parses the message to just obtain the payload.
     * @return string value of the payload of this message.
     */
    public String getMessageString() {
        int start=0;
        for (int i=0; i<3; i++) 
            start = contents.indexOf(':', start) + 1;
        return contents.substring(start);
    }

    public int getMessageNumber() { return messageNumber; }

    public void setMessageNumber(int messageNumber) {
        this.messageNumber = messageNumber;
    }

    public String getProcessID() { return processID; }
    public void setProcessID(String processID) { this.processID = processID; }

    public int getSourcePort() {
        int start = 0; int end = 0;
        for (int i=0; i<2; i++) 
            start = contents.indexOf(':', start) + 1;
        end = contents.indexOf(':', start);
        return Integer.parseInt(contents.substring(start, end));
    }

    public void setSourcePort(int sourcePort) { this.sourcePort = sourcePort; }

    public InetAddress getSourceIP() {
        int start = 0; int end = 0;
        start = contents.indexOf(':', start) + 1;
        end = contents.indexOf(':', start);

        InetAddress IP = null;
        try {
            return InetAddress.getByName(contents.substring(start, end));
        }
        catch(UnknownHostException e) {
            System.out.print("Couldn't get sourceIP!");
            System.out.println(e);
            System.exit(1);
        }
        return IP;
    }
    public void setSourceIP(InetAddress sourceIP) { this.sourceIP = sourceIP; }

    
}

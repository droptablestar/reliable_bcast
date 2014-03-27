package edu.purdue.cs505;

import java.net.*;

public class Header {
    /** Constant used to identify that this message is an ACK. */
    public static final String types[] = {"0", "1", "2", "-1", "-2", "-3"};

    /** Constant used to identify that this message is an ACK. */
    public static final int ACK = 0;

    /** Constant used to identify that this message is NOT an ACK. */
    public static final int NACK = 1;

    /** Constant used to identify that this message is EOT phase 1. */
    public static final int EOT1 = 2;

    /** Constant used to identify that this message is EOT phase 2. */
    public static final int EOT2 = 3;

    /** Constant used to identify that this message is EOT phase 3. */
    public static final int EOT3 = 4;
    
    /** Global sequence associated with all messages. */
    private static int seqNum = 0;

    private int thisSeqNum = 0;

    /** Time used to determine when this message was placed in the queue. */
    private long timeout;

    private String sourceIP;
    private int sourcePort = 0;
    
    private String destIP;
    private int destPort = 0;
    
    private int typeOfMessage;

    public Header(String sourceIP, int sourcePort,
                  String destIP, int destPort,
                  int typeOfMessage, int thiSeqNum) {
        // try {
            // this.sourceIP = InetAddress.getByName(sourceIP);
            // this.destIP = InetAddress.getByName(destIP);
            this.sourceIP = sourceIP;
            this.sourcePort = sourcePort;
        // }
        // catch (UnknownHostException e) {
        //     System.out.println("Error getting IP for header: "+sourceIP +
        //                        " --- " + destIP);
        //     System.out.println(e);
        //     System.exit(1);
        // }
        this.sourcePort = sourcePort;
        this.destPort = destPort;
        this.typeOfMessage = typeOfMessage;
        this.thisSeqNum = thisSeqNum;
    }

    public Header(String IP, int port, int typeOfMessage, long timeout) {
        // try {
            // this.sourceIP = InetAddress.getByName(IP);
            this.sourceIP = IP;
        // }
        // catch (UnknownHostException e) {
        //     System.out.println("Error getting IP for header: "+IP);
        //     System.out.println(e);
        //     System.exit(1);
        // }
        this.sourcePort = port;
        this.typeOfMessage = typeOfMessage;
        this.thisSeqNum = seqNum++;
    }

    /** Updates the timeout value of this message to the current system
     * time. */
    public void setTimeout() { this.timeout = System.currentTimeMillis(); }

    public void setTimeout(long timeout) { this.timeout = timeout; }
    public long getTimeout() { return this.timeout; }

    public int getSourcePort() { return this.sourcePort; }
    public void setSourcePort(int sourcePort) { this.sourcePort = sourcePort; }

    public String getSourceIP() { return this.sourceIP; }
    public void setSourceIP(String sourceIP) { this.sourceIP = sourceIP; }

    public int getDestPort() { return this.destPort; }
    public void setDestPort(int destPort) { this.destPort = destPort; }

    public String getDestIP() { return this.destIP; }
    public void setDestIP(String destIP) { this.destIP = destIP; }

    public int getTypeOfMessage() { return this.typeOfMessage; }
    public void setTypeOfMessage(int type) { this.typeOfMessage = type; }

    public String toString() {
        return types[typeOfMessage]+""+thisSeqNum+":"+
            sourceIP.toString()+":"+sourcePort+ ":"+
            destIP+":"+destPort;
    }
}
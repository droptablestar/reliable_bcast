package edu.purdue.cs505;

import java.net.*;

public class Header {
    /** Constant used to identify that this message is an ACK. */
    public static final String types[] = {"0", "1", "2"};

    /** Constant used to identify that this message is an ACK. */
    public static final int ACK = 0;

    /** Constant used to identify that this message is NOT an ACK. */
    public static final int NACK = 1;

    /** Constant used to identify that this message is an ACK. */
    public static final int OBS = 2;

    /** Global sequence associated with all messages. */
    private static int seqNum = 0;

    private int thisSeqNum = 0;

    /** Time used to determine when this message was placed in the queue. */
    private long timeout;

    private String sourceIP;
    private int sourcePort = 0;
    
    private String destIP;
    private int destPort = 0;

    private String processID;
    
    private int typeOfMessage;

    public Header(String sourceIP, int sourcePort,
                  String destIP, int destPort,
                  int typeOfMessage, int thisSeqNum) {
        this.sourceIP = sourceIP;
        this.sourcePort = sourcePort;
        this.destIP = destIP;
        this.destPort = destPort;
        this.typeOfMessage = typeOfMessage;
        this.thisSeqNum = thisSeqNum;
        this.processID = thisSeqNum+":"+sourceIP+":"+sourcePort;
    }

    /* This is for creating a new message */
    public Header(String IP, int port, int typeOfMessage, long timeout) {
        this.sourceIP = IP;
        this.sourcePort = port;
        this.typeOfMessage = typeOfMessage;
        this.thisSeqNum = seqNum++;
        this.processID = thisSeqNum+":"+IP+":"+port;
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

    public int getSeqNum() { return this.thisSeqNum; }
    public void setSeqNum(int sNum) { this.thisSeqNum = sNum; }

    public String getProcessID() { return this.processID; }
    public void setProcessID(int sNum, String IP, int port) { this.processID = sNum + ":" + IP + ":" +port; }
    public void setProcessID(String processID) { this.processID = processID; }

    public String toString() {
        return types[typeOfMessage] + "" + thisSeqNum + ":"
            + sourceIP.toString() + ":" + sourcePort + ":"
            + destIP + ":" + destPort + ":" + processID;
    }
}

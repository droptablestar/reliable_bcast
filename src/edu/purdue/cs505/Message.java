package edu.purdue.cs505;

import java.util.Arrays;
import java.net.*;

public class Message {
    /** String value associated with this message. */
    private String contents;

    private Header header;
    
    /** Constructor which sets the string contents to nothing. */
    public Message() { this.contents = ""; }

    public Message(String sourceIP, int sourcePort,
                   String destIP, int destPort,
                   int typeOfMessage, int thisSeqNum, String contents) {
        
         this.header = new Header(sourceIP, sourcePort, destIP, destPort,
                                  typeOfMessage, thisSeqNum);
         this.contents = contents;
    }

    /** Constructor which sets the contents to a string and inserts a timeout
     * value as well as the NACK value.
     *
     * @param contents string contents of the message.
     */
    public Message(String contents, Process p) {
        this.header = new Header(p.getIP(), p.getPort(),
                                 Header.NACK, System.currentTimeMillis());
        this.contents = contents;
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
        return header.getTypeOfMessage() == Header.ACK;
    } // isACK()

    public boolean isOBS() {
        return header.getTypeOfMessage() == Header.OBS;
    } // isACK()

    /** Turns this message into an ACK. */
    public void makeACK(String destIP, int destPort) {
        header.setTypeOfMessage(Header.ACK);
        header.setDestIP(destIP);
        header.setDestPort(destPort);
        contents = header.toString();
    }

    public void makesObsolete(int seqNum) {
        header.setTypeOfMessage(Header.OBS);
        header.setSeqNum(seqNum);
    }

    /** Prints a message. Used for debugging. */
    public void printMsg() {
        System.out.println("["+header.toString()+":"+contents+"]");
    } // printMsg()


    /** Parses the message to just obtain the payload.
     * @return string value of the payload of this message.
     */
    public String getMessageString() {
        int start=0;
        for (int i=0; i<3; i++) 
            start = contents.indexOf(':', start) + 1;
        return contents.substring(start);
    }

    public int getMessageNumber() { return header.getSeqNum(); }
    public void setMessageNumber(int num) { header.setSeqNum(num); }

    public int getTypeOfMessage() { return header.getTypeOfMessage(); }
    public void setTypeOfMessage(int type) { header.setTypeOfMessage(type); }

    public int getSourcePort() { return header.getSourcePort(); }
    public void setSourcePort(int port) { header.setSourcePort(port); }

    public String getSourceIP() { return header.getSourceIP(); }
    public void setSourceIP(String IP) { header.setSourceIP(IP); }

    public int getDestPort() { return header.getDestPort(); }
    public void setDestPort(int port) { header.setDestPort(port); }

    public String getDestIP() { return header.getDestIP(); }
    public void setDestIP(String IP) { header.setDestIP(IP); }

    public long getTimeout() { return header.getTimeout(); }
    public void setTimeout() { header.setTimeout(); }

    public int getSeqNum() { return header.getSeqNum(); }
    public void setSeqNum(int seqNum) { header.setSeqNum(seqNum); }

    public String getProcessID() { return header.getProcessID(); }
    public void setProcessID(int seqNum, String IP, int port) { header.setProcessID(seqNum, IP, port); }
    public void setProcessID(String processID) { header.setProcessID(processID); }

    public String getDestID() {
        return header.getSeqNum() + ":" + header.getDestIP() + ":"
            + header.getDestPort();
    }

    public void stripHeader() {
        int type, seq;
        int firstColon = contents.indexOf(':');
        type = Integer.parseInt(contents.substring(0,1));
        seq = Integer.parseInt(contents.substring(1,firstColon));

        String splitString[] = contents.split(":");
        header = new Header(splitString[1], Integer.parseInt(splitString[2]),
                            splitString[3], Integer.parseInt(splitString[4]),
                            type, seq);

        StringBuffer tempContents = new StringBuffer();
        for (int i=8; i<splitString.length; i++)
            tempContents.append(splitString[i]);
        contents = tempContents.toString();
        header.setProcessID(Integer.parseInt(splitString[5]),
	                    splitString[6],Integer.parseInt(splitString[7]));
    }
    
    public void toSend(String destIP, int destPort) {
        header.setDestIP(destIP);
        header.setDestPort(destPort);
    }

    public String headerToString() { return header.toString(); }
}

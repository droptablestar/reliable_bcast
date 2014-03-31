package edu.purdue.cs505;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class FRBroadcast implements FIFOReliableBroadcast {
    private ArrayList<Process> processList;
    private Process currentProcess;
    private RChannel channel;
    private BReceiveThread brThread;
    private BlockingQueue<Message> receivedQueue; 

    public static boolean srbOn = true;
    public static long deliveryDelay = 1000;
    
    public FRBroadcast() {
        processList = new ArrayList<Process>();
        receivedQueue = new LinkedBlockingQueue<Message>();
    }
    
    public void init(Process currentProcess) {
        this.currentProcess = currentProcess;
        channel = new RChannel(currentProcess.getPort(), receivedQueue);
        channel.init("localhost", currentProcess.getPort());
    }

    public void addProcess(Process p) {
        processList.add(p);
    }

    public void rbroadcast(Message m) {
	for (Iterator<Process> pi=processList.iterator(); pi.hasNext(); ) {
            Process p = pi.next();
            Message m2 = new Message(m.getSourceIP(), m.getSourcePort(),
                                     p.getIP(), p.getPort(),
                                     m.getTypeOfMessage(), m.getSeqNum(),
                                     m.getContents());
            m2.setProcessID(m.getSeqNum(),m.getSourceIP(),m.getSourcePort());
            channel.rsend(m2);
        }
        Message m2 = new Message(m.getSourceIP(), m.getSourcePort(),
                                 currentProcess.getIP(), currentProcess.getPort(),
                                 m.getTypeOfMessage(), m.getSeqNum(),
                                 m.getContents());
        m2.setProcessID(m.getSeqNum(),m.getSourceIP(),m.getSourcePort());
        channel.rsend(m2);
    }

    public void rblisten(BroadcastReceiver m) {
        channel.rlisten(new RChannelReceiver(receivedQueue));
        brThread = new BReceiveThread((BcastReceiver)m, receivedQueue,
                                      processList, channel, true);
        brThread.start();
    }

    public void printProcesses() {
        for (Iterator<Process> pi=processList.iterator(); pi.hasNext(); ) 
            System.out.println((pi.next()).getID());
    }

    public void halt() {
    }

    public void haltR() {
        channel.haltR();
    }
}

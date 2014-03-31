package edu.purdue.cs505;

// import java.util.Comparator;
// import java.util.PriorityQueue;
// import java.util.Collections;
// import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RBroadcast implements ReliableBroadcast {
    private ArrayList<Process> processList;
    private Process currentProcess;
    private RChannel channel;
    private BReceiveThread brThread;
    private BlockingQueue<Message> receivedQueue; 

    public RBroadcast() {
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
        try { receivedQueue.put(m2); }
        catch (InterruptedException e) {
            System.out.println("Error putting into receivedQueue [rbroadcast]");
            System.out.println(e);
        }
    }

    public void rblisten(BroadcastReceiver m) {
        channel.rlisten(new RChannelReceiver(receivedQueue));
        brThread = new BReceiveThread((BcastReceiver)m, receivedQueue,
                                      processList, channel, false);
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

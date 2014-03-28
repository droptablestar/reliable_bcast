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
        RChannel rc = new RChannel(currentProcess.getPort());
        rc.init(p.getIP(), p.getPort());
        processList.add(p);
    }

    public void rbroadcast(Message m) {
	for (Iterator<Process> pi=processList.iterator(); pi.hasNext(); ) {
            Process p = pi.next();
            Message msg = new Message(m.getContents(), currentProcess);
            System.out.println("Broadcasting: " + msg.getContents() +
                               " to: " + p.getIP() + ":" + p.getPort());
            msg.toSend(p.getIP(), p.getPort());
            msg.printMsg();
            channel.rsend(msg);
        }
    }

    public void rblisten(BroadcastReceiver m) {
        channel.rlisten(new RChannelReceiver(receivedQueue));
        brThread = new BReceiveThread((BcastReceiver)m);
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

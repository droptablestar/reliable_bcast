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
        // for (Process p : processList) {
        //     Message msg = new Message(m.getSourceIP(), m.getSourcePort(),
        //                               p.getIP(), p.getPort(),
        //                               m.getTypeOfMessage(), m.getSeqNum(),
        //                               m.getContents());
        //     // System.out.println("Broadcasting: " + m2.getContents() +
        //     //                    " to: " + p.getIP() + ":" + p.getPort());
        //     // m2.printMsg();
        //     channel.rsend(msg);
        // }
	for (Iterator<Process> pi=processList.iterator(); pi.hasNext(); ) {
            Process p = pi.next();
            Message m2 = new Message(m.getSourceIP(), m.getSourcePort(),
                                     p.getIP(), p.getPort(),
                                     m.getTypeOfMessage(), m.getSeqNum(),
                                     m.getContents());
            channel.rsend(m2);
            break;
        }
        Message m2 = new Message(m.getSourceIP(), m.getSourcePort(),
                                 currentProcess.getIP(), currentProcess.getPort(),
                                 m.getTypeOfMessage(), m.getSeqNum(),
                                 m.getContents());
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

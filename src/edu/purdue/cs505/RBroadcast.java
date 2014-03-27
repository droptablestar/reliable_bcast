package edu.purdue.cs505;

// import java.util.Comparator;
// import java.util.PriorityQueue;
// import java.util.Collections;
// import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class RBroadcast implements ReliableBroadcast {
    private ArrayList<Process> processList;
    private ArrayList<RChannel> channels;
    private Process currentProcess;
    private RChannel receiverDummy;
    
    public RBroadcast() {
        processList = new ArrayList<Process>();
        channels = new ArrayList<RChannel>();
    }
    
    public void init(Process currentProcess) {
        this.currentProcess = currentProcess;
        receiverDummy = new RChannel(currentProcess.getPort());
        receiverDummy.init("localhost", currentProcess.getPort());
    }

    public void addProcess(Process p) {
        RChannel rc = new RChannel(currentProcess.getPort());
        rc.init(p.getIP(), p.getPort());
        channels.add(rc);
        processList.add(p);
    }

    public void rbroadcast(Message m) {
        for (Iterator<Process> pi=processList.iterator(); pi.hasNext(); ) {
            Process p = pi.next();
            System.out.println("Broadcasting: " + m.getContents() +
                               " to: " + p.getIP() + ":" + p.getPort());
            
        }
	for (Iterator<RChannel> ci=channels.iterator(); ci.hasNext(); )
            ci.next().rsend(m);
    }

    public void rblisten(BroadcastReceiver m) {
        receiverDummy.rlisten(new RChannelReceiver());
        System.out.println("Listening: " + currentProcess.getIP() + " : " +
                           currentProcess.getPort());
    }

    public void printProcesses() {
        for (Iterator<Process> pi=processList.iterator(); pi.hasNext(); ) 
            System.out.println((pi.next()).getID());
    }

    public void halt() {
        for (Iterator<RChannel> ci=channels.iterator(); ci.hasNext(); )
            ci.next().halt();
    }

    public void haltR() {
        receiverDummy.haltR();
    }
}

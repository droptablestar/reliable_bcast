package edu.purdue.cs505;
public interface ReliableBroadcast {
    void init(Process currentProcess); 
    void addProcess(Process p); 
    void rbroadcast(Message m); 
    void rblisten(BroadcastReceiver m); 
}

package edu.purdue.cs505;
public interface ReliableBroadcast {
    void init(Process currentProcess); // Initiate the broadcast channel
    void addProcess(Process p); // Add a process to the group
    void rbroadcast(Message m); // Broadcast a message
    void rblisten(BroadcastReceiver m); // User­callback for delivering msgs
}
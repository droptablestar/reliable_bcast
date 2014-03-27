package edu.purdue.cs505;

public interface BroadcastReceiver {
    // Channel users should implement this for handling delivered messages
    void rdeliver(Message m); 
}
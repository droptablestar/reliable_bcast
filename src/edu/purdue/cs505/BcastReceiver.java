package edu.purdue.cs505;

public class BcastReceiver implements BroadcastReceiver {
    public void rdeliver(Message m) {
        System.out.print("Received: "); m.printMsg();
    }
}

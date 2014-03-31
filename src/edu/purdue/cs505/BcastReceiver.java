package edu.purdue.cs505;

import java.io.PrintWriter;
import java.io.FileNotFoundException;

public class BcastReceiver implements BroadcastReceiver {
    private PrintWriter wr;

    public BcastReceiver(int id) {
        try {
            this.wr = new PrintWriter("tests/"+id+".out");
        } catch(FileNotFoundException e) {
            System.err.println("File not found: " + e);
            System.exit(1);
        }
    }

    public void rdeliver(Message m) {
        System.out.print(m.getDestIP()+":"+m.getDestPort()+" received: ");
        m.printMsg();

        wr.println(m.getMessageString());
        wr.flush();
    }
}

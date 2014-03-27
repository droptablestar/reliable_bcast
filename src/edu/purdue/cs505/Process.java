package edu.purdue.cs505;

public class Process {
    private String IP; //IP of the process
    private int port; //Port of the process

    public Process(String IP, int port) {
        this.IP = IP;
        this.port = port;
    }

    public String getIP() { return IP; }
    public int getPort() { return port; }
    public String getID() { return getIP() + ":" + getPort(); }
}
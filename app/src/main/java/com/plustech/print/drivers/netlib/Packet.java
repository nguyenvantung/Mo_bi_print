package com.plustech.print.drivers.netlib;
/*
 * Copyright 2013 NVan
 */


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


/**
 * Encapsulate packet details that we are interested in.
 * @author NVan
 */
public class Packet {
    public InetAddress src;
    public int srcPort;
    public InetAddress dst;
    public int dstPort;
    public String description;
    public String type;
    public String name;
    public String printState;

    public Packet() {}
    public Packet(DatagramPacket dp, DatagramSocket socket) {
        src = dp.getAddress();
        srcPort = dp.getPort();
        dst = socket.getLocalAddress();
        dstPort = socket.getLocalPort();
        printState = socket.isConnected() ? "Ready" : "Off";
    }
}

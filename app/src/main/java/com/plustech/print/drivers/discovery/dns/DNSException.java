/*
 * Copyright 2013 NVan
 */
package com.plustech.print.drivers.discovery.dns;

public class DNSException extends RuntimeException {
    private static final long serialVersionUID = 372670807060894755L;
    
    public DNSException() {}
    public DNSException(String message) {
        super(message);
    }
    public DNSException(Throwable e) {
        super(e);
    }
    public DNSException(String message, Throwable e) {
        super(message, e);
    }
    
}

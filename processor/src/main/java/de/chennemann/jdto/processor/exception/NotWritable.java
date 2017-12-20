package de.chennemann.jdto.processor.exception;



public class NotWritable extends RuntimeException {
    
    
    public NotWritable() {
        super();
    }
    
    
    public NotWritable(final String message) {
        super(message);
    }
    
    
    public NotWritable(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    
    public NotWritable(final Throwable cause) {
        super(cause);
    }
}

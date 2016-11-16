package com.ibm.nas.exception;


public class LMSException extends DAOException {

    public LMSException(String message) {
        super(message);
    }

    public LMSException(String message, Throwable throwable) {
        super(message,throwable);
    }

}

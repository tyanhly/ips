package com.kiss.markov;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;

@SuppressLint("UseValueOf")
public class MarkovException extends Exception {

    public static int MARKOV_DATA_NOT_IN_RANGE = 0x001;

    public static int MARKOV_SYSTEM_DONT_KNOW = 0x101;

    public static Map<Integer, String> msgs;

    static {
        msgs = new HashMap<Integer, String>();
        msgs.put(new Integer(MARKOV_DATA_NOT_IN_RANGE),
                "MARKOV_DATA_NOT_IN_RANGE");
        msgs.put(new Integer(MARKOV_SYSTEM_DONT_KNOW),
                "MARKOV_SYSTEM_DONT_KNOW");
    }
    
    public MarkovException(String msg) {
        super(msg);
    }

    public MarkovException(String name, Throwable cause) {
        super(name, cause);
    }

    public MarkovException(Exception cause) {
        super(cause);
    }

    private static final long serialVersionUID = 1L;

}

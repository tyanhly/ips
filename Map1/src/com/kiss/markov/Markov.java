package com.kiss.markov;

import java.math.BigDecimal;

public class Markov {
    protected MStatusInit statusSet;
    protected MDataInit dataInits;
    protected MDataFilter dataFilter;
    
    public Markov(){
        statusSet = new MStatusInit();
        dataInits = new MDataInit();
    }
    
    
}

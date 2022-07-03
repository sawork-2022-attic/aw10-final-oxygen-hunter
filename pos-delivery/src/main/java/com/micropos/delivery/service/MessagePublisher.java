package com.micropos.delivery.service;

import com.micropos.delivery.model.Entry;

public class MessagePublisher {

    private MessageListener listener = null;

    public void subscribe(MessageListener listener){
        this.listener = listener;
    }

    public void remove(){
        this.listener = null;
    }

    public void publish(Entry entry){
        if (listener != null) {
            listener.onNext(entry);
        }
    }
}

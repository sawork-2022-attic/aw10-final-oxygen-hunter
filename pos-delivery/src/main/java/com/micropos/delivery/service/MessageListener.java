package com.micropos.delivery.service;

import com.micropos.delivery.model.Entry;

public interface MessageListener {
    void onNext(Entry info);

    void onComplete();
}
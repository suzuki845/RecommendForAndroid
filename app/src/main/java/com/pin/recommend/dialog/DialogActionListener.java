package com.pin.recommend.dialog;

public interface DialogActionListener<T> {
    void onDecision(T dialog);
    void onCancel();
}

package com.pin.recommend.ui.component;

public interface DialogActionListener<T> {
    void onDecision(T dialog);

    void onCancel();
}

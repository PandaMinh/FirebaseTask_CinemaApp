package com.exapmle.firebase.firebase;

public interface DataCallback<T> {
    void onSuccess(T data);

    void onError(String message);
}

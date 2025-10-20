package org.edward.pandora.common.tcp;

public interface FutureListener {
    void onComplete() throws Exception;

    void onError(Throwable cause);
}
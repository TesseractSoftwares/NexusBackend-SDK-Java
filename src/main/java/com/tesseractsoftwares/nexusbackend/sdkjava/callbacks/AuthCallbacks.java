package com.tesseractsoftwares.nexusbackend.sdkjava.callbacks;

public class AuthCallbacks {
    @FunctionalInterface
    public interface OnSuccess {
        void execute(String message);
    }

    @FunctionalInterface
    public interface OnInvalidCredentials {
        void execute(String message);
    }

    @FunctionalInterface
    public interface OnError {
        void execute(Exception exception);
    }
}

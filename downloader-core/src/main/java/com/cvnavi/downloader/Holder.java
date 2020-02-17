package com.cvnavi.downloader;

import java.io.Serializable;

public final class Holder<T> implements Serializable {

    private static final long serialVersionUID = 2623699057546497185L;

    /**
     * The value contained in the holder.
     */
    private T value;

    /**
     * Creates a new holder with a <code>null</code> value.
     */
    public Holder() {
    }

    /**
     * Create a new holder with the specified value.
     *
     * @param value The value to be stored in the holder.
     */
    public Holder(T value) {
        this.value = value;
    }

    public T get(){
        return value;
    }

    public void set(T value){
        this.value=value;
    }
}
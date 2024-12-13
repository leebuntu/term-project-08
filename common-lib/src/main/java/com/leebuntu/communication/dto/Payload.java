package com.leebuntu.communication.dto;

import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class Payload<T> {
    private transient final Type type;
    private transient final Gson gson = new Gson();

    public Payload() {
        this.type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public Type getType() {
        return type;
    }

    public Gson getGson() {
        return gson;
    }

    public T fromJson(String json) {
        return gson.fromJson(json, type);
    }

    public String toJson() {
        return gson.toJson(this, type);
    }
}

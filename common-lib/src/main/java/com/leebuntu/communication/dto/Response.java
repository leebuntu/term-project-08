package com.leebuntu.communication.dto;


import com.leebuntu.communication.dto.enums.Status;

public class Response extends Payload<Response> {

    protected Status status;
    protected String message;

    public Response(Status status, String message) {
        super();
        this.status = status;
        this.message = message;
    }

    public Response() {
        super();
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public Response fromJson(String json) {
        Response response = super.fromJson(json);
        this.status = response.status;
        this.message = response.message;
        return this;
    }
}

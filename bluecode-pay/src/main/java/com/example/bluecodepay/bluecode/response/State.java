package com.example.bluecodepay.bluecode.response;

import backend.template.entity.Status;

public enum State {

    APPROVED(Status.SUCCESS), //The transaction was successfully performed.
    DECLINED(Status.ERROR), //The transaction failed.
    CANCELLED(Status.CANCELLED), //The transaction has been cancelled (using the /cancel endpoint).
    REFUNDED(Status.REFUNDED),//The transaction has been completely refunded (using the /refund endpoint)
    PROCESSING(Status.PENDING);

    private final Status correspondingStatus;


    State(Status status){
        this.correspondingStatus = status;
    }

    public Status getCorrespondingStatus(){
        return correspondingStatus;
    }

}

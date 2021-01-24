package com.rdvdev2.timetravelmod.impl.common.timemachine.exception;

import com.rdvdev2.timetravelmod.impl.common.timemachine.TimeMachineExecutor;

public class TimeMachineExecutionException extends Exception {

    private final TimeMachineExecutor.TimeMachineError error;

    public TimeMachineExecutionException(TimeMachineExecutor.TimeMachineError error) {
        this.error = error;
    }

    public TimeMachineExecutor.TimeMachineError getError() {
        return error;
    }
}

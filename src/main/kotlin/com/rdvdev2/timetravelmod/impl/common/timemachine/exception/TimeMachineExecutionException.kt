package com.rdvdev2.timetravelmod.impl.common.timemachine.exception

import com.rdvdev2.timetravelmod.impl.common.timemachine.TimeMachineExecutor.TimeMachineError

class TimeMachineExecutionException(val error: TimeMachineError) : RuntimeException()
package com.rdvdev2.timetravelmod.api.timemachine.block

import com.rdvdev2.timetravelmod.api.timemachine.ITimeMachineUpgrade
import net.minecraft.block.Block

class TimeMachineUpgradeBlock(settings: Settings?, val upgrade: ITimeMachineUpgrade) : Block(settings)
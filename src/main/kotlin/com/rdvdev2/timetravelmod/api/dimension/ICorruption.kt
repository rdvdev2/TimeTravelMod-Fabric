package com.rdvdev2.timetravelmod.api.dimension

interface ICorruption {

    fun increaseCorruptionLevel(amount: Int): Int

    fun setCorruptionLevel(value: Int): Int

    val corruptionLevel: Int
}
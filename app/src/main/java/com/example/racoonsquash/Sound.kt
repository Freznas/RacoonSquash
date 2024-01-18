package com.example.racoonsquash
// Hanterar ljud
interface Sound {

    fun play(id: Int)
    fun releaseResource()
    fun stop()

}
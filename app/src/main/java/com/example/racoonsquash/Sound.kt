package com.example.racoonsquash

interface Sound {

    fun play(id: Int)
    fun releaseResource()
    fun stop()

}
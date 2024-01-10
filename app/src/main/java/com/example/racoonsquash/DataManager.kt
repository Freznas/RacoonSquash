package com.example.racoonsquash

interface DataManager {
    /**
     * Returnerar poang fran alla spel
     */
    fun getAllScores(): List<Score>

    /**
     * Returns all the scores for the given Game
     */
    fun getAllScores(game: Game): List<Score>

    /**
     * Sparar nya poang
     */
    fun addNewScore(score: Score)


    data class Score(val userName: String, val score: Int, val game: Game)

    //Talar om alla spel i appen:
    enum class Game {
        SQUASH, BREAKOUT
    }
}
package com.example.racoonsquash

import com.example.racoonsquash.DataManager.Game
import com.example.racoonsquash.DataManager.Game.PONG
import com.example.racoonsquash.DataManager.Game.SQUASH
import com.example.racoonsquash.DataManager.Score

class InMemoryDataManager : DataManager {

    private val scores: MutableList<Score> = mutableListOf(
        Score("Bibbi", 1500, SQUASH),
        Score("Bobbo", 750, SQUASH),
        Score("Dadda", 100, SQUASH),

        Score("Fantus", 1000, PONG),
        Score("Babar", 500, PONG),
        Score("Barbapapa", 100, PONG),
    )

    override fun getAllScores(): List<Score> {
        return scores
    }

    override fun getAllScores(game: Game): List<Score> {
        return scores.filter { x -> game == x.game }
    }

    override fun addNewScore(score: Score) {
        scores.add(score)
    }
}
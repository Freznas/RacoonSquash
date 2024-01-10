package com.example.racoonsquash

import com.example.racoonsquash.DataManager.Game
import com.example.racoonsquash.DataManager.Game.BREAKOUT
import com.example.racoonsquash.DataManager.Game.SQUASH
import com.example.racoonsquash.DataManager.Score

class InMemoryDataManager : DataManager {

    private val scores: MutableList<Score> = mutableListOf(
        Score("Bibbi", 1500, SQUASH),
        Score("Bobbo", 750, SQUASH),
        Score("Dadda", 100, SQUASH),

        Score("Fantus", 1000, BREAKOUT),
        Score("Babar", 500, BREAKOUT),
        Score("Barbapapa", 100, BREAKOUT),
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
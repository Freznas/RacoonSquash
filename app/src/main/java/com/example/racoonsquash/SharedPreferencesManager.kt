package com.example.racoonsquash

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesManager(context: Context) : DataManager {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)

    private val scoresKey = "scores"

    fun saveScore(userName: String, game: String, score: Int) {
        val key = "$userName-$game"
        sharedPreferences.edit().putInt(key, score).apply()
    }

    override fun getAllScores():  List<DataManager.Score> {
        val scores = mutableListOf<DataManager.Score>()

        DataManager.Game.values().iterator().forEach { game ->
            val scoreStringSet: MutableSet<String>? =
                sharedPreferences.getStringSet(game.name, setOf())

            scoreStringSet!!.forEach { scoreString ->
                val type = object : TypeToken<DataManager.Score>() {}.type
                val score = Gson().fromJson<DataManager.Score>(scoreString, type)
                scores.add(score)
            }
        }
        return scores

    }

    override fun getAllScores(game: DataManager.Game): List<DataManager.Score> {
        val scores = mutableListOf<DataManager.Score>()
        val gameKey = game.name // Antag att namnet på spelet används som nyckel

        // Antag att poängen lagras som en JSON-sträng i SharedPreferences
        val scoreStringSet = sharedPreferences.getStringSet(gameKey,  setOf())

        scoreStringSet!!.forEach { scoreString ->
            val type = object : TypeToken<DataManager.Score>() {}.type
            val score = Gson().fromJson<DataManager.Score>(scoreString, type)
            scores.add(score)
        }

        return scores
    }


    override fun addNewScore(score: DataManager.Score) {
        val scores = sharedPreferences.getStringSet(score.game.name, setOf())!!.toMutableSet()
        val scoreJson = Gson().toJson(score)

        scores!!.add(scoreJson)

        sharedPreferences.edit().putStringSet(score.game.name, scores).apply()
    }
}

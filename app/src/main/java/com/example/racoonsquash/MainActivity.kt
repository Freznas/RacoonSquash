package com.example.racoonsquash

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ExpandableListView
import androidx.appcompat.app.AppCompatActivity
import com.example.racoonsquash.databinding.ActivityMainBinding

//RacoonGames
// Medlemmar:Jörgen Hård (ProductOwner), Joakim Bjärkstedt (scrum-Master) Elin Andersson(utvecklare) Denise Cigel (Utvecklare)
class MainActivity : AppCompatActivity() {
    private lateinit var expandableListView: ExpandableListView
    private lateinit var listAdapter: TopScoreExpandableListAdapter
    private lateinit var gameList: MutableList<String>
    private lateinit var topScorePerGame: HashMap<String, List<DataManager.Score>>
    private lateinit var dataManager: DataManager

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val btnpong: Button = findViewById(R.id.btn_pong)
        val btnsquash: Button = findViewById(R.id.btn_squash)

        dataManager = InMemoryDataManager()

        btnpong.setOnClickListener {
            val intent = Intent(this, PongActivity::class.java)
            startActivity(intent)
        }
        btnsquash.setOnClickListener {
            val intent = Intent(this, SquashActivity::class.java)
            startActivity(intent)
        }

        // Initialisera ExpandableListView
        expandableListView = findViewById(R.id.expandableListView)

        // Förbered och sätt datan
        loadTopScores()
        listAdapter = TopScoreExpandableListAdapter(this, gameList, topScorePerGame)
        expandableListView.setAdapter(listAdapter)

        // Sätt upp lyssnare för grupp- och barnklick
        expandableListView.setOnGroupClickListener { parent, v, groupPosition, id ->
            // Hantera grupp klick händelser här
            false
        }

        expandableListView.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
            // Hantera barn klick händelser här
            false
        }
    }

    private fun loadTopScores() {
        //get all the games as a list of Strings
        gameList = DataManager.Game.values().map { game -> game.name }.toMutableList()

        topScorePerGame = HashMap()

        DataManager.Game.values().forEach { game ->
            topScorePerGame[game.name] = dataManager.getAllScores(game)
        }

    }
}

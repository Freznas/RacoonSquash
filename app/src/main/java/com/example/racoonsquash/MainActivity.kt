package com.example.racoonsquash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.racoonsquash.databinding.ActivityMainBinding

//RacoonGames
// Medlemmar:Jörgen Hård (ProductOwner), Joakim Bjärkstedt (scrum-Master) Elin Andersson(utvecklare) Denise Cigel (Utvecklare)
class MainActivity : AppCompatActivity() {
    private lateinit var listAdapter: TopScoreExpandableListAdapter
    private lateinit var gameList: MutableList<String>
    private lateinit var topScorePerGame: HashMap<String, List<DataManager.Score>>
    private lateinit var dataManager: DataManager
    private var backgroundMusic: BackgroundMusic? = null

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        backgroundMusic = BackgroundMusic(this)

        binding.btnPong.setOnClickListener {
            val intent = Intent(this, PongActivity::class.java)
            intent.putExtra("userName", binding.etPlayername.text.toString())
            startActivity(intent)
        }
        binding.btnSquash.setOnClickListener {
            val intent = Intent(this, SquashActivity::class.java)
            intent.putExtra("userName", binding.etPlayername.text.toString())
            startActivity(intent)
        }

        dataManager = SharedPreferencesManager(this)
        loadTopScores()

        listAdapter = TopScoreExpandableListAdapter(this, gameList, topScorePerGame)
        binding.expandableListView.setAdapter(listAdapter)
        setupListeners()
    }

    private fun setupListeners() {
        binding.expandableListView.setOnGroupClickListener { parent, v, groupPosition, id ->
            false   // Returnera 'false' för att tillåta standardexpandering/kollaps av gruppen
        }
        binding.expandableListView.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
            true // Returnera 'true' för att indikera att klicket hanterats
        }
    }

    private fun loadTopScores() {
        //get all the games as a list of Strings
        gameList = DataManager.Game.values().map { game -> game.name }.toMutableList()

        topScorePerGame = HashMap()

        DataManager.Game.values().forEach { game ->
            topScorePerGame[game.name] = dataManager.getAllScores(game)
        }
        listAdapter = TopScoreExpandableListAdapter(
            this,
            gameList,
            topScorePerGame
        )
    }

    override fun onResume() {
        super.onResume()

        DataManager.Game.values().forEach { game ->
            topScorePerGame[game.name] = dataManager.getAllScores(game)
        }

        listAdapter.notifyDataSetChanged()


        backgroundMusic?.loopTrack(true)
        backgroundMusic?.play(0)

    }

    override fun onPause() {
        super.onPause()
        if (backgroundMusic != null) {
            backgroundMusic!!.pauseMedia()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        backgroundMusic?.stop()
    }
}

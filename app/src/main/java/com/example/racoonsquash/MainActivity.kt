package com.example.racoonsquash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.racoonsquash.databinding.ActivityMainBinding
//RacoonGames
// Medlemmar:Jörgen Hård (ProductOwner), Joakim Bjärkstedt (scrum-Master) Elin Andersson(utvecklare) Denise Cigel (Utvecklare)
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val btnpong: Button = findViewById(R.id.btn_pong)
        val btnsquash: Button = findViewById(R.id.btn_squash)


        btnpong.setOnClickListener {
            val intent = Intent(this, PongActivity::class.java)
            startActivity(intent)
        }
        btnsquash.setOnClickListener {
            val intent = Intent(this, SquashActivity::class.java)
            startActivity(intent)
        }
    }
}

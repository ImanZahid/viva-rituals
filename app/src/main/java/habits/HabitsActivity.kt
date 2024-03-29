package com.example.habits

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.habits.adapter.ParentAdapter
import com.example.habits.model.ParentDataFactory
import com.ncorti.kotlin.template.app.MainActivity
import com.ncorti.kotlin.template.app.R
import com.ncorti.kotlin.template.app.databinding.ActivityHabitsBinding
import com.ncorti.kotlin.template.app.userClass.Constants
import com.ncorti.kotlin.template.app.userClass.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HabitsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHabitsBinding
    lateinit var recyclerView: RecyclerView
    private lateinit var parentAdapter: ParentAdapter
    lateinit var mediaPlayer: MediaPlayer
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHabitsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mediaPlayer = MediaPlayer.create(this, R.raw.soundeffect)

        lifecycleScope.launch {
            try {
                // Step 2: Observe changes in habits LiveData
                HabitSys.habits.observe(this@HabitsActivity, Observer { updatedHabits ->
                    // Step 3: Trigger logic that depends on the loaded data
                    Log.d("_HABITS_OBSERVER", updatedHabits.toString())
                    // Call the logic that uses the habits data here, like initializing the recycler view
                    lifecycleScope.launch {
                        initRecycler()
                        parentAdapter.notifyDataSetChanged()
                    }

                })

            } catch (e: Exception) {
                Log.e("HabitsActivity", "Error during data retrieval", e)
            }
        }

        binding.btnHabitsToMain.setOnClickListener{
            mediaPlayer.start()
            val switchActivityIntent: Intent
            switchActivityIntent = Intent(this, MainActivity::class.java)
            startActivity(switchActivityIntent)
        }
    }
    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            initRecycler()
        }
    }
    suspend fun initRecycler() {
        val parents = withContext(Dispatchers.Default) {
            ParentDataFactory.getParents(this@HabitsActivity, Constants.UID)
        }

        recyclerView = binding.rvParent
        parentAdapter = ParentAdapter(this, parents, lifecycleScope)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@HabitsActivity, RecyclerView.VERTICAL, false)
            adapter = parentAdapter
        }
    }
}
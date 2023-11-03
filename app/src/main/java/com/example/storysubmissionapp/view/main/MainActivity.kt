package com.example.storysubmissionapp.view.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.example.storysubmissionapp.R
import com.example.storysubmissionapp.data.model.UserModel
import com.example.storysubmissionapp.databinding.ActivityMainBinding
import com.example.storysubmissionapp.utils.showToast
import com.example.storysubmissionapp.view.ViewModelFactory
import com.example.storysubmissionapp.view.adapter.LoadingStateAdapter
import com.example.storysubmissionapp.view.adapter.StoryAdapter
import com.example.storysubmissionapp.view.maps.MapsActivity
import com.example.storysubmissionapp.view.upload.UploadActivity
import com.example.storysubmissionapp.view.welcome.WelcomeActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private val adapter = StoryAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }
        viewModel.getSessionData().observe(this@MainActivity) { user ->
            if (!user.isLogin) {
                val intent = Intent(this, WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                loadStories(user)
            }
        }
    }

    private fun loadStories(user: UserModel) {
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            LoadingStateAdapter {
                adapter.retry()
            }
        )

        viewModel.stories(user.token).observe(this) {
            adapter.submitData(lifecycle, it)

        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                when (loadStates.refresh) {
                    is LoadState.NotLoading -> {

                    }

                    is LoadState.Error -> {
                        showToast(
                            getString(
                                R.string.error,
                                (loadStates.refresh as LoadState.Error).error.message.toString()
                            )
                        )
                    }

                    else -> Unit
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                lifecycleScope.launch {
                    viewModel.logout()
                }
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }

            R.id.settings -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }

            R.id.maps -> {
                startActivity(Intent(this, MapsActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
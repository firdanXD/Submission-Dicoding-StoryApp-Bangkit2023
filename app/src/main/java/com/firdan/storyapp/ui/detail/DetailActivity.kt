package com.firdan.storyapp.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.firdan.storyapp.data.local.entity.UserModel
import com.firdan.storyapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private val binding: ActivityDetailBinding by lazy {
        ActivityDetailBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
    }

    private fun setupView() {
        val story: UserModel? = intent.getParcelableExtra(STORY_EXTRA)

        story?.let {
            supportActionBar?.title = it.name
            supportActionBar?.setDisplayShowHomeEnabled(true)

            binding.tvDesc.text = it.description

            Glide.with(this)
                .load(it.photoUrl)
                .into(binding.ivStory)
        }
    }

    companion object {
        const val STORY_EXTRA = "story_extra"
    }
}

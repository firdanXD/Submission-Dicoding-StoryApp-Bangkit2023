package com.firdan.storyapp.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.firdan.storyapp.data.local.entity.UserModel
import com.firdan.storyapp.databinding.ItemPostBinding
import com.firdan.storyapp.ui.detail.DetailActivity
import com.firdan.storyapp.ui.main.ListFragment
import com.firdan.storyapp.ui.MapFragment

class StoriesAdapter(private val appContext: Context) :
    PagingDataAdapter<UserModel, StoriesAdapter.StoryViewHolder>(StoryDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding =
            ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val currentStory = getItem(position)
        holder.bind(currentStory)

        holder.binding.cardStory.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailActivity::class.java)
            intent.putExtra(DetailActivity.STORY_EXTRA, currentStory)

            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    appContext as Activity,
                    Pair(holder.binding.imgPost, "picture"),
                    Pair(holder.binding.tvCaption, "description")
                )

            holder.itemView.context.startActivity(intent, optionsCompat.toBundle())
        }
    }

    inner class StoryViewHolder(val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: UserModel?) {
            binding.let {
                binding.tvUserName.text = story?.name
                binding.tvCaption.text = story?.description

                Glide.with(itemView.context)
                    .load(story?.photoUrl)
                    .into(binding.imgPost)
            }
        }
    }

    object StoryDiffCallback : DiffUtil.ItemCallback<UserModel>() {
        override fun areItemsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
            return oldItem == newItem
        }
    }

    class SectionPageAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount() = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> ListFragment()
                1 -> MapFragment()
                else -> throw IllegalArgumentException("Invalid position: $position")
            }
        }
    }
}
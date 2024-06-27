package com.firdan.storyapp.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.firdan.storyapp.R
import com.firdan.storyapp.adapter.StoriesAdapter
import com.firdan.storyapp.ui.LoadAdapter
import com.firdan.storyapp.databinding.FragmentListBinding
import com.firdan.storyapp.ui.viewmodels.MainViewModel
import com.firdan.storyapp.utils.ViewModelFactory
import kotlinx.coroutines.launch

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private lateinit var storiesAdapter: StoriesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel: MainViewModel by viewModels {
            ViewModelFactory.getInstance(requireContext())
        }
        setupStoriesAdapter()
        setupData(viewModel)
    }

    private fun setupStoriesAdapter() {
        storiesAdapter = StoriesAdapter(requireActivity())

        binding.rvStories.apply {
            adapter = storiesAdapter.withLoadStateFooter(
                footer = LoadAdapter {
                    retryStoriesLoading()
                }
            )

            layoutManager = LinearLayoutManager(requireActivity())
            setHasFixedSize(true)
        }

        binding.swipeLayout.setOnRefreshListener {
            binding.tvError.visibility = View.INVISIBLE
            storiesAdapter.retry()
            storiesAdapter.refresh()
        }

        storiesAdapter.addLoadStateListener { loadState ->
            if (loadState.mediator?.refresh is LoadState.Loading) {
                if (storiesAdapter.snapshot().isEmpty()) {
                    binding.progressBar.visibility = View.VISIBLE
                }

                binding.tvNoStory.visibility = View.INVISIBLE
            } else {
                binding.progressBar.visibility = View.INVISIBLE
                binding.swipeLayout.isRefreshing = false

                val error = when {
                    loadState.mediator?.refresh is LoadState.Error -> loadState.mediator?.refresh as LoadState.Error
                    loadState.mediator?.prepend is LoadState.Error -> loadState.mediator?.prepend as LoadState.Error
                    loadState.mediator?.append is LoadState.Error -> loadState.mediator?.append as LoadState.Error
                    else -> null
                }

                error?.let {
                    if (storiesAdapter.snapshot().isEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.failed_fetching_data),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    binding.tvError.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun retryStoriesLoading() {
        storiesAdapter.retry()
    }

    private fun setupData(viewModel: MainViewModel) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getToken().observe(viewLifecycleOwner) {
                if (it != "null") {
                    fetchData(viewModel, "Bearer $it")
                }
            }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    private fun fetchData(viewModel: MainViewModel, token: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getStories(token).observe(viewLifecycleOwner) {
                storiesAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

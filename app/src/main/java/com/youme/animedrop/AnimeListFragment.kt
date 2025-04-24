package com.youme.animedrop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.youme.animedrop.data.model.AnimeCountdown
import com.youme.animedrop.pagingmodel.AnimePagingAdapter
import com.youme.animedrop.pagingmodel.AnimeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AnimeListFragment : Fragment() {

    private val viewModel: AnimeViewModel by viewModels()
    private val adapter: AnimePagingAdapter by lazy { AnimePagingAdapter(::onAnimeItemClick) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_anime_list, container, false)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.AnimeFlow.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
        val recyclerView = view.findViewById<RecyclerView>(R.id.main)
        recyclerView.layoutManager=GridLayoutManager(requireActivity(),2)
        recyclerView.adapter=adapter

        return view
    }

    private fun onAnimeItemClick(mangaItem :AnimeCountdown){
    }
}

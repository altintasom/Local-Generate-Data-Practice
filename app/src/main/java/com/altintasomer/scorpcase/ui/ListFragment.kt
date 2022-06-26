package com.altintasomer.scorpcase.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.altintasomer.scorpcase.R
import com.altintasomer.scorpcase.databinding.FragmentListBinding
import com.altintasomer.scorpcase.ui.adapter.PersonListAdapter
import com.altintasomer.scorpcase.utils.PaginationScrollListener
import com.altintasomer.scorpcase.utils.Status
import com.altintasomer.scorpcase.viewmodel.ListViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "ListFragment"

@AndroidEntryPoint
class ListFragment : Fragment(R.layout.fragment_list) {

    private lateinit var binding: FragmentListBinding

    private val viewModel: ListViewModel by viewModels()
    private lateinit var gridLayoutManager: GridLayoutManager

    private lateinit var personListAdapter: PersonListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        personListAdapter = PersonListAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    private fun init(view: View) {
        binding = FragmentListBinding.bind(view)
        gridLayoutManager =
            GridLayoutManager(requireContext(), 1, GridLayoutManager.VERTICAL, false)
        binding.rvList.apply {
            adapter = personListAdapter
            layoutManager = gridLayoutManager
            setHasFixedSize(true)
        }.addOnScrollListener(object : PaginationScrollListener(gridLayoutManager) {
            override fun loadMoreItems() {
                if (viewModel.isLastPage)
                    showSnackBar()
                else
                    viewModel.getPerson()
            }

            override fun isLoading(): Boolean {
                return viewModel.isLoading
            }

        })

        viewModel.personList.observe(viewLifecycleOwner) { eventResponse ->
            eventResponse.getContentIfNotHandled()?.let { resource ->
                when (resource.status) {
                    Status.LOADING -> {
                        binding.layoutProgress.visibility = View.VISIBLE
                        binding.layoutRefresh.visibility = View.GONE
                    }
                    Status.SUCCESS -> {
                        binding.layoutProgress.visibility = View.GONE
                        personListAdapter.updateList(resource.data)
                        binding.layoutRefresh.visibility = View.GONE
                        if (resource.data.isNullOrEmpty()) {
                            binding.layoutRefresh.visibility = View.VISIBLE
                        }
                    }
                    Status.ERROR -> {
                        binding.layoutProgress.visibility = View.GONE
                        Log.e(TAG, "init: error:  ${resource.message}")
                        Toast.makeText(
                            requireContext(),
                            resource.message ?: "Error",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }
            }
        }

        binding.swipeList.setOnRefreshListener {
            viewModel.swipeRefresh()
            binding.swipeList.isRefreshing = false
        }


    }

    private fun showSnackBar() {
        val snackbar = Snackbar.make(binding.main, "End of the list", Snackbar.LENGTH_LONG)

        snackbar.setAction("Refresh") {
            viewModel.swipeRefresh()
            snackbar.dismiss()
        }
        snackbar.show()

    }


}
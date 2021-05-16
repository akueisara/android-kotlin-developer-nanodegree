package com.udacity.shoestore.screens.shoes.shoelist

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.udacity.shoestore.R
import com.udacity.shoestore.databinding.FragmentShoeListBinding
import com.udacity.shoestore.databinding.ListShoeItemBinding
import com.udacity.shoestore.screens.shoes.ShoeViewModel

class ShoeListFragment : Fragment() {

    private lateinit var binding: FragmentShoeListBinding
    private val shoeViewModel: ShoeViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_shoe_list, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fragment = this
        binding.lifecycleOwner = this

        shoeViewModel.shoes.observe(viewLifecycleOwner, { shoes ->
            shoes.forEach { shoe ->
                val shoeItemBinding = ListShoeItemBinding.inflate(layoutInflater)
                shoeItemBinding.shoe = shoe
                binding.showListLayout.addView(shoeItemBinding.root)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.log_out) {
            navigateToLoginScreen()
        }
        return super.onOptionsItemSelected(item)
    }

    fun navigateToShoeDetailScreen() {
        view?.findNavController()?.navigate(ShoeListFragmentDirections.actionShoeListToShoeDetail())
    }

    private fun navigateToLoginScreen() {
        view?.findNavController()?.navigate(ShoeListFragmentDirections.actionShoeListToLogin())
    }
}
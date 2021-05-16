package com.udacity.shoestore.screens.shoes.shoelist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.udacity.shoestore.R
import com.udacity.shoestore.databinding.FragmentShoeListBinding
import com.udacity.shoestore.screens.shoes.ShoeItemLinearLayout
import com.udacity.shoestore.screens.shoes.ShoeViewModel

class ShoeListFragment : Fragment() {

    private lateinit var binding: FragmentShoeListBinding
    private val shoeViewModel: ShoeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_shoe_list, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this

        shoeViewModel.shoes.observe(viewLifecycleOwner, { shoes ->
            shoes.forEach { shoe ->
                val shoeLayout = ShoeItemLinearLayout(requireContext())
                shoeLayout.setShoe(shoe)
                binding.showListLayout.addView(shoeLayout)
            }
        })
    }

    fun navigateToShoeDetailScreen() {
        view?.findNavController()?.navigate(ShoeListFragmentDirections.actionShoeListToShoeDetail())
    }
}
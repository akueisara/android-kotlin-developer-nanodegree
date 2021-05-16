package com.udacity.shoestore.screens.shoes

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import com.udacity.shoestore.R
import com.udacity.shoestore.databinding.ListShoeItemBinding
import com.udacity.shoestore.models.Shoe

class ShoeItemLinearLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var binding: ListShoeItemBinding =
        DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.list_shoe_item, this, false)

    fun setShoe(shoe: Shoe) {

    }
}
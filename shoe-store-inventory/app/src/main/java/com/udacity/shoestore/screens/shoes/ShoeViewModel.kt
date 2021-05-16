package com.udacity.shoestore.screens.shoes

import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udacity.shoestore.models.Shoe

class ShoeViewModel: ViewModel(), Observable {
    private val _shoes = MutableLiveData<MutableList<Shoe>>(mutableListOf())
    val shoes: LiveData<MutableList<Shoe>>
        get() = _shoes

    private val _back = MutableLiveData<Boolean>()
    val back: LiveData<Boolean>
        get() = _back

    private var name = ""
    private var size = 0.0
    private var company = ""
    private var description = ""

    private fun initializeShoeInfo() {
        name = ""
        size = 0.0
        company = ""
        description = ""
    }

    fun saveShoe() {
        _shoes.value?.add(Shoe(name, size, description, company))
        initializeShoeInfo()
        _back.value = true
    }

    fun completeGoingBack() {
        _back.value = false
    }

    /* --- Observable implementation --- */

    @delegate:Transient
    private val callBacks: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callBacks.add(callback)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        callBacks.remove(callback)
    }

    private fun notifyChange() {
        callBacks.notifyCallbacks(this, 0, null)
    }

    /* --- Two-way data binding --- */

    @Bindable
    fun getShoeName(): String {
        return name
    }

    fun setShoeName(value: String) {
        if (name != value) {
            name = value

            // Notify observers of a new value.
            notifyChange()
        }
    }

    @Bindable
    fun getShoeCompany(): String {
        return company
    }

    fun setShoeCompany(value: String) {
        if (company != value) {
            company = value

            notifyChange()
        }
    }

    @Bindable
    fun getShoeSize(): String {
        return size.toString()
    }

    fun setShoeSize(value: String) {
        if (size != value.toDouble()) {
            size = value.toDouble()

            notifyChange()
        }
    }

    @Bindable
    fun getShoeDescription(): String {
        return description
    }

    fun setShoeDescription(value: String) {
        if (description != value) {
            description = value

            notifyChange()
        }
    }
}
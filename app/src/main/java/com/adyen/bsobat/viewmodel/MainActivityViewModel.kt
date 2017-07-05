package com.bol.instantapp.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations.switchMap
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentActivity
import com.bol.instantapp.Repository.FourSquareRepository
import javax.inject.Inject


class MainActivityViewModel : ViewModel(){
    private val exploreInput: MutableLiveData<Input> = MutableLiveData()

    val venuesResult = switchMap(exploreInput){
            repository.exploreVenues(it.lat, it.lon, it.radius);
    }

    private lateinit  var repository: FourSquareRepository;

    @Inject fun init(repository: FourSquareRepository) {
        this.repository = repository;

    }

    fun explore(lat: String,lon: String,radius: Int){

        exploreInput.value = Input(lat, lon, radius)
    }

    companion object{
        fun create(activity: FragmentActivity): MainActivityViewModel {
            var viewModel = ViewModelProviders.of(activity).get(MainActivityViewModel::class.java)
            return viewModel
        }
    }
}
data class Input(var lat: String, var lon: String, var radius: Int)
package com.bol.instantapp.Repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.adyen.bsobat.dto.FourSquareResponse
import com.adyen.bsobat.dto.Venue
import com.adyen.bsobat.dto.Venues
import com.bol.instantapp.api.FourSquareApi
import com.bol.instantapp.exception.AppException
import com.bol.instantapp.viewmodel.Resource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class FourSquareRepository @Inject constructor(val api: FourSquareApi) {

    fun exploreVenues(lat: String, lon: String, radius: Int): LiveData<Resource<Venues>> {
        val data = MutableLiveData<Resource<Venues>>();
        val ll = "$lat,$lon"
        api.exploreVenues(ll, radius).enqueue(object : Callback<FourSquareResponse> {
            override fun onResponse(call: Call<FourSquareResponse>?, response: Response<FourSquareResponse>?) {
                val items: MutableList<Venue> = mutableListOf()

                response?.body()?.response?.groups?.let {
                    val body = response.body();
                    val groups = body.response!!.groups
                    for (item in groups) {
                        item.items.mapTo(items) { it.venue }
                    }

                }
                val venue = Venues(items)
                data.value = Resource.success(venue)
            }

            override fun onFailure(call: Call<FourSquareResponse>?, t: Throwable?) {
                val exception = AppException(t)
                data.value = Resource.error(exception)
            }
        });
        return data;
    }
}

package com.bol.instantapp.api

import com.adyen.bsobat.dto.FourSquareResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FourSquareApi {
    @GET("v2/venues/explore")
    abstract fun exploreVenues(@Query("ll") ll: String, @Query("radius") radius: Int = 250): Call<FourSquareResponse>
}
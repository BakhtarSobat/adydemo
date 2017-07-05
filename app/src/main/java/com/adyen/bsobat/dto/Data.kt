package com.adyen.bsobat.dto


data class Location(var address: String, var postalCode: String?, var city: String?, var distance: String?)
data class Venue(var id:String?, var name:String?, var location: Location?, var url: String?)
data class Venues(var items: List<Venue>)
data class Item(var venue: Venue)
data class Group(var items: List<Item>)
data class RecommendedPlace(var groups: List<Group>)
data class FourSquareResponse(var response: RecommendedPlace?)
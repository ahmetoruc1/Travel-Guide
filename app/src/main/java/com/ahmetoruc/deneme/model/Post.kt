package com.ahmetoruc.deneme.model

import java.io.Serializable

class Post(val email:String,val city:String,val country:String,val title:String,val downloadUrl:String,val selectedLatitude:Double?,val selectedLongitude:Double?):Serializable {
}
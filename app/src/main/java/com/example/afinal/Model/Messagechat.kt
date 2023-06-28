package com.example.afinal.Model

class Messagechat {

    var message:String? =  null
    var senderId:String? = null
    var currentTime:String?= null
    var image:String? = null
    var key: String = ""

    constructor(){}

    constructor(message:String?,senderId:String?,currentTime:String?,image:String?){
       this.message = message
       this.senderId = senderId
       this.currentTime = currentTime
        this.image = image

    }

}
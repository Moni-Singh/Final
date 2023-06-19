package com.example.afinal.Model

class Messagechat {

    var message:String? =  null
    var senderId:String? = null
    var key: String = ""

    constructor(){}

    constructor(message:String?,senderId:String?){
       this.message = message
       this.senderId = senderId

    }

}
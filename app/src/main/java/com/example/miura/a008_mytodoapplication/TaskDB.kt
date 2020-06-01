package com.example.miura.a008_mytodoapplication

import io.realm.RealmObject

open class TaskDB:RealmObject() {

    //データベースに保存する内容
    open var strTime: String = ""          //Todoの時間
    open var strTask: String = ""          //Todoの内容
    open var finishFrag: Boolean = false   //Todoの達成状況(達成or未達成)

}
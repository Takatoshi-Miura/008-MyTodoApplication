package com.example.miura.a008_mytodoapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_edit.*
import java.text.SimpleDateFormat
import java.util.*

class EditActivity : AppCompatActivity() {

    //データベースに保存する内容
    lateinit var realm: Realm   //Realmをインスタンス化するための変数
    var strTime: String = ""    //Todoの時間
    var strTask: String = ""    //Todoの内容

    //遷移元から受け取るデータ
    var intposition: Int = 0            //行番号
    var boolMemorize: Boolean = false   //Todoの達成状況

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        //タスクの日時の初期値として現在時刻を設定
        val df = SimpleDateFormat("MM/dd HH:mm")
        val date = Date()
        editTextTime.setText(df.format(date))

        //遷移元から送られてきたデータを受け取る
        val bundle = intent.extras

        //タスクの修正か追加かの判定用
        val strStatus = bundle?.getString(getString(R.string.intent_key_status))
        textViewStatus.text = strStatus

        //タスクの修正の場合の処理
        if (strStatus == getString(R.string.status_change)) {
            strTime = bundle.getString(getString(R.string.intent_key_time))!!
            strTask = bundle.getString(getString(R.string.intent_key_task))!!
            boolMemorize = bundle.getBoolean(getString(R.string.intent_key_frag))

            editTextTime.setText(strTime)
            editTextTask.setText(strTask)
            checkBox.isChecked = boolMemorize

            intposition = bundle.getInt(getString(R.string.intent_key_position))
        }


        checkBox.setOnClickListener {
            boolMemorize = checkBox.isChecked
        }

        //登録ボタンをタップした時の処理
        buttonRegister.setOnClickListener {
            if (strStatus == getString(R.string.status_add)) {
                addNewTask()
            } else {
                changeTask()
            }
        }

    }


    override fun onResume() {
        super.onResume()
        //データベースの使用を可能にする
        realm = Realm.getDefaultInstance()
    }


    override fun onPause() {
        super.onPause()
        //データベースの使用を止める
        realm.close()
    }


    //RealmデータベースにTodoを追加するメソッド
    private fun addNewTask() {
        //データベースの使用開始
        realm.beginTransaction()

        //データベースに新たにデータを追加することを宣言
        val taskDB = realm.createObject(TaskDB::class.java)

        //Todoの時間と内容を追加
        taskDB.strTime = editTextTime.text.toString()
        taskDB.strTask = editTextTask.text.toString()

        //データベースの使用を終了
        realm.commitTransaction()

        editTextTask.setText("")
        //トーストで結果を表示し、元の画面に推移
        Toast.makeText(this@EditActivity,"登録が完了いたしました",Toast.LENGTH_SHORT).show()
        finish()
    }


    //RealmデータベースのTodoを修正するメソッド
    private fun changeTask() {
        //データベースの全データの取得
        val result = realm.where(TaskDB::class.java).findAll().sort("strTime")

        //全データの中の（前の画面でタップされた行番号）番目のデータを取得
        val selectDB = result[intposition]!!

        //データベースの使用宣言
        realm.beginTransaction()

        //データの修正
        selectDB.strTime = editTextTime.text.toString()
        selectDB.strTask = editTextTask.text.toString()
        selectDB.finishFrag = boolMemorize

        //データベースの更新
        realm.commitTransaction()

        editTextTime.setText("")
        editTextTask.setText("")

        //トーストで結果を表示し、元の画面に推移
        Toast.makeText(this@EditActivity,"修正が完了いたしました",Toast.LENGTH_SHORT).show()
        finish()
    }

}

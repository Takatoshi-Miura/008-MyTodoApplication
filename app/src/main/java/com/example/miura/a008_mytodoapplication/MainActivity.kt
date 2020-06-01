package com.example.miura.a008_mytodoapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    lateinit var realm: Realm                    //Realmをインスタンス化するための変数
    lateinit var result:RealmResults<TaskDB>     //データベースのデータを格納するための変数
    lateinit var task_list: ArrayList<String>    //listView表示用
    lateinit var adapter: ArrayAdapter<String>   //listViewとArrayListをつなげるためのアダプター


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //「新しいタスクの追加」ボタンで画面推移
        buttonAddNewWord.setOnClickListener {
            val intent = Intent(this@MainActivity, EditActivity::class.java)
            intent.putExtra(getString(R.string.intent_key_status), getString(R.string.status_add))
            startActivity(intent)
        }

        //
        listView.setOnItemClickListener(this)
        listView.setOnItemLongClickListener(this)
    }


    override fun onResume() {
        super.onResume()

        //データベースの使用を可能にする
        realm = Realm.getDefaultInstance()

        //データベースに保存されているデータを抽出
        result = realm.where(TaskDB::class.java).findAll().sort("strTime")

        //listViewには『Todoの時間：Todoの内容』という形式で表示したい。
        //データベースからはデータをレコードで取得しているため、listView用に形式を再構築する必要がある。
        //全ての要素をバラし、ArrayListに上記の形式で格納していく。
        task_list = ArrayList<String>()

        //要素の読み取り＆形式の再構築
        val length = result.size
        for (i in 0 .. length-1) {
            if (result[i].finishFrag) {
                task_list.add(result[i]!!.strTime + ":" + result[i]!!.strTask + "✔️")
            } else {
                task_list.add(result[i]!!.strTime + ":" + result[i]!!.strTask)
            }
        }

        //ArrayAdapterを使用し、listViewにArrayListの内容を表示させる
        adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, task_list)
        listView.adapter = adapter
    }


    override fun onPause() {
        super.onPause()
        //データベースの使用を止める
        realm.close()
    }


    //listViewの項目をタップした時の処理
    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        //タップした項目の情報を取得
        val selectDB = result[position]                     //行番号
        val strSelectTime = result[position]!!.strTime      //Todoの時間
        val strSelectTask = result[position]!!.strTask      //Todoの内容
        val strSelectFrag = result[position]!!.finishFrag   //Todoの達成状況

        //項目の情報を持って「編集画面」へ推移
        val intent = Intent(this@MainActivity, EditActivity::class.java)
        intent.putExtra(getString(R.string.intent_key_time),strSelectTime)
        intent.putExtra(getString(R.string.intent_key_task),strSelectTask)
        intent.putExtra(getString(R.string.intent_key_frag),strSelectFrag)
        intent.putExtra(getString(R.string.intent_key_position),position)
        intent.putExtra(getString(R.string.intent_key_status),getString(R.string.status_change))
        startActivity(intent)
    }


    //listViewの項目を長押しした時の処理
    override fun onItemLongClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long): Boolean {

        //長押しした項目を削除するか否かのメッセージを表示
        val selectDB = result[position]!!
        val dialog = AlertDialog.Builder(this@MainActivity).apply{
            setTitle(selectDB.strTask + "の削除")
            setMessage("削除しても良いですか？")
            setPositiveButton("yes"){dialog, which ->

                //項目のデータを削除
                realm.beginTransaction()
                selectDB.deleteFromRealm()
                realm.commitTransaction()

                //項目のリストを削除
                task_list.removeAt(position)

                //アダプターを再接続
                listView.adapter = adapter
            }

            //削除しない場合は何もしない
            setNegativeButton("no"){dialog, which ->
            }
            show()
        }

        return true

    }

}

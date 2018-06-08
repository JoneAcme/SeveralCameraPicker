package com.jone.demo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.jone.sevral.SeveralImagePicker
import com.jone.sevral.comments.inter.PickerCompleteInterface
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        btn_select.setOnClickListener {
            selectImg()
        }

    }

    private fun selectImg() {
        SeveralImagePicker.setCompleteListener(object : PickerCompleteInterface {
            override fun onComlete(pathList: ArrayList<String>) {
                textView.text = pathList.toString()
//                Toast.makeText(this@MainActivity, "select image success${pathList.toString()}", Toast.LENGTH_SHORT).show()
            }
        }).start(this)
    }
}

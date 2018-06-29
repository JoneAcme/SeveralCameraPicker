package com.jone.demo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import com.jone.several.SeveralImagePicker
import com.jone.several.comments.inter.PickerCompleteInterface
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        btn_select.setOnClickListener {
            selectImg()
        }

        btnPop.setOnClickListener {
//            UpLoadImgDialog(this).showAtLocation(btnPop,Gravity.CENTER,0,0)
            UpLoadImgDialog(this).show()
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

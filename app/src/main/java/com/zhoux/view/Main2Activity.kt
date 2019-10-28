package com.zhoux.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main2.*
import java.util.*

class Main2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)


        CircularProgressView.setStartDraw(true)
        CircularProgressView.setProgress(20)
        CircularProgressView.invalidate()

        LineProgressViewKt.setProgress(2,5)



        btnChange.setOnClickListener {
            val random = Random()
            val randomHight2 =  1+random.nextInt(5)
            LineProgressViewKt.setProgress(randomHight2-1,5)

            val randomHight =  random.nextInt(100)
            CircularProgressView.setProgress(randomHight)
        }

    }
}

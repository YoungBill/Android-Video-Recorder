package com.pin.video.recorder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv_record.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_record -> {
                AndPermission
                    .with(this@MainActivity)
                    .runtime()
                    .permission(Permission.RECORD_AUDIO, Permission.CAMERA)
                    .onGranted {
                        val intent = Intent(this@MainActivity, VideoCameraActivity::class.java)
                        startActivityForResult(intent, 100)
                    }
                    .onDenied {
                        Toast.makeText(
                            this@MainActivity,
                            "give me permission!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .start()
            }
        }
    }
}

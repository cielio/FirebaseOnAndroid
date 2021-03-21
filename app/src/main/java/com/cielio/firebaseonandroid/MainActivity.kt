package com.cielio.firebaseonandroid

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var filePath:Uri

    companion object{
        private const val REQUEST_CODE = 111
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnChoose.setOnClickListener {
            startFileChooser()
        }

        btnUpload.setOnClickListener {
            progress.visibility = ProgressBar.VISIBLE

            val imageRef = FirebaseStorage.getInstance().reference.child("images/pic.jpg")
            imageRef.putFile(filePath)
                    .addOnSuccessListener {
                        progress.visibility = ProgressBar.GONE
                        Toast.makeText(applicationContext, "Arquivo enviado", Toast.LENGTH_LONG).show()
                    }
                    .removeOnFailureListener{
                        progress.visibility = ProgressBar.GONE
                        Toast.makeText(applicationContext, it.message, Toast.LENGTH_LONG).show()
                    }
                    .removeOnProgressListener {
                        //var progressBar = (100.0 * it.bytesTransferred) / it.totalByteCount
                    }
        }
    }

    private fun startFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        //intent.action = Intent.ACTION_GET_CONTENT pega todas ais imagens
        //Intent.ACTION_PICK seleciona a fonte de imagens primeiro
        intent.action = Intent.ACTION_PICK
        startActivityForResult(Intent.createChooser(intent,"Escolha uma Imagem"),REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null){
            filePath = data.data!!
            imageView.setImageURI(filePath)
        }
    }
}
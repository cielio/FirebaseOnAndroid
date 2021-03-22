package com.cielio.firebaseonandroid

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import coil.load
import com.cielio.firebaseonandroid.databinding.ActivityMainBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class MainActivity : AppCompatActivity() {
    private lateinit var filePath: Uri
    private lateinit var binding: ActivityMainBinding
    companion object {
        private const val REQUEST_CODE = 111
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnChoose.setOnClickListener {
            startFileChooser()
        }

        binding.btnUpload.setOnClickListener {
            //uploadImage()
            // [START upload_get_download_url]
            uploadImageGetDonload()
        }
    }

    private fun uploadImageGetDonload() {
        binding.progress.visibility = ProgressBar.VISIBLE
        val storage = Firebase.storage
        val storageRef = storage.reference
        val ref = storageRef.child("images/mountains.jpg")
        val uploadTask = ref.putFile(filePath)

        val urlTask = uploadTask
                .continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    ref.downloadUrl
                }
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        binding.progress.visibility = ProgressBar.GONE
                        val downloadUri = task.result
                        Toast.makeText(applicationContext, "Arquivo enviado ${downloadUri.toString()}", Toast.LENGTH_LONG).show()
                        binding.imageView.load(downloadUri) {
                            placeholder(R.drawable.ic_launcher_background)
                            fallback(R.drawable.ic_launcher_background)
                        }
                    } else {
                        // Handle failures
                        // ...
                    }
                }
    }

    private fun uploadImage() {
        binding.progress.visibility = ProgressBar.VISIBLE

        val imageRef = FirebaseStorage.getInstance().reference.child("images/pic.jpg")
        imageRef.putFile(filePath)
                .addOnSuccessListener {
                    binding.progress.visibility = ProgressBar.GONE
                    Toast.makeText(applicationContext, "Arquivo enviado", Toast.LENGTH_LONG).show()
                }
                .removeOnFailureListener {
                    binding.progress.visibility = ProgressBar.GONE
                    Toast.makeText(applicationContext, it.message, Toast.LENGTH_LONG).show()
                }
                .removeOnProgressListener {
                    //var progressBar = (100.0 * it.bytesTransferred) / it.totalByteCount
                }
    }

    private fun startFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        //intent.action = Intent.ACTION_GET_CONTENT pega todas as imagens
        //Intent.ACTION_PICK seleciona a fonte de imagens primeiro
        intent.action = Intent.ACTION_PICK
        startActivityForResult(Intent.createChooser(intent, "Escolha uma Imagem"), REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            filePath = data.data!!
            binding.imageView.setImageURI(filePath)
        }
    }
}
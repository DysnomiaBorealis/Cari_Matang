package com.example.cari_matang2.presentation.uploadphoto

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import com.example.cari_matang2.adapter.UploadPhotoAdapter
import com.example.cari_matang2.databinding.ActivityUploadPhotoBinding
import java.io.File

@Suppress("DEPRECATION")
class UploadPhotoActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_SELECT_PHOTO = 100
    }

    private lateinit var binding : ActivityUploadPhotoBinding
    private lateinit var uploadPhotoAdapter: UploadPhotoAdapter
    private lateinit var photoFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUploadPhotoAdapter()

        if (intent.hasExtra("photoFile")) {
            photoFile = intent.getSerializableExtra("photoFile") as File
            val position = uploadPhotoAdapter.itemCount
            uploadPhotoAdapter.addPhoto(photoFile, position)
        }

    }

    private fun initUploadPhotoAdapter() {
        uploadPhotoAdapter = UploadPhotoAdapter(
            onRemovePhoto = { position ->
                uploadPhotoAdapter.removePhoto(position)
                // Disable the upload button if there are no more photos
                if (uploadPhotoAdapter.itemCount == 0) {
                    binding.btnUploadPhoto.isEnabled = false
                }
            }
        )
        binding.rvUploadPhoto.adapter = uploadPhotoAdapter
    }


    private fun selectPhotoFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_SELECT_PHOTO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SELECT_PHOTO && resultCode == RESULT_OK && data != null) {
            val photoUri = data.data
            val photoFile = uriToFile(photoUri)

            if (photoFile != null) {
                val position = uploadPhotoAdapter.itemCount
                uploadPhotoAdapter.addPhoto(photoFile, position)
                // Enable the upload button when a photo is added
                binding.btnUploadPhoto.isEnabled = true
            }
        }
    }


    private fun uriToFile(uri: Uri?): File? {
        val cursor = uri?.let { contentResolver.query(it, arrayOf(MediaStore.Images.Media.DATA), null, null, null) }
        val column_index = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        val path = column_index?.let { cursor.getString(it) }
        cursor?.close()
        return path?.let { File(it) }
    }
}



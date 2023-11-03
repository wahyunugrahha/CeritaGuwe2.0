package com.example.storysubmissionapp.view.upload

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.storysubmissionapp.R
import com.example.storysubmissionapp.data.Result
import com.example.storysubmissionapp.data.model.UserModel
import com.example.storysubmissionapp.databinding.ActivityUploadBinding
import com.example.storysubmissionapp.utils.ValidateType
import com.example.storysubmissionapp.utils.getImageUri
import com.example.storysubmissionapp.utils.reduceFileImage
import com.example.storysubmissionapp.utils.showToast
import com.example.storysubmissionapp.utils.uriToFile
import com.example.storysubmissionapp.utils.validate
import com.example.storysubmissionapp.view.ViewModelFactory
import com.example.storysubmissionapp.view.main.MainActivity
import com.example.storysubmissionapp.view.welcome.WelcomeActivity
import com.google.android.gms.location.FusedLocationProviderClient
import java.io.File

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    private val viewModel: UploadViewModel by viewModels { ViewModelFactory.getInstance(this) }
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentImageUri: Uri? = null
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast(getString(R.string.permission_request_granted), true)
            } else {
                showToast(getString(R.string.permission_request_denied), true)
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        viewModel.getSessionData().observe(this@UploadActivity) { user ->
            if (!user.isLogin) {
                val intent = Intent(this, WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                setupAction(user)
            }
        }
    }

    private fun setupAction(user: UserModel) {
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.uploadButton.setOnClickListener {
            if (binding.deskripsi.validate(
                    "Description cannot be empty",
                    ValidateType.REQUIRED
                )
            ) uploadImage(user.token, binding.deskripsi.text.toString())
        }
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private fun showImage() {
        currentImageUri?.let { uri ->
            Log.d("Image URI", "showImage: $uri")
            binding.previewImageView.setImageURI(uri)
        } ?: {
            Log.d("Empty Image", "Image is empty")
        }
    }

    private fun uploadImage(token: String, description: String) {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            if (
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                    val isChecked = binding.cbLoc.isChecked
                    val lat: Double? = if (isChecked) loc.latitude else null
                    val lon: Double? = if (isChecked) loc.longitude else null

                    uploadStory(token, imageFile, description, lat, lon)
                }
            } else {
                uploadStory(token, imageFile, description)
            }
        } ?: showToast(getString(R.string.image_is_required_to_make_a_story))
    }

    private fun uploadStory(
        token: String,
        imageFile: File,
        description: String,
        lat: Double? = null,
        lon: Double? = null
    ) {
        viewModel.uploadImage(token, imageFile, description, lat, lon)
            .observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            binding.uploadButton.isEnabled = false
                        }

                        is Result.Error -> {

                            binding.uploadButton.isEnabled = true
                            showToast(result.error)
                        }

                        is Result.Success -> {
                            binding.uploadButton.isEnabled = true
                            showToast(result.data.message)

                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                    }
                }
            }
    }


    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }


    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", getString(R.string.no_media_selected))
            showToast(getString(R.string.no_media_selected))
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }


    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}
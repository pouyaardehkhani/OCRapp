package com.example.ocrapp

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector


class MainActivity : AppCompatActivity() {
    private val CAMERA_REQUEST = 1888
    private lateinit var imageView: ImageView
    private lateinit var photo: Button
    private lateinit var detect: Button
    private lateinit var srcText: TextView
    private val MY_CAMERA_PERMISSION_CODE = 100
    // Binding object instance with access to the views in the activity_main.xml layout

    // variable for our image bitmap.
    private lateinit var imageBitmap: Bitmap

    companion object {
        val IMAGE_REQUEST_CODE = 1_000;
        const val REQUEST_IMAGE_CAPTURE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView1)
        photo = findViewById(R.id.button1)
        detect = findViewById(R.id.button2)
        srcText = findViewById(R.id.srcText)

        photo.setOnClickListener {
            dispatchTakePictureIntent()
        }
        detect.setOnClickListener {
            detectTxt()
        }
    }

    private fun dispatchTakePictureIntent() {
        // in the method we are displaying an intent to capture our image.
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // on below line we are calling a start activity
        // for result method to get the image captured.
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // calling on activity result method.
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // on below line we are getting
            // data from our bundles. .
            val extras = data!!.extras
            imageBitmap = extras!!["data"] as Bitmap

            // below line is to set the
            // image bitmap to our image.
            imageView!!.setImageBitmap(imageBitmap)
        }
    }

    private fun detectTxt() {
        // this is a method to detect a text from image.
        // below line is to create variable for firebase
        // vision image and we are getting image bitmap.
        val image = FirebaseVisionImage.fromBitmap(imageBitmap!!)

        // below line is to create a variable for detector and we
        // are getting vision text detector from our firebase vision.
        val detector: FirebaseVisionTextDetector =
            FirebaseVision.getInstance().getVisionTextDetector()

        // adding on success listener method to detect the text from image.
        detector.detectInImage(image)
            .addOnSuccessListener(OnSuccessListener<FirebaseVisionText> { firebaseVisionText -> // calling a method to process
                // our text after extracting.
                processTxt(firebaseVisionText)
            }).addOnFailureListener(OnFailureListener { // handling an error listener.
                Toast.makeText(
                    this@MainActivity,
                    "Fail to detect the text from image..",
                    Toast.LENGTH_SHORT
                ).show()
            })
    }

    private fun processTxt(text: FirebaseVisionText) {
        // below line is to create a list of vision blocks which
        // we will get from our firebase vision text.
        val blocks: List<FirebaseVisionText.Block> = text.getBlocks()

        // checking if the size of the
        // block is not equal to zero.
        if (blocks.size == 0) {
            // if the size of blocks is zero then we are displaying
            // a toast message as no text detected.
            Toast.makeText(this@MainActivity, "No Text ", Toast.LENGTH_LONG).show()
            return
        }
        // extracting data from each block using a for loop.
        for (block in text.getBlocks()) {
            // below line is to get text
            // from each block.
            val txt: String = block.getText()

            // below line is to set our
            // string to our text view.
            srcText.text = txt
        }
    }
}
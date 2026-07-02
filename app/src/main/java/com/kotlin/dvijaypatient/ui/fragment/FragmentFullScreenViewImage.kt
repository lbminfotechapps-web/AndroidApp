package com.kotlin.dvijaypatient.ui.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.kotlin.dvijaypatient.R
import com.squareup.picasso.Picasso

class FragmentFullScreenViewImage : Fragment() {

    private var rootView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_fragment_full_screen_view_image, container, false)
        init()
        return rootView
    }

    private fun init() {
        val imgDisplay = rootView?.findViewById<ImageView>(R.id.imgDisplay)
        val options = BitmapFactory.Options().apply {
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }

        var imagePath = arguments?.getString("imagePath", "") ?: ""
        imagePath = imagePath.replace(" ", "%20")

        if (imagePath.trim().isNotEmpty()) {
            try {
                imgDisplay?.visibility = View.VISIBLE
                Picasso.get()
                    .load(imagePath)
                    .into(imgDisplay, object : com.squareup.picasso.Callback {
                        override fun onSuccess() {
                            // Image loaded successfully
                        }

                        override fun onError(e: java.lang.Exception?) {
                            imgDisplay?.setImageResource(R.drawable.ic_shutter_normal)
                        }
                    })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            imgDisplay?.visibility = View.VISIBLE
            imgDisplay?.setImageResource(R.drawable.ic_shutter_normal)
        }
    }

    override fun onResume() {
        //ClassGlobal.getOnResumeBackground(activity)
        super.onResume()
    }

    override fun onPause() {
      //  ClassGlobal.getOnPouseBackground()
        super.onPause()
    }
}

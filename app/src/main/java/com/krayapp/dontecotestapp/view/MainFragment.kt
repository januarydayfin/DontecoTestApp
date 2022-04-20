package com.krayapp.dontecotestapp.view

import android.content.Intent
import androidx.fragment.app.Fragment

class MainFragment : Fragment() {
    companion object {
        fun newInstance(): Fragment = MainFragment()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}

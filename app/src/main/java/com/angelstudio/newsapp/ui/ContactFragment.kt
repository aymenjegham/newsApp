package com.angelstudio.newsapp.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.angelstudio.newsapp.R
import com.angelstudio.newsapp.databinding.FragmentContactBinding

class ContactFragment : Fragment(R.layout.fragment_contact) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentContactBinding.bind(view)
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            title = getString(R.string.contact_us)
            setDisplayHomeAsUpEnabled(true)
        }
        requireActivity().findViewById<View>(R.id.floatingActionButton).visibility = View.INVISIBLE
        requireActivity().findViewById<View>(R.id.tvdeleteall).visibility = View.GONE

        binding.contactEmailButton.setOnClickListener {
            val email = getString(R.string.jegham_aymen_89_gmail_com)
            val intent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null))
            try {
                startActivity(intent)
            } catch (exception: ActivityNotFoundException) {
                Toast.makeText(requireContext(), R.string.contact_no_email_app, Toast.LENGTH_LONG).show()
            }
        }
    }
}

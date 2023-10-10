package com.example.project5

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import com.example.project5.databinding.FragmentEditBinding

class EditFragment : Fragment() {

    // Late Initialize View Model and Fragment Binding
    private lateinit var sharedViewModel: SharedViewModel
    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Initialize our Fragment
        _binding = FragmentEditBinding.inflate(inflater, container, false)

        // set view to binding.root
        val view = binding.root

        // Initialize our View Model
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        // Any text change is being updated to the data save in the View Model
        binding.EditInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                sharedViewModel.setSharedString(s.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
        return view
    }
}
package com.example.project5

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.project5.databinding.ActivityMainBinding
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedViewModel: SharedViewModel
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        replaceFrag(EditFragment())
        var source = "English"
        var translate = "Spanish"
        var options: TranslatorOptions?
        var translator: Translator? = null
        var outputString = ""
        sharedViewModel.sharedString.observe(this) { newValue ->
            // Update the TextView with the new value
            outputString = newValue

            if (translator != null) {
                val conditions = DownloadConditions.Builder()
                    .requireWifi()
                    .build()
                translator!!.downloadModelIfNeeded(conditions)
                    .addOnSuccessListener {
                        // Model downloaded successfully. Okay to start translating.
                        translator!!.translate(newValue)
                            .addOnSuccessListener { translatedText ->
                                // Translation successful.
                                binding.Output.text = translatedText
                            }
                            .addOnFailureListener { _ ->
                                // Error.
                                // ...
                            }
                    }
                    .addOnFailureListener { _ ->
                        // Model couldn’t be downloaded or other internal error.
                        // ...
                    }
            }
        }
        binding.SourceGroup.setOnCheckedChangeListener{
                _, checkedId -> val radioButton = view.findViewById<RadioButton>(checkedId)
            source = radioButton.text.toString()
            options = TranslatorOptions.Builder()
                .setSourceLanguage(getTranslateLanguageCode(source))
                .setTargetLanguage(getTranslateLanguageCode(translate)).build()
            translator = Translation.getClient(options!!)
        }

        binding.TranslationSource.setOnCheckedChangeListener{
                _, checkedId -> val radioButton = view.findViewById<RadioButton>(checkedId)
            translate = radioButton.text.toString()
            options = TranslatorOptions.Builder()
                .setSourceLanguage(getTranslateLanguageCode(source))
                .setTargetLanguage(getTranslateLanguageCode(translate)).build()
            translator = Translation.getClient(options!!)
            if(binding.Output.text.isNotEmpty()){
                translator!!.translate(outputString)
                    .addOnSuccessListener { translatedText ->
                        // Translation successful.
                        binding.Output.text = translatedText
                    }
                    .addOnFailureListener { _ ->
                        // Error.
                        // ...
                    }
            }
        }
    }

    private fun getTranslateLanguageCode(languageName: String): String {
        return when (languageName) {
            "English" -> TranslateLanguage.ENGLISH
            "Spanish" -> TranslateLanguage.SPANISH
            "German" -> TranslateLanguage.GERMAN
            // Add cases for other languages as needed
            else -> throw IllegalArgumentException("Invalid language name: $languageName")
        }
    }

    private fun replaceFrag(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentFrame, fragment)
        fragmentTransaction.commit()
    }
}
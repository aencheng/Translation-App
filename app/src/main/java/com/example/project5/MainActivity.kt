package com.example.project5

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.project5.databinding.ActivityMainBinding
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentificationOptions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedViewModel: SharedViewModel
    // late initialize binding and View Model
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Bind the Activity
        binding = ActivityMainBinding.inflate(layoutInflater)

        // Set view to binding.root
        val view = binding.root
        setContentView(view)

        // Initialize View Model
        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]

        // Replace activity_main fragment with our actual Edit Fragment
        replaceFrag(EditFragment())

        // Initialize our Language Identifier with a set Confidence Threshold
        val languageIdentifier = LanguageIdentification.getClient(
            LanguageIdentificationOptions.Builder()
                .setConfidenceThreshold(0.34f) // Set confidence threshold for language identification
                .build()
        )

        // Initialize our variables we'll be using in translation.
        var source = ""
        var translate = ""
        var options: TranslatorOptions?
        var translator: Translator? = null
        var outputString = ""

        // Observes the live data withing the shared View Model
        sharedViewModel.sharedString.observe(this) { newValue ->
            outputString = newValue

            // Checks that the source is empty and a certain length of characters have been inputted
            // before running through the auto Translation.
            if(getTranslateLanguageCode(source) == "NA" && outputString.length >= 3){

                // Begins to Identify our Live Data
                languageIdentifier.identifyLanguage(outputString)
                    .addOnSuccessListener { languageCode ->
                        if (languageCode == "und") {
                            // Language not identified
                        } else {// On Success and found a valid Language Code
                            // pass the value to our source
                            source = languageCode
                            // Checks that our source and translate variables are not empty before running this code
                            // Otherwise, It will crash and throw an error.
                            if(getTranslateLanguageCode(languageCode) != "NA"
                                && getTranslateLanguageCode(languageCode) != "NA"){
                                // Builds our TranslationOptions
                                options = TranslatorOptions.Builder()
                                    .setSourceLanguage(getTranslateLanguageCode(source))
                                    .setTargetLanguage(getTranslateLanguageCode(translate)).build()

                                // Gets the Client of our Options
                                translator = Translation.getClient(options!!)
                            }
                            else{
                                // If language is not supported, this will be printed.
                                binding.Output.text = "This language is not supported"
                            }
                        }
                    }
                    .addOnFailureListener { _ ->
                        // Handle error
                    }
            }

            if(getTranslateLanguageCode(source) != "NA" && translate != ""){
                // Builds Download Conditions
                val conditions = DownloadConditions.Builder()
                    .requireWifi()
                    .build()
                // Checks non-null and the downloads the Model
                translator!!.downloadModelIfNeeded(conditions)
                    .addOnSuccessListener {
                        // Model downloaded successfully. Okay to start translating.
                        translator!!.translate(newValue)
                            .addOnSuccessListener { translatedText ->
                                // Translation successful.
                                // Sets output text to the translation of user input
                                binding.Output.text = translatedText
                            }
                            .addOnFailureListener { _ ->
                                // Error.
                            }
                    }
                    .addOnFailureListener { _ ->
                        // Model couldn’t be downloaded or other internal error.
                    }
            }
        }

        // Checks for Radio Button Change from Sources
        binding.SourceGroup.setOnCheckedChangeListener{
            // Sets our radio button based on what has been selected
                _, checkedId -> val radioButton = view.findViewById<RadioButton>(checkedId)
            // Sets our source variable to our selected Option
            source = radioButton.text.toString()

            // Checks for edge case of translate not being selected.
            // Otherwise would have crashed
            if(translate != ""){
                options = TranslatorOptions.Builder()
                    .setSourceLanguage(getTranslateLanguageCode(source))
                    .setTargetLanguage(getTranslateLanguageCode(translate)).build()
                translator = Translation.getClient(options!!)
            }
            else{
                // Asks user to select an option.
                binding.Output.text = "Please select a Translation Source!"
            }
        }

        // Checks for Radio Button Change from Translation Sources
        binding.TranslationSource.setOnCheckedChangeListener{
            // Sets our radio button based on what has been selected
                _, checkedId -> val radioButton = view.findViewById<RadioButton>(checkedId)

            // Sets our translate variable to our selected Option
            translate = radioButton.text.toString()

            // Checks in order to not crash
            // Builds our TranslationOptions and Translation Client
            if(source != "") {
                options = TranslatorOptions.Builder()
                    .setSourceLanguage(getTranslateLanguageCode(source))
                    .setTargetLanguage(getTranslateLanguageCode(translate)).build()
                translator = Translation.getClient(options!!)

                // Begins Translation and Changes text if successful
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

    // Function to get languageCode easily
    private fun getTranslateLanguageCode(languageName: String): String {
        return when (languageName) {
            "English" -> TranslateLanguage.ENGLISH
            "Spanish" -> TranslateLanguage.SPANISH
            "German" -> TranslateLanguage.GERMAN
            "en" -> TranslateLanguage.ENGLISH
            "es" -> TranslateLanguage.SPANISH
            "de" -> TranslateLanguage.GERMAN
            // Add cases for other languages as needed
            else -> "NA"
        }
    }

    // Function to replace our component with the fragment we want
    private fun replaceFrag(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentFrame, fragment)
        fragmentTransaction.commit()
    }
}
package com.angelstudio.newsapp.ui


import android.os.Bundle
import android.util.Log

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil.setContentView
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.angelstudio.newsapp.R


class SettingsFragment : PreferenceFragmentCompat() {


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        addPreferencesFromResource(R.xml.preferences)


        val categories = this.findPreference<Preference>(getString(R.string.categories)) as Preference


        val country = this.findPreference<ListPreference>(getString(R.string.country)) as ListPreference
        country.title =  country.entry
        var image = country.value.toString()
        val drawable = resources.getIdentifier(image, "drawable", context?.packageName)
        country.icon=ResourcesCompat.getDrawable(getResources(), drawable,null)



        val language = this.findPreference<ListPreference>(getString(R.string.language)) as ListPreference
        language.title =  language.entry
        var imagelan = language.value.toString()
        val drawablelan = resources.getIdentifier(imagelan, "drawable", context?.packageName)
        language.icon=ResourcesCompat.getDrawable(getResources(), drawablelan,null)

        country.setOnPreferenceChangeListener { preference, newValue ->
            if (preference is ListPreference) {
                val index = preference.findIndexOfValue(newValue.toString())
                val entry = preference.entries.get(index)
                var image = newValue.toString()
                val drawable = resources.getIdentifier(image, "drawable", context?.packageName)

                preference.title = "$entry"
                preference.icon=ResourcesCompat.getDrawable(getResources(), drawable,null)
            }

            true
        }


        language.setOnPreferenceChangeListener { preference, newValue ->
            if (preference is ListPreference) {
                val index = preference.findIndexOfValue(newValue.toString())
                val entry = preference.entries.get(index)
                var imagelanguage = newValue.toString()
                val drawablelanguage = resources.getIdentifier(imagelanguage, "drawable", context?.packageName)

                preference.title = entry
                preference.icon= ResourcesCompat.getDrawable(getResources(), drawablelanguage,null)
            }

            true
        }


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


            (activity as? AppCompatActivity)?.supportActionBar?.title = "Settings"
    }

}

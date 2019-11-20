package com.angelstudio.newsapp.ui
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProviders
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.angelstudio.newsapp.R
import com.angelstudio.newsapp.ui.feed.FeedFragmentViewModel
import com.angelstudio.newsapp.ui.feed.FeedFragmentViewModelFactory
 import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
 import java.util.*


class SettingsFragment : PreferenceFragmentCompat(), KodeinAware ,SharedPreferences.OnSharedPreferenceChangeListener{




    override val kodein by closestKodein()
    private val viewModelFactory: FeedFragmentViewModelFactory by instance()
    private lateinit var viewModel: FeedFragmentViewModel





    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this,viewModelFactory).get(FeedFragmentViewModel::class.java)

        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.Settings)


    }


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        addPreferencesFromResource(R.xml.preferences)
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);


        val country = this.findPreference<ListPreference>(getString(R.string.country_setting)) as ListPreference
        country.title =  country.entry
        var image = country.value.toString()






        val drawable = resources.getIdentifier(image, "drawable", context?.packageName)
        country.icon=ResourcesCompat.getDrawable(getResources(), drawable,null)

    /*    val language = this.findPreference<ListPreference>(getString(R.string.language_setting)) as ListPreference
        language.title =  language.entry
        var imagelan = language.value.toString()
        val drawablelan = resources.getIdentifier(imagelan, "drawable", context?.packageName)
        language.icon=ResourcesCompat.getDrawable(getResources(), drawablelan,null)  */


    val theme =this.findPreference<SwitchPreference>(getString(R.string.theme_setting)) as SwitchPreference
    theme.setOnPreferenceChangeListener{preference, newValue ->
        if(newValue == true){
            AppCompatDelegate.setDefaultNightMode( AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode( AppCompatDelegate.MODE_NIGHT_NO)
        }
        true
    }


    country.setOnPreferenceChangeListener { preference, newValue ->
        if (preference is ListPreference) {
            val index = preference.findIndexOfValue(newValue.toString())
            val entry = preference.entries.get(index)
            var imageCountry = newValue.toString()
            val drawableSource = resources.getIdentifier(imageCountry, "drawable", context?.packageName)


            preference.title = "$entry"
            preference.icon=ResourcesCompat.getDrawable(getResources(), drawableSource,null)


            refreshi()
        }


        true
    }




/*
    language.setOnPreferenceChangeListener { preference, newValue ->
        if (preference is ListPreference) {
            val index = preference.findIndexOfValue(newValue.toString())
            val entry = preference.entries.get(index)
            var imagelanguage = newValue.toString()
            val drawablelanguage = resources.getIdentifier(imagelanguage, "drawable", context?.packageName)


            val config = resources.configuration
            val local:String
            if(imagelanguage.equals("arab")){
                local="ar"
            }else{
                local=imagelanguage
            }
            val locale = Locale(local)
            Log.v("selectedlang",local)
            Locale.setDefault(locale)
            config.locale = locale
            resources.updateConfiguration(config, resources.displayMetrics)

            preference.title = entry
            preference.icon= ResourcesCompat.getDrawable(getResources(), drawablelanguage,null)
        }

        true
    }

    */

}
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        refreshi()
    }


    fun refreshi()=GlobalScope.launch {
        delay(400)
        viewModel.fetchTopHeadline()
    }



}



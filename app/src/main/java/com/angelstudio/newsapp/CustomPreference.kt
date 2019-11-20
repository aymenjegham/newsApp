package com.angelstudio.newsapp

import android.content.Context
import android.content.res.TypedArray
import android.provider.Settings.System.getString
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.preference.Preference

import androidx.preference.PreferenceViewHolder
import com.angelstudio.newsapp.ui.SettingsFragment
import com.angelstudio.newsapp.ui.feed.FeedFragmentViewModel
import com.angelstudio.newsapp.ui.feed.FeedFragmentViewModelFactory
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.custom_preference_layout.view.*
import kotlinx.android.synthetic.main.fragment_feed.view.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.KodeinPropertyDelegateProvider
import org.kodein.di.android.closestKodein
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import kotlin.reflect.KProperty

class CustomPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = 0
) : Preference(context, attrs, defStyleAttr) {

    init {
        widgetLayoutResource = R.layout.custom_preference_layout
    }


    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        with(holder.itemView) {

                val category=getPersistedString("general")
                val categoryId =getResources().getIdentifier(category, "id",context?.packageName);
                categories_list.check(categoryId)

            var lastCheckedId = View.NO_ID
            categories_list.setOnCheckedChangeListener( ChipGroup.OnCheckedChangeListener { chipGroup, selectedchip ->
                if(selectedchip == View.NO_ID) {
                    chipGroup.check(lastCheckedId)
                    return@OnCheckedChangeListener
                }
                lastCheckedId = selectedchip

                val chip = chipGroup.findViewById<Chip>(selectedchip)
                if (chip != null) {
                    persistString(getResources().getResourceEntryName(chip.id))

                 }

            })
        }
        }


}



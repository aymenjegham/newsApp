package com.angelstudio.newsapp

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.angelstudio.newsapp.databinding.CustomPreferenceLayoutBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class CustomPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = 0
) : Preference(context, attrs, defStyleAttr) {

    init {
        layoutResource = R.layout.custom_preference_layout
    }


    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        val categoriesList = CustomPreferenceLayoutBinding.bind(holder.itemView).categoriesList

        categoriesList.setOnCheckedChangeListener(null)
        val category = getPersistedString("general")
        val categoryId = context.resources.getIdentifier(category, "id", context.packageName)
        categoriesList.check(categoryId)

        var lastCheckedId = categoriesList.checkedChipId
        categoriesList.setOnCheckedChangeListener(ChipGroup.OnCheckedChangeListener { chipGroup, selectedChip ->
            if (selectedChip == View.NO_ID) {
                chipGroup.check(lastCheckedId)
                return@OnCheckedChangeListener
            }
            lastCheckedId = selectedChip

            val chip = chipGroup.findViewById<Chip>(selectedChip)
            if (chip != null) {
                persistString(context.resources.getResourceEntryName(chip.id))
            }
        })
    }
}

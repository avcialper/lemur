package com.avcialper.lemur.util.constant

import com.avcialper.lemur.R

enum class FilterType(val value: Int) {
    ALL(R.string.all),
    PERSONAL(R.string.personal),
    TEAM(R.string.team),
    MEET(R.string.meet),
    CONTINUES(R.string.continues),
    COMPLETED(R.string.completed),
    CANCELED(R.string.canceled);

    companion object {
        val size = FilterType.entries.size

        fun fromIndex(index: Int): FilterType {
            return FilterType.entries[index]
        }

    }

}

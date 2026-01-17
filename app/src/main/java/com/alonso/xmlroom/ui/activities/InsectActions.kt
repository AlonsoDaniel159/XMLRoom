package com.alonso.xmlroom.ui.activities

import com.alonso.xmlroom.data.local.entity.Insect

interface InsectActions {
    fun onInsectLongPressed(insect: Insect)
    fun onInsectClicked(insect: Insect)
}
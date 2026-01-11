package com.alonso.xmlroom

import com.alonso.xmlroom.room.entity.Insect

interface InsectActions {
    fun onInsectLongPressed(insect: Insect)
    fun onInsectClicked(insect: Insect)
}
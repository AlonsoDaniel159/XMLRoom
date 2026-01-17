package com.alonso.xmlroom.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.alonso.xmlroom.data.local.Constants
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = Constants.E_INSECTS)
data class Insect(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Constants.P_ID)
    var id: Long = 0,

    @ColumnInfo(name = Constants.P_NAME)
    var name: String = "",

    @ColumnInfo(name = Constants.P_IMG_LOCATION)
    var imgLocation: String = "",

    @ColumnInfo(name = Constants.P_USER_ID)
    var userId: Long = 0,

    @Ignore
    var inDanger: Boolean = true
): Parcelable {
    constructor(id: Long, name: String, imgLocation: String, uid: Long) :
            this(id, name, imgLocation, uid, false)
}
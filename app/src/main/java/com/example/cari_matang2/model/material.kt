package com.example.cari_matang2.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class Material(

	@field:SerializedName("thumbnail_material")
	val thumbnailMaterial: String? = null,

	@field:SerializedName("title_material")
	val titleMaterial: String? = null,

	@field:SerializedName("id_material")
	val idMaterial: Int? = null
) : Parcelable

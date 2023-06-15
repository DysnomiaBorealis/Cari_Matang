package com.example.cari_matang2.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class PartsPageItem(

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("content")
	val content: String? = null
) : Parcelable

@Parcelize
data class Content(

	@field:SerializedName("pages")
	val pages: List<PagesItem?>? = null,

	@field:SerializedName("id_content")
	val idContent: Int? = null
) : Parcelable

@Parcelize
data class PagesItem(

	@field:SerializedName("parts_page")
	val partsPage: List<PartsPageItem?>? = null
) : Parcelable

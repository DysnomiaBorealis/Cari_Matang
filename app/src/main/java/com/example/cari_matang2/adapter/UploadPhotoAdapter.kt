package com.example.cari_matang2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cari_matang2.databinding.ItemUploadPhotoBinding
import java.io.File
import com.bumptech.glide.Glide

class UploadPhotoAdapter(private val onRemovePhoto: (position: Int) -> Unit) : RecyclerView.Adapter<UploadPhotoAdapter.ViewHolder>() {
    private val photoFiles: MutableList<File?> = MutableList(8) { null }

    inner class ViewHolder(private val binding: ItemUploadPhotoBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(file: File?) {
            if (file == null) {
                binding.containerImage.visibility = View.GONE
                binding.imageViewPhoto.visibility = View.VISIBLE
                binding.btnRemove.visibility = View.GONE
            } else {
                Glide.with(binding.root)
                    .load(file)
                    .into(binding.ivPhoto)
                binding.containerImage.visibility = View.VISIBLE
                binding.imageViewPhoto.visibility = View.GONE
                binding.btnRemove.visibility = View.VISIBLE
            }

            binding.btnRemove.setOnClickListener {
                photoFiles[adapterPosition] = null
                notifyItemChanged(adapterPosition)
                onRemovePhoto(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemUploadPhotoBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = photoFiles.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(photoFiles[position])
    }

    fun addPhoto(file: File, position: Int) {
        photoFiles[position] = file
        notifyItemChanged(position)
    }

    fun removePhoto(position: Int) {
        photoFiles[position] = null
        notifyItemChanged(position)
    }
}

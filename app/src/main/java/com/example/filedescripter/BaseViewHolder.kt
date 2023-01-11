package com.example.filedescripter

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder(view: View): RecyclerView.ViewHolder(view) {
    open fun bindData(position: Int, data: Any) = Unit
}
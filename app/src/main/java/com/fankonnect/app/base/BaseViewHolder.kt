package com.fankonnect.app.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<TYPE>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var callbackAction: CallbackAction<BaseAction>? = null

    abstract fun bind(data: TYPE)

    open fun onAction(action: BaseAction) {
        this.callbackAction?.onAction(action)
    }
}
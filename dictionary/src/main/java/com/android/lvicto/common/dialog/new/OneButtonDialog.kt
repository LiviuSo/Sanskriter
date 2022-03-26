package com.android.lvicto.common.dialog.new

import android.app.Dialog
import android.os.Bundle
import androidx.annotation.LayoutRes
import com.android.lvicto.common.base.BaseDialog2
import kotlinx.android.synthetic.main.dialog_error.*
import java.lang.IllegalStateException

abstract class OneButtonDialog : BaseDialog2() {

    companion object {
        const val ARG_MESSAGE = "dialog_arg_message"
        const val ARG_CAPTION = "dialog_arg_caption"
    }

    protected var mMessage: String? = null
    protected var mCaption: String? = null
    protected var mAction: ((OneButtonDialog) -> Unit)? = null

    protected fun addArguments(message: String, caption: String) {
        this.arguments = Bundle(2).also {
            it.putString(ARG_MESSAGE, message)
            it.putString(ARG_CAPTION, caption)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (arguments == null) {
            throw IllegalStateException("arguments musn't be null")
        }

        mMessage = requireArguments().getString(ARG_MESSAGE)
        mCaption = requireArguments().getString(ARG_CAPTION)

        return Dialog(requireContext()).apply {
            this.setContentView(getLayout())
            txtMessage.text = mMessage
            btnPositive.text = mCaption
            btnPositive.setOnClickListener {
                onButtonClicked()
            }
        }
    }

    protected fun onButtonClicked() {
        mAction?.invoke(this)
    }

    @LayoutRes
    abstract fun getLayout(): Int
}
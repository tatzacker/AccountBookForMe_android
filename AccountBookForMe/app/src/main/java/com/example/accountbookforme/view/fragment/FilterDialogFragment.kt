package com.example.accountbookforme.view.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.accountbookforme.R
import com.example.accountbookforme.databinding.DialogFilterBinding
import com.example.accountbookforme.model.Filter

class FilterDialogFragment(
    private val title: Int,
    private val filter: Filter?
) : DialogFragment() {

    private lateinit var listener: OnAddFilterListener

    private var _binding: DialogFilterBinding? = null
    private val binding get() = _binding!!

    // 結果を渡すリスナー
    interface OnAddFilterListener {
        fun create(name: String)

        fun update(filter: Filter)

        fun delete(filter: Filter)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        when {
            context is OnAddFilterListener -> listener = context
            parentFragment is OnAddFilterListener -> listener =
                parentFragment as OnAddFilterListener
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        _binding = DialogFilterBinding.inflate(LayoutInflater.from(context))

        // 前画面から渡されたデータがあれば代入
        if (filter != null) {
            binding.editFilterName.setText(filter.name)
        }

        val builder = AlertDialog.Builder(context)
            .setView(binding.root)
            .setTitle(title)
            .setPositiveButton(R.string.label_save, null)
            .setNeutralButton(R.string.label_cancel, null)

        if (filter != null) {
            // 編集時は削除ボタンも表示する
            builder.setNegativeButton(R.string.label_delete) { _, _ ->
                // TODO: 関連する支出や品物がある場合は消せないようにする
                // 削除
                listener.delete(filter)
            }
        }

        val dialog = builder.create()
        dialog.show()
        // OKボタンタップ時の処理
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            // バリデーションチェック
            if (validationCheck()) {
                // 成功

                // 入力内容を反映
                // TODO: いずれは双方向データバインディングで

                if (filter == null) {
                    // 新規作成
                    listener.create(binding.editFilterName.text.toString())
                } else {
                    // 更新
                    filter.name = binding.editFilterName.text.toString()
                    listener.update(filter)
                }
                // ダイアログを閉じる
                dialog.dismiss()
            }
        }

        return dialog
    }

    /**
     * バリデーションチェック
     */
    private fun validationCheck(): Boolean {

        // 名称が入力されていない
        return if (TextUtils.isEmpty(binding.editFilterName.text.toString())) {
                // エラートーストを出す
                Toast.makeText(activity, getString(R.string.name_is_empty), Toast.LENGTH_LONG)
                    .show()
                false
            } else {
            true
        }
    }
}
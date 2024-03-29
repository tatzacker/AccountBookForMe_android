package com.example.accountbookforme.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.accountbookforme.databinding.SimpleListItemBinding
import com.example.accountbookforme.model.Expense
import com.example.accountbookforme.util.DateUtil
import com.example.accountbookforme.util.Utils

class StoreExpenseListAdapter : RecyclerView.Adapter<StoreExpenseListAdapter.ExpenseViewHolder>() {

    private lateinit var listener: OnExpenseClickListener
    private var expenseList: List<Expense> = listOf()

    // 結果を渡すリスナー
    interface OnExpenseClickListener {
        fun onItemClick(expense: Expense)
    }

    // リスナーをセット
    fun setOnExpenseClickListener(listener: OnExpenseClickListener) {
        this.listener = listener
    }

    open class ExpenseViewHolder(binding: SimpleListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val purchasedAt: TextView = binding.label
        val total: TextView = binding.value
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = SimpleListItemBinding.inflate(layoutInflater, parent, false)
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {

        val expense = expenseList[position]

        holder.purchasedAt.text = DateUtil.formatDate(expense.purchasedAt, DateUtil.DATE_YYYYMMDD)
        holder.total.text = Utils.calcExpenseTotal(expenseList)?.let { Utils.totalWithPercentage(expense.total, it) }

        // セルのクリックイベントにリスナーをセット
        holder.itemView.setOnClickListener {
            listener.onItemClick(expense)
        }
    }

    // リストのサイズ取得
    override fun getItemCount() = expenseList.size

    // カテゴリごとの支出額リストをセットして変更を通知
    fun setExpenseStoreListItems(expenseList: List<Expense>) {
        this.expenseList = expenseList
        notifyDataSetChanged()
    }
}
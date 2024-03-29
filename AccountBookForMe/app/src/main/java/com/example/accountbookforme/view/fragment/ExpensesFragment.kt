package com.example.accountbookforme.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.accountbookforme.MMApplication
import com.example.accountbookforme.R
import com.example.accountbookforme.adapter.ExpenseListAdapter
import com.example.accountbookforme.databinding.FragmentListWithMonthBinding
import com.example.accountbookforme.model.Expense
import com.example.accountbookforme.util.DateUtil
import com.example.accountbookforme.util.Utils
import com.example.accountbookforme.view.activity.DetailActivity
import com.example.accountbookforme.viewmodel.ExpensesViewModel
import com.example.accountbookforme.viewmodel.ExpensesViewModelFactory
import kotlinx.coroutines.launch

class ExpensesFragment : Fragment() {

    private var _binding: FragmentListWithMonthBinding? = null
    private val binding get() = _binding!!

    private val expensesViewModel: ExpensesViewModel by activityViewModels {
        ExpensesViewModelFactory(
            (activity?.application as MMApplication).expenseRepository,
            (activity?.application as MMApplication).epRepository
        )
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var expenseListAdapter: ExpenseListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        _binding = FragmentListWithMonthBinding.inflate(inflater, container, false)
        val view = binding.root

        // 今月を表示
        binding.month.text = DateUtil.getMonth()

        recyclerView = binding.list
        expenseListAdapter = ExpenseListAdapter()

        // セルのクリック処理
        expenseListAdapter.setOnExpenseClickListener(
            object : ExpenseListAdapter.OnExpenseClickListener {
                override fun onItemClick(expense: Expense) {
                    val intent = Intent(context, DetailActivity::class.java)
                    // 支出IDを渡す
                    intent.putExtra("expenseId", expense.id)
                    // 支出詳細画面に遷移する
                    startActivity(intent)
                }
            }
        )

        val linearLayoutManager = LinearLayoutManager(view.context)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = expenseListAdapter

        // セルの区切り線表示
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                view.context,
                linearLayoutManager.orientation
            )
        )

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 支出リストの監視開始
        expensesViewModel.expenseList.observe(viewLifecycleOwner, { expenseList ->
            lifecycleScope.launch {
                // 支出の総額を取得
                expenseList.forEach { expense ->
                    expense.total = expensesViewModel.calcTotal(expense.id)
                }
                expenseListAdapter.setExpenseListItems(expenseList)
                // 支出リストの合計総額を表示
                binding.allTotal.text = expensesViewModel.expenseList.value?.let {
                    Utils.calcExpenseTotal(it)
                }?.let { Utils.convertToStrDecimal(it) }
            }
        })
    }

    // メニュー表示
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_add, menu)
    }

    // メニュータップ時の処理設定
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add -> {
                val intent = Intent(activity, DetailActivity::class.java)
                startActivity(intent)
            }
        }

        return true
    }
}
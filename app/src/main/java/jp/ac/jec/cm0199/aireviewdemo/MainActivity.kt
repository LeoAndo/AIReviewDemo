package jp.ac.jec.cm0199.aireviewdemo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var editQuery: EditText
    private lateinit var btnSearch: Button
    private lateinit var progress: ProgressBar
    private lateinit var textEmpty: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RepoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        // スプラッシュスクリーンをインストール
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        // システムバーとの重なりを調整
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ViewModelの初期化
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        // UI要素の取得
        editQuery = findViewById(R.id.editQuery)
        btnSearch = findViewById(R.id.btnSearch)
        progress = findViewById(R.id.progress)
        textEmpty = findViewById(R.id.textEmpty)
        recyclerView = findViewById(R.id.recyclerResults)

        adapter = RepoAdapter { item ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.htmlUrl))
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        // 観測
        viewModel.loading.observe(this) { isLoading ->
            progress.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (isLoading) {
                textEmpty.visibility = View.VISIBLE
                textEmpty.text = getString(R.string.loading)
            }
        }

        viewModel.error.observe(this) { code ->
            if (code == null) return@observe
            textEmpty.visibility = View.VISIBLE
            textEmpty.text = when (code) {
                "NETWORK" -> getString(R.string.error_network)
                "RATE_LIMIT" -> getString(R.string.error_rate_limit)
                else -> getString(R.string.error_unknown)
            }
        }

        viewModel.results.observe(this) { list ->
            adapter.submitList(list)
            if (list.isEmpty()) {
                textEmpty.visibility = View.VISIBLE
                textEmpty.text = getString(R.string.empty_no_results)
            } else {
                textEmpty.visibility = View.GONE
            }
        }

        // 検索実行
        btnSearch.setOnClickListener {
            textEmpty.visibility = View.GONE
            viewModel.search(editQuery.text.toString())
        }

        // クリック処理は ViewHolder 内で設定済み
    }

    private inner class RepoAdapter(
        private val onClick: (RepoItem) -> Unit,
    ) : RecyclerView.Adapter<RepoViewHolder>() {
        private val items = ArrayList<RepoItem>()

        fun submitList(newItems: List<RepoItem>) {
            items.clear()
            items.addAll(newItems)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_2, parent, false)
            return RepoViewHolder(view, onClick)
        }

        override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size
    }

    private inner class RepoViewHolder(
        itemView: View,
        private val onClick: (RepoItem) -> Unit,
    ) : RecyclerView.ViewHolder(itemView) {
        private val text1: TextView = itemView.findViewById(android.R.id.text1)
        private val text2: TextView = itemView.findViewById(android.R.id.text2)
        private var current: RepoItem? = null

        init {
            itemView.setOnClickListener {
                current?.let(onClick)
            }
        }

        fun bind(item: RepoItem) {
            current = item
            text1.text = "${item.fullName} ⭐${item.stars}"
            text2.text = item.description ?: ""
        }
    }
}

package com.example.localdatabase

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.localdatabase.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: HomeworkAdapter

    val resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.data != null) {
            when (result.resultCode) {
                AddHomework.RESULT_ADD -> {
                    val homework =
                        result.data?.getParcelableExtra<Homework>(AddHomework.EXTRA_HOMEWORK) as Homework
                    adapter.addItem(homework)
                    binding.rvHomework.smoothScrollToPosition(adapter.itemCount - 1)
                    showSnackbarMessage("Data berhasil ditambahkan")
                }
                AddHomework.RESULT_UPDATE -> {
                    val homework =
                        result.data?.getParcelableExtra<Homework>(AddHomework.EXTRA_HOMEWORK) as Homework
                    val position =
                        result.data?.getIntExtra(AddHomework.EXTRA_POSITION, 0) ?: 0

                    adapter.updateItem(position, homework)
                    binding.rvHomework.smoothScrollToPosition(position)
                    showSnackbarMessage("Data berhasil diubah")
                }
                AddHomework.RESULT_DELETE -> {
                    val position =
                        result.data?.getIntExtra(AddHomework.EXTRA_POSITION, 0) ?: 0

                    adapter.removeItem(position)
                    showSnackbarMessage("Data berhasil dihapus")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Homework"

        // Setup RecyclerView
        binding.rvHomework.layoutManager = LinearLayoutManager(this)
        binding.rvHomework.setHasFixedSize(true)

        adapter = HomeworkAdapter(object : HomeworkAdapter.OnItemClickCallback {
            override fun onItemClicked(selectedHomework: Homework?, position: Int?) {
                val intent = Intent(this@MainActivity, AddHomework::class.java)
                intent.putExtra(AddHomework.EXTRA_HOMEWORK, selectedHomework)
                intent.putExtra(AddHomework.EXTRA_POSITION, position)
                resultLauncher.launch(intent)
            }
        })
        binding.rvHomework.adapter = adapter

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddHomework::class.java)
            resultLauncher.launch(intent)
        }

        // Memuat data homework jika tidak ada savedInstanceState
        if (savedInstanceState == null) {
            loadHomeworkAsync()
        } else {
            val list = savedInstanceState.getParcelableArrayList<Homework>(EXTRA_STATE)
            list?.let {
                adapter.listHomework = it
            }
        }
    }

    // Fungsi untuk memuat data homework secara asinkron
    private fun loadHomeworkAsync() {
        runBlocking {
            val homeworkHelper = HomeworkHelper.getInstance(applicationContext)

            // Pastikan homeworkHelper tidak null sebelum digunakan
            homeworkHelper?.let { helper ->
                helper.open()
                val deferredHomework = async(Dispatchers.IO) {
                    val cursor = helper.queryAll()
                    MappingHelper.mapCursorToArrayList(cursor)
                }
                val homework = deferredHomework.await()
                if (homework.isNotEmpty()) {
                    adapter.listHomework = homework
                } else {
                    adapter.listHomework = ArrayList()
                    showSnackbarMessage("Data tidak ada")
                }
                helper.close()
            } ?: showSnackbarMessage("Gagal mengakses database")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, adapter.listHomework)
    }

    private fun showSnackbarMessage(message: String) {
        Snackbar.make(binding.rvHomework, message, Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
    }
}

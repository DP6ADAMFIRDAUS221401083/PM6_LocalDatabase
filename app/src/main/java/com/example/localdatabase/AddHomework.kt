package com.example.localdatabase

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.localdatabase.databinding.ActivityAddHomeworkBinding
import java.text.SimpleDateFormat
import java.util.*

class AddHomework : AppCompatActivity() {
    private var isEdit = false
    private var homework: Homework? = null
    private var position: Int = 0
    private lateinit var homeworkHelper: HomeworkHelper
    private lateinit var binding: ActivityAddHomeworkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHomeworkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Menginisialisasi HomeworkHelper
        homeworkHelper = HomeworkHelper.getInstance(applicationContext)!!
        homeworkHelper.open()

        // Cek apakah ada data homework yang diteruskan
        homework = intent.getParcelableExtra(EXTRA_HOMEWORK)

        if (homework != null) {
            position = intent.getIntExtra(EXTRA_POSITION, 0)
            isEdit = true
        } else {
            homework = Homework()  // Membuat objek Homework baru jika tidak ada
        }

        val actionBarTitle: String
        val btnTitle: String

        // Jika ada data homework berarti ini adalah mode edit
        if (isEdit) {
            actionBarTitle = "Ubah"
            btnTitle = "Update"

            homework?.let {
                binding.edtTitle.setText(it.title)
                binding.edtDescription.setText(it.description)
            }
        } else {
            actionBarTitle = "Tambah"
            btnTitle = "Simpan"
        }

        supportActionBar?.title = actionBarTitle
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnSubmit.text = btnTitle
        binding.btnSubmit.setOnClickListener { addNewHomework() }
        binding.btnDelete.setOnClickListener { showAlertDialog(ALERT_DIALOG_DELETE) }
    }

    // Fungsi untuk menambahkan atau mengubah data homework
    private fun addNewHomework() {
        val title = binding.edtTitle.text.toString().trim()
        val description = binding.edtDescription.text.toString().trim()

        if (title.isEmpty()) {
            binding.edtTitle.error = "Title tidak boleh kosong"
            return
        }

        homework?.title = title
        homework?.description = description

        val intent = Intent()
        intent.putExtra(EXTRA_HOMEWORK, homework)
        intent.putExtra(EXTRA_POSITION, position)

        val values = ContentValues()
        values.put(DatabaseContract.HomeworkColumns.TITLE, title)          // Menggunakan DatabaseContract.HomeworkColumns.TITLE
        values.put(DatabaseContract.HomeworkColumns.DESCRIPTION, description)  // Menggunakan DatabaseContract.HomeworkColumns.DESCRIPTION

        if (isEdit) {
            // Mengupdate data homework yang ada
            val result = homeworkHelper.update(homework?.id.toString(), values)
            if (result > 0) {
                setResult(RESULT_UPDATE, intent)
                finish()
            } else {
                Toast.makeText(this, "Gagal memperbarui data", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Menambahkan data homework baru
            homework?.date = getCurrentDate()
            values.put(DatabaseContract.HomeworkColumns.DATE, homework?.date)  // Menggunakan DatabaseContract.HomeworkColumns.DATE
            val result = homeworkHelper.insert(values)
            if (result > 0) {
                homework?.id = result.toInt()
                setResult(RESULT_ADD, intent)
                finish()
            } else {
                Toast.makeText(this, "Gagal menambah data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }

    override fun onBackPressed() {
        showAlertDialog(ALERT_DIALOG_CLOSE)
        super.onBackPressed()
    }

    // Menampilkan dialog konfirmasi untuk batal atau hapus
    private fun showAlertDialog(type: Int) {
        val isDialogClose = type == ALERT_DIALOG_CLOSE
        val dialogTitle: String
        val dialogMessage: String

        if (isDialogClose) {
            dialogTitle = "Batal"
            dialogMessage = "Apakah Anda ingin membatalkan memperbarui data?"
        } else {
            dialogTitle = "Hapus Homework"
            dialogMessage = "Apakah Anda yakin ingin menghapus item ini?"
        }

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(dialogTitle)
            .setMessage(dialogMessage)
            .setCancelable(false)
            .setPositiveButton("Ya") { _, _ ->
                if (isDialogClose) {
                    finish()
                } else {
                    val result = homeworkHelper.deleteById(homework?.id.toString())
                    if (result > 0) {
                        val intent = Intent()
                        intent.putExtra(EXTRA_POSITION, position)
                        setResult(RESULT_DELETE, intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Gagal menghapus data", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.cancel()
            }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    companion object {
        const val EXTRA_HOMEWORK = "extra_homework"
        const val EXTRA_POSITION = "extra_position"
        const val RESULT_ADD = 101
        const val RESULT_UPDATE = 201
        const val RESULT_DELETE = 301
        const val ALERT_DIALOG_CLOSE = 10
        const val ALERT_DIALOG_DELETE = 20
    }
}

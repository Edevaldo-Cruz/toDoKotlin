package com.example.todolistkotlin.ui

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.todolistkotlin.databinding.ActivityAddTaskBinding
import com.example.todolistkotlin.datasource.TaskDataSource
import com.example.todolistkotlin.extensions.format
import com.example.todolistkotlin.extensions.text
import com.example.todolistkotlin.model.Task
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat.CLOCK_24H
import java.util.*

/*
    -> incluir no build.grade app nas dependencias do android o seguinte codigo.

 buildFeatures {

       viewBinding = true
    }

    -> Adicionar AddTaskActivity no Manifest clicando segurando Ctrl.
 */

class AddTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(TASK_ID)) {
            val taskId = intent.getIntExtra(TASK_ID, 0)
            TaskDataSource.finById(taskId)?.let {
                binding.tilTitle.text = it.title
                binding.tilDate.text = it.date
                binding.tilHour.text = it.hour
            }
        }

        /*
            Inserção de data e hora
                =>Date(java util)
                => AppExtensions criado para formatar a data padrão brasileiro.
         */
        
        insertListeners()
    }

    private fun insertListeners() {
        binding.tilDate.editText?.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().build()

            datePicker.addOnPositiveButtonClickListener {
                val timeZone = TimeZone.getDefault()
                val offset = timeZone.getOffset(Date().time) * -1
                binding.tilDate.text = Date(it + offset).format()
            }
            datePicker.show(supportFragmentManager, "DATE_PICKER_TAG")
        }

        binding.tilHour.editText?.setOnClickListener() {
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(CLOCK_24H).build()
            timePicker.addOnPositiveButtonClickListener {
                val hour = if (timePicker.hour in 0..9) "0${timePicker.hour}" else timePicker.hour
                val minute = if (timePicker.minute in 0..9) "0${timePicker.minute}" else timePicker.minute

                binding.tilHour.text = "${hour}:${minute}"
            }

            timePicker.show(supportFragmentManager,"null")
        }
        binding.btnCancel.setOnClickListener {
            // Cancela a operação
            finish()
        }
        binding.btnNewTask.setOnClickListener {
          // Adiciona tarefa
            val task = Task(
                title = binding.tilTitle.text,
                date = binding.tilDate.text,
                hour = binding.tilHour.text,
                id = intent.getIntExtra(TASK_ID, 0)
            )
            TaskDataSource.insertTask(task)
            setResult(Activity.RESULT_OK)
            finish()
            /*
                => Para fazer testes e visualizar no logcat.
            Log.e("TAG", "insertListeners: " + TaskDataSource.getList() )
             */
        }
    }

    companion object {
        const val TASK_ID = "task_id"
    }
}
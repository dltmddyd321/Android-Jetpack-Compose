package com.example.viewmodeladdnum

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.viewmodeladdnum.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        binding.resultText.text= viewModel.getTotal().toString()
        binding.btn.setOnClickListener {
            if(!isNumber(binding.editText.text.toString())) {
                Log.d("TEST", "숫자 형식이 아닙니다.")
                return@setOnClickListener
            }

            viewModel.setTotal(binding.editText.text.toString().toInt())
            binding.resultText.text = viewModel.getTotal().toString()
        }
    }

    private fun isNumber(s: String): Boolean {
        return when(s.toIntOrNull())
        {
            null -> false
            else -> true
        }
    }
}
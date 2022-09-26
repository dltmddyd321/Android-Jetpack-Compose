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
    private lateinit var viewModelFactory: MainViewModelFactory
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        //뷰모델 초기화 공장 -> 시작 인자 값을 넘기고 뷰모델 객체 생성을 돕는다.
        viewModelFactory = MainViewModelFactory(100)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
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
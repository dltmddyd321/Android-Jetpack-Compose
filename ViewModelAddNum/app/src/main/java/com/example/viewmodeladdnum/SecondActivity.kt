package com.example.viewmodeladdnum

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.viewmodeladdnum.databinding.ActivitySecondBinding

class SecondActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySecondBinding
    private lateinit var viewModel: SecondViewModel
    private lateinit var viewModelFactory: SecondViewModelFactory
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_second)
        viewModelFactory = SecondViewModelFactory(-1)
        viewModel = ViewModelProvider(this, viewModelFactory)[SecondViewModel::class.java]

        //바인딩을 위해 뷰 모델 객체에 실제 라이프사이클 소유자를 제공해야한다.
        binding.lifecycleOwner = this

        binding.secondViewModel = viewModel

//        viewModel.cntData.observe(this) {
//            binding.textView.text = it.toString()
//        }
    }
}
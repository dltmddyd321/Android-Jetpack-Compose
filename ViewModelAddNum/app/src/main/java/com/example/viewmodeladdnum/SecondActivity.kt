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

        viewModel.cntData.observe(this) {
            binding.textView.text = it.toString()
        }

        binding.button.setOnClickListener {
            viewModel.updateCnt()
        }
    }
}
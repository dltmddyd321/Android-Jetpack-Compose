package com.example.viewmodeladdnum

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.viewmodeladdnum.databinding.ActivityThirdBinding

//동일 위젯 내에서 사용자에 입력에 따른 즉각적인 변화를 알려주기 위해서는 양방향 데이터 바인딩을 적용한다.
class ThirdActivity : AppCompatActivity() {

    private lateinit var binding: ActivityThirdBinding
    private lateinit var viewModel: ThirdViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_third)
        viewModel = ViewModelProvider(this)[ThirdViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }
}
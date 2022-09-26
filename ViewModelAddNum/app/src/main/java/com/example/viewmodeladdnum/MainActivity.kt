package com.example.viewmodeladdnum

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.viewmodeladdnum.databinding.ActivityMainBinding

/*
Observer: 데이터가 변경되는지 감시하고 있다가 UI 컨트롤러에게 알려준다.
알림을 감지한 UI는 해당 데이터를 통해 UI를 업데이트한다.
 */

/*
LiveData: 수명 주기를 인식하는 관찰 가능한 데이터 홀더 클래스
- 앱 데이터가 변경되면 자동적으로 업데이트함으로서, 항상 최신의 데이터 유지가 가능하다.
- 옵저버와 관련된 수명 주기가 종료되면 같이 스스로 종료된다. 따라서 메모리 누수 가능성이 낮다.
- 앱의 다른 구성 요소 간에 앱 서비스를 공유할 수 있다.
 */
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

        //LiveData Observing을 통해 값을 변경한다.
        viewModel.totalData.observe(this) {
            binding.resultText.text = it.toString()
        }

        binding.btn.setOnClickListener {
            if(!isNumber(binding.editText.text.toString())) {
                Log.d("TEST", "숫자 형식이 아닙니다.")
                return@setOnClickListener
            }

            viewModel.setTotal(binding.editText.text.toString().toInt())
        }

        binding.nextBtn.setOnClickListener {
            startActivity(Intent(this, SecondActivity::class.java))
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
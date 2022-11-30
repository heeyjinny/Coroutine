package com.heeyjinny.coroutine

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.heeyjinny.coroutine.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

//인터넷 주소를 넣으면 이미지를 불러오는 앱 생성
//1
//인터넷 이미지를 다운로드 하기 위해 인터넷 권한 선언필요
//AndroidManifest.xml 에서 권한 설정

//2
//탑레벨 함수 작성
//suspend키워드를 사용해 코루틴을 만들어 함수 loadImage()생성
//imageUrl을 문자열로 파라미터로 받아 Bitmap형식으로 리턴하는 함수
suspend fun loadImage(imageUrl: String): Bitmap {
    //2-1
    //파라미터로 전달받은 주소로 URL객체 생성
    val url = URL(imageUrl)
    //2-2
    //URL이 가지고 있는 openStream의 변수 생성
    val stream = url.openStream()
    //2-3
    //주소를 열어 비트맵 이미지 저장한 값을 반환
    return BitmapFactory.decodeStream(stream)
}

class MainActivity : AppCompatActivity() {

    //뷰바인딩
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //3
        //다운로드 버튼을 클릭하면 주소를 전달해 이미지를 보여주는 코드 생성
        binding.btnDownload.setOnClickListener {
            //3-1
            //코루틴 스코프 추가,
            //디스패처는 Main으로 입력해 UI관련 요소들을 다룰 수 있도록 구성
            CoroutineScope(Dispatchers.Main).launch {
                //3-2
                //버튼이 클릭되면 가장먼저 코루틴 실행
                //버튼이 클릭되면 프로그래스바가 화면에 보여지도록 설정
                binding.progress.visibility= View.VISIBLE

                //3-3
                //에디트텍스트에 입력된 값 문자열로 가져와 변수url에 저장
                val url = binding.editUrl.text.toString()

                //3-4
                //loadImage()함수를 호출하면서 url전달
                //백그라운드를 처리하는 IO컨텍스트에서 진행되어야함
                //withContext()문을 사용해 컨텍스트를 IO로 전환하고 변수bitmap에 저장
                val bitmap = withContext(Dispatchers.IO){
                    loadImage(url)
                }

                //3-5
                //loadImage()함수에서 비트맵 생성하여
                //변수bitmap에 저장될 때까지 다음줄이 실행되지않고 멈춤
                //이미지뷰에 변수bitmap의 값 입력
                binding.imagePreview.setImageBitmap(bitmap)

                //3-6
                //프로그래스바는 다시 안보이게 설정
                binding.progress.visibility = View.GONE

            }//코루틴스코프

        }//btnDownload

    }//onCreate
}//MainActivity
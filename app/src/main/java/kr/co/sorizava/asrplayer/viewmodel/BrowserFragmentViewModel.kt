package kr.co.sorizava.asrplayer.viewmodel

import android.text.Spannable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.co.sorizava.asrplayer.model.BrowserFragmentModel

class BrowserFragmentViewModel : ViewModel(){

    //Model을 가져온다.

    private val mBrowserFragmentModel : BrowserFragmentModel = BrowserFragmentModel()
    var mSpeakerColorList : MutableList<Color>? = mBrowserFragmentModel.mSpeakerColorList

    init {

    }

    /************** mvvm *******************/
    //1. model의 데이터와 vm을 연결한다.

    //최종 텍스트가 담기는 공간
    private var mCSSpeakerText_MutableLiveData : MutableLiveData<MutableList<BrowserFragmentModel.csSpeakerText>> = MutableLiveData<MutableList<BrowserFragmentModel.csSpeakerText>>()
    val mCSSpeakerText_LiveData : LiveData<MutableList<BrowserFragmentModel.csSpeakerText>> //= mPreCSSpeakerText_MutableLiveData!!
        get() = mCSSpeakerText_MutableLiveData!!

    //과거 텍스트와 현재 텍스트가 담기는 temp
    private var mCSTemp_SpeakerText_MutableLiveData : MutableLiveData<MutableList<BrowserFragmentModel.csSpeakerText>> = MutableLiveData<MutableList<BrowserFragmentModel.csSpeakerText>>()
    val mCSTemp_SpeakerText_LiveData : LiveData<MutableList<BrowserFragmentModel.csSpeakerText>> //= mPreCSSpeakerText_MutableLiveData!!
        get() = mCSTemp_SpeakerText_MutableLiveData!!


    fun setTranscript(_transcript : String? , _isFinal : Boolean = false, _speakerNum : Int = 0, _startTime : Double = 0.0 ,_endTime : Double = 0.0 )
    {
        mBrowserFragmentModel.SetText(_transcript  , _isFinal , _speakerNum ,_startTime ,_endTime)
        this.getSpeakerText();
    }

    fun clearSpeakerText() {

        mBrowserFragmentModel.mPreCSSpeakerText_LiveData.value!!.clear()
        mBrowserFragmentModel.mNowCSSpeakerText_LiveData.value!!.mText =""
        if( mCSSpeakerText_MutableLiveData.value != null) mCSSpeakerText_MutableLiveData.value!!.clear()

    }

    private fun getSpeakerText() : LiveData<MutableList<BrowserFragmentModel.csSpeakerText>>
    {
        //과거 문장들과 현재 문장을 가져온다.

        //1. 화면에 뿌려줘야할 데이터 구조체를 초기화한다.
        if ( mCSSpeakerText_MutableLiveData.value != null){
            mCSTemp_SpeakerText_MutableLiveData.value!!.clear()
            mCSSpeakerText_MutableLiveData.value!!.clear()
        }
        else {
            mCSTemp_SpeakerText_MutableLiveData.value = mutableListOf<BrowserFragmentModel.csSpeakerText>()
            mCSSpeakerText_MutableLiveData.value = mutableListOf<BrowserFragmentModel.csSpeakerText>()
        }


        //2. 과거 문장을 복사한다.
        mCSTemp_SpeakerText_MutableLiveData.value!!.addAll(mBrowserFragmentModel.mPreCSSpeakerText_LiveData.value!!)

        //3. 현재문장을 추가한다.
        if( mBrowserFragmentModel.mNowCSSpeakerText_LiveData.value?.mText != "" ){
            mCSTemp_SpeakerText_MutableLiveData.value!!.add(mBrowserFragmentModel.mNowCSSpeakerText_LiveData.value!!)
        }

        mCSSpeakerText_MutableLiveData.value = mCSTemp_SpeakerText_MutableLiveData.value

        return mCSSpeakerText_LiveData;
    }

    //화면에 표출될 spannable
    var mSpannable_MutableLiveData : MutableLiveData<Spannable> = MutableLiveData<Spannable>()
    val mSpannable_LiveData : LiveData<Spannable> //= mPreCSSpeakerText_MutableLiveData!!
        get() = mSpannable_MutableLiveData



}
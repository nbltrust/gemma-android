package com.example.yiran.gemma.Modules.Me.Security.CreateLockPattern;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.cybex.gma.client.R;
import com.example.yiran.gemma.Modules.Me.Security.SecuritySettingActivity;
import com.github.ihsg.patternlocker.PatternLockerView;

import java.util.ArrayList;
import java.util.List;

public class CreateLockPatternActivity extends AppCompatActivity{

    private List<Integer> mHitList;
    private int round;//第几次输入
    private static final int INIT = 0;
    private SharedPreferences sp;

    private TextView mTextView;
    private PatternLockerView mPattern;
    private static final String Tag = "CreatePatternActivity";
    private boolean isPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lock_pattern);
        isPass = true;
        round = INIT;
        mTextView = findViewById(R.id.tv_lock_pattern_hint);
        mTextView.setText("请输入手势密码");

        mPattern = findViewById(R.id.patternLocker);
        mPattern.setOnPatternChangedListener(new com.github.ihsg.patternlocker.OnPatternChangeListener() {
            //处理LOCK Pattern逻辑
            @Override
            public void onStart(PatternLockerView view){
                Log.d(Tag,"onStart executed");
                round++;
            }

            @Override
            public void onChange(PatternLockerView view, List<Integer> hitList){
                //Log.d(Tag,"onChange executed");
            }

            @Override
            public void onComplete(PatternLockerView view, List<Integer> hitList){
                //Log.d(Tag,"onComplete executed");
                int len = hitList.size();

                //创建pattern
                switch (round){
                    case 1 ://第一遍输入pattern，储存，调整view
                        Log.d(Tag, "case 1 executed");
                        mHitList = new ArrayList<>();
                        for (int i = 0; i < len; i++){
                            mHitList.add(hitList.get(i));
                        }

                        mTextView.setText("请再次输入解锁密码，以验证");
                        break;

                    case 2://第二遍输入pattern，比对验证

                        Log.d(Tag, "case 2 executed");

                        if (mHitList.size() != hitList.size()){//长度不等，验证未过
                            isPass = false;
                            mTextView.setText("输入错误，请再次输入");
                            break;
                        }else{//长度相等，进一步验证
                            for (int j =0; j < len; j++){
                                if ( !mHitList.get(j).equals(hitList.get(j)) ){//验证未过，停留在本页面
                                    isPass = false;
                                    mTextView.setText("输入错误，请再次输入");
                                    break;
                                }
                            }

                            if (isPass){
                                //savePattern(mHitList);
                                Intent intent = new Intent(CreateLockPatternActivity.this, SecuritySettingActivity.class );
                                startActivity(intent);
                                Toast.makeText(CreateLockPatternActivity.this,"设置手势密码成功",Toast.LENGTH_LONG).show();
                            }else{
                                Intent intent = new Intent(CreateLockPatternActivity.this, SecuritySettingActivity.class );
                                startActivity(intent);
                                Toast.makeText(CreateLockPatternActivity.this,"设置手势密码失败，请重试",Toast.LENGTH_LONG).show();
                            }
                        }
                        break;

                    default:
                        Intent intent = new Intent(CreateLockPatternActivity.this, SecuritySettingActivity.class );
                        startActivity(intent);
                        Toast.makeText(CreateLockPatternActivity.this,"设置手势密码失败，请重试",Toast.LENGTH_LONG).show();
                        break;
                }
            }

            @Override
            public void onClear(PatternLockerView view){
                //Log.d(Tag, "onClear executed");
            }
        });


    }

    //JAVA业务逻辑，后期往presenter中封装
    public String listToString(List<Integer> list){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++){
            sb.append(list.get(i));
        }
        return sb.toString().trim();
    }

    private void savePattern(List<Integer> pattern){
        sp = getSharedPreferences("pattern", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("gesture", listToString(pattern));
        editor.commit();
    }

    private String getPattern(){
        String pattern = sp.getString("pattern","error,no such file");
        return pattern;
    }



    @Override
    public void onResume(){
        super.onResume();
    }

}

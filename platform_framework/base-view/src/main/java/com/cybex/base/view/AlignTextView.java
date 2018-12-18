package com.cybex.base.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;


import com.cybex.gma.framework.view.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 文字适配控件
 * copy from me.codeboy.android:align-text-view:2.3.2
 * fix:为了解决原本框架中未对width==0时适配,导致calc方法死循环
 */
public class AlignTextView extends android.support.v7.widget.AppCompatTextView {
    private float textHeight; // 单行文字高度
    private float textLineSpaceExtra = 0; // 额外的行间距
    private int width; // textView宽度
    private List<String> lines = new ArrayList<String>(); // 分割后的行
    private List<Integer> tailLines = new ArrayList<Integer>(); // 尾行
    private Align align = Align.ALIGN_LEFT; // 默认最后一行左对齐
    private boolean firstCalc = true;  // 初始化计算

    private float lineSpacingMultiplier = 1.0f;
    private float lineSpacingAdd = 0.0f;

    private int originalHeight = 0; //原始高度
    private int originalLineCount = 0; //原始行数
    private int originalPaddingBottom = 0; //原始paddingBottom
    private boolean setPaddingFromMe = false;

    // 尾行对齐方式
    public enum Align {
        ALIGN_LEFT, ALIGN_CENTER, ALIGN_RIGHT  // 居中，居左，居右,针对段落最后一行
    }

    public AlignTextView(Context context) {
        super(context);
        setTextIsSelectable(false);
    }

    public AlignTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTextIsSelectable(false);

        int[] attributes = new int[]{android.R.attr.lineSpacingExtra, android.R.attr.lineSpacingMultiplier};
        TypedArray arr = context.obtainStyledAttributes(attrs, attributes);
        lineSpacingAdd = arr.getDimensionPixelSize(0, 0);
        lineSpacingMultiplier = arr.getFloat(1, 1.0f);
        originalPaddingBottom = getPaddingBottom();
        arr.recycle();

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AlignTextView);

        int alignStyle = ta.getInt(R.styleable.AlignTextView_align, 0);
        switch (alignStyle) {
            case 1:
                align = Align.ALIGN_CENTER;
                break;
            case 2:
                align = Align.ALIGN_RIGHT;
                break;
            default:
                align = Align.ALIGN_LEFT;
                break;
        }

        ta.recycle();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;


        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        this.width = widthSize - paddingLeft - paddingRight;

        int measuredWidth = widthSize;
        int measuredHeight = heightSize;
        if (measuredWidth>0) {
            String text = getText().toString();
            TextPaint paint = getPaint();
            lines.clear();
            tailLines.clear();

            // 文本含有换行符时，分割单独处理
            String[] items = text.split("\\n");
            for (String item : items) {
                calc(paint, item);
            }

            //使用替代textview计算原始高度与行数
            measureTextViewHeight(text, paint.getTextSize(), measuredWidth -
                    getPaddingLeft() - getPaddingRight());

            //获取行高
            textHeight = 1.0f * originalHeight / originalLineCount;

            textLineSpaceExtra = textHeight * (lineSpacingMultiplier - 1) + lineSpacingAdd;

            //计算实际高度,加上多出的行的高度(一般是减少)
            int heightGap = (int) ((textLineSpaceExtra + textHeight) * (lines.size() -
                    originalLineCount));

            height=originalHeight+heightGap;
//            Log.e(" ","  originalHeight="+originalHeight+"   heightGap="+heightGap);
//            Log.e(" ","  measuredWidth="+measuredWidth+"   height="+height);
//            Log.e(" ","  heightSize="+heightSize);

            setMeasuredDimension(widthSize, height);
        }
        else{

        }






    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getMeasuredWidth();
        //首先进行高度调整
        if (firstCalc&&width>0) {
            String text = getText().toString();
            TextPaint paint = getPaint();
            lines.clear();
            tailLines.clear();

            // 文本含有换行符时，分割单独处理
            String[] items = text.split("\\n");
            for (String item : items) {
                calc(paint, item);
            }

            //使用替代textview计算原始高度与行数
            measureTextViewHeight(text, paint.getTextSize(), getMeasuredWidth() -
                    getPaddingLeft() - getPaddingRight());

            //获取行高
            textHeight = 1.0f * originalHeight / originalLineCount;

            textLineSpaceExtra = textHeight * (lineSpacingMultiplier - 1) + lineSpacingAdd;

            //计算实际高度,加上多出的行的高度(一般是减少)
            int heightGap = (int) ((textLineSpaceExtra + textHeight) * (lines.size() -
                    originalLineCount));

//            setPaddingFromMe = true;
            //调整textview的paddingBottom来缩小底部空白
//            setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(),
//                    originalPaddingBottom + heightGap);

            firstCalc = false;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        TextPaint paint = getPaint();
        paint.setColor(getCurrentTextColor());
        paint.drawableState = getDrawableState();

        width = getMeasuredWidth();

        Paint.FontMetrics fm = paint.getFontMetrics();
        float firstHeight = getTextSize() - (fm.bottom - fm.descent + fm.ascent - fm.top);

        int gravity = getGravity();
        if ((gravity & 0x1000) == 0) { // 是否垂直居中
            firstHeight = firstHeight + (textHeight - firstHeight) / 2;
        }

        int paddingTop = getPaddingTop();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        width = width - paddingLeft - paddingRight;

        for (int i = 0; i < lines.size(); i++) {
            float drawY = i * textHeight + firstHeight;
            String line = lines.get(i);
            // 绘画起始x坐标
            float drawSpacingX = paddingLeft;
            float gap = (width - paint.measureText(line));
            float interval = gap / (line.length() - 1);

            // 绘制最后一行
            if (tailLines.contains(i)) {
                interval = 0;
                if (align == Align.ALIGN_CENTER) {
                    drawSpacingX += gap / 2;
                } else if (align == Align.ALIGN_RIGHT) {
                    drawSpacingX += gap;
                }
            }

            for (int j = 0; j < line.length(); j++) {
                float drawX = paint.measureText(line.substring(0, j)) + interval * j;
                canvas.drawText(line.substring(j, j + 1), drawX + drawSpacingX, drawY +
                        paddingTop + textLineSpaceExtra * i, paint);
            }
        }
    }

    /**
     * 设置尾行对齐方式
     *
     * @param align 对齐方式
     */
    public void setAlign(Align align) {
        this.align = align;
        invalidate();
    }

    /**
     * 计算每行应显示的文本数
     *
     * @param text 要计算的文本
     */
    private void calc(Paint paint, String text) {
//        Log.e("width="+width);
        if(width==0){
            return;
        }
        if (text.length() == 0) {
            lines.add("\n");
            return;
        }
        int startPosition = 0; // 起始位置
        float oneChineseWidth = paint.measureText("国");
        int ignoreCalcLength = (int) (width / oneChineseWidth); // 忽略计算的长度
        StringBuilder sb = new StringBuilder(text.substring(0, Math.min(ignoreCalcLength + 1,
                text.length())));

        for (int i = ignoreCalcLength + 1; i < text.length(); i++) {
            String substring = text.substring(startPosition, i + 1);
//            Log.e("substring="+substring);
            if (paint.measureText(substring) > width) {
                startPosition = i;
                //将之前的字符串加入列表中
                lines.add(sb.toString());

                sb = new StringBuilder();

                //添加开始忽略的字符串，长度不足的话直接结束,否则继续
                if ((text.length() - startPosition) > ignoreCalcLength) {
                    sb.append(text.substring(startPosition, startPosition + ignoreCalcLength));
                } else {
                    lines.add(text.substring(startPosition));
                    break;
                }

                i = i + ignoreCalcLength - 1;
            } else {
                sb.append(text.charAt(i));
            }
        }
        if (sb.length() > 0) {
            lines.add(sb.toString());
        }
        tailLines.add(lines.size() - 1);
    }


    @Override
    public void setText(CharSequence text, BufferType type) {
        firstCalc = true;
        super.setText(text, type);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        if (!setPaddingFromMe) {
            originalPaddingBottom = bottom;
        }
        setPaddingFromMe = false;
        super.setPadding(left, top, right, bottom);
    }


    /**
     * 获取文本实际所占高度，辅助用于计算行高,行数
     *
     * @param text        文本
     * @param textSize    字体大小
     * @param deviceWidth 屏幕宽度
     */
    private void measureTextViewHeight(String text, float textSize, int deviceWidth) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        int widthMeasureSpec = MeasureSpec.makeMeasureSpec(deviceWidth, MeasureSpec.EXACTLY);
        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        textView.measure(widthMeasureSpec, heightMeasureSpec);
        originalLineCount = textView.getLineCount();
        originalHeight = textView.getMeasuredHeight();
    }

}
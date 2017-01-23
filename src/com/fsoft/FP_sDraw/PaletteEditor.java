package com.fsoft.FP_sDraw;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;

/**
 * Created with IntelliJ IDEA.
 * User: Dr. Failov
 * Date: 03.03.13
 * Time: 22:31
 */
public class PaletteEditor extends ScrollView {
    Context context;
    public final int AIM_BACKGROUD_COLOR=1;
    public final int AIM_BRUSH_COLOR=2;
    int aim;
    int new_color;

    TextView header_text;
    SeekBar slider_hue;
    TextView slider_hue_text;
    SeekBar slider_saturation;
    TextView slider_saturation_text;
    SeekBar slider_value;
    TextView slider_value_text;
    Button brush_preview;

    public PaletteEditor(){
         super(Settings.context);
         context=Settings.context;
         LinearLayout linear=new LinearLayout(context);
         linear.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
         this.addView(linear);
         linear.setOrientation(LinearLayout.VERTICAL);



         //--------------------------------------------------------------------------------------------------------------brush color
         Settings.___________________LOG("Настройка блока кисти...", false);
         //TextView "Brush"
         header_text=new TextView(context);
         header_text.setGravity(Gravity.CENTER);
         header_text.setTextSize(25);
         header_text.setTypeface(Typeface.DEFAULT_BOLD);
         linear.addView(header_text);

         //Text View "Hue"
         TextView text_red=new TextView(context);
         text_red.setText("Оттенок");
         linear.addView(text_red);
         //SeenBar "Hue"
         slider_hue =new SeekBar(context);             //Hue [0 .. 360)
         slider_hue.setId(1);                                                                                            //slider_hue 1 brush
         slider_hue.setMax(359);
         //slider_hue.setProgress(Color.red(new_color));
         slider_hue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
             @Override
             public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                 if (b) { seekBarUpdated(); }
                 slider_hue_text.setText(String.valueOf(i));
             }

             @Override
             public void onStartTrackingTouch(SeekBar seekBar) {
             }

             @Override
             public void onStopTrackingTouch(SeekBar seekBar) {
             }
         });
         slider_hue.setLayoutParams(Settings.layout_params_slider);
         linear.addView(slider_hue);
         //Text View
         slider_hue_text =new TextView(context);
         slider_hue_text.setText(String.valueOf(Color.red(new_color)));
         slider_hue_text.setGravity(Gravity.RIGHT);
         linear.addView(slider_hue_text);

         //Text View "saturation"
         TextView text_green=new TextView(context);
         text_green.setText("Насыщеность");
         linear.addView(text_green);
         //SeenBar "saturation"
         slider_saturation =new SeekBar(context);           //Saturation [0...1]
         slider_saturation.setId(2);                                                                                          //slider_saturation 2 brush
         slider_saturation.setMax(100);
         slider_saturation.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
             @Override
             public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                 if (b) { seekBarUpdated(); }
                 slider_saturation_text.setText(String.valueOf(i));
             }

             @Override
             public void onStartTrackingTouch(SeekBar seekBar) {
             }

             @Override
             public void onStopTrackingTouch(SeekBar seekBar) {
             }
         });
         slider_saturation.setLayoutParams(Settings.layout_params_slider);
         linear.addView(slider_saturation);
        //Text View
        slider_saturation_text =new TextView(context);
        slider_saturation_text.setText(String.valueOf(Color.green(new_color)));
        slider_saturation_text.setGravity(Gravity.RIGHT);
        linear.addView(slider_saturation_text);

         //Text View "value"
         TextView text_blue=new TextView(context);
         text_blue.setText("Яркость");
         linear.addView(text_blue);
         //SeenBar "value"
         slider_value =new SeekBar(context);             //Value [0...1]
         slider_value.setId(3);                                                                                           //slider_value 3 brush
         slider_value.setMax(100);
         slider_value.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
             @Override
             public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                 if (b) { seekBarUpdated(); }
                 slider_value_text.setText(String.valueOf(i));
             }

             @Override
             public void onStartTrackingTouch(SeekBar seekBar) {
             }

             @Override
             public void onStopTrackingTouch(SeekBar seekBar) {
             }
         });
         slider_value.setLayoutParams(Settings.layout_params_slider);
         linear.addView(slider_value);
         //Text View
         slider_value_text =new TextView(context);
         slider_value_text.setText(String.valueOf(Color.blue(new_color)));
         slider_value_text.setGravity(Gravity.RIGHT);
         linear.addView(slider_value_text);

         //Button BrushColor Preview
         brush_preview=new Button(context);
         brush_preview.setText("ОК");
         brush_preview.setOnClickListener(new OnClickListener() {
             @Override public void onClick(View view) {
                 apply();
             }
         });
         LinearLayout.LayoutParams layout_params_common = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, (int)Settings.preview_size.y);
         layout_params_common.setMargins(5, 20, 5, 5);
         brush_preview.setLayoutParams(layout_params_common);
         linear.addView(brush_preview);
     }
    public void update(int new_aim){
        aim=new_aim;
        if(aim == AIM_BRUSH_COLOR) header_text.setText("Цвет кисти");
        else if(aim == AIM_BACKGROUD_COLOR) header_text.setText("Цвет фона");

        if(aim==AIM_BRUSH_COLOR) new_color=Settings.brush_color;
        else if(aim==AIM_BACKGROUD_COLOR) new_color=Settings.background_color;

        float newHSV[] = new float[]{0,0,0};
        Color.colorToHSV(new_color, newHSV);
        slider_hue.setProgress((int)(newHSV[0]));
        slider_saturation.setProgress((int)(newHSV[1]*100));
        slider_value.setProgress((int)(newHSV[2]*100));
        brush_preview.setBackgroundColor(new_color);
        if(Color.red(new_color)>100 || Color.green(new_color)>100 || Color.blue(new_color)>100)
             brush_preview.setTextColor(Color.BLACK);
        else
             brush_preview.setTextColor(Color.WHITE);
    }
    public void seekBarUpdated()  {
        new_color = Color.HSVToColor(new float[]{slider_hue.getProgress(),(float)slider_saturation.getProgress()/(float)100, (float)slider_value.getProgress()/(float)100});//hsv[0] is Hue [0 .. 360) hsv[1] is Saturation [0...1] hsv[2] is Value [0...1]
        brush_preview.setBackgroundColor(new_color);
        if (Color.red(new_color) > 100 || Color.green(new_color) > 100 || Color.blue(new_color) > 100)
            brush_preview.setTextColor(Color.BLACK);
        else
            brush_preview.setTextColor(Color.WHITE);

    }
    public boolean keyEvent(KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN)
        {
            if(event.getKeyCode()==KeyEvent.KEYCODE_BACK){
                return true;
            }
        }
        if(event.getAction() == KeyEvent.ACTION_UP)
        {
            if(event.getKeyCode()==KeyEvent.KEYCODE_BACK){
                apply();
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    } //обработка клавиш аппаратных
    public void apply(){
        if(aim == AIM_BRUSH_COLOR)
        {
            Settings.brush_color=new_color;
            ((sDraw)context).settings.update(((sDraw) context).settings.UPDATE_BRUSH_COLOR);
        }
        if(aim == AIM_BACKGROUD_COLOR)
        {
            Settings.background_color=new_color;
            ((sDraw)context).settings.update(((sDraw) context).settings.UPDATE_BACKGROUND_COLOR);
        }
        ((sDraw)context).set(2);
    }
}

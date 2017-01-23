package com.fsoft.FP_sDraw;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.view.*;
import android.widget.*;

/**
 * Created with IntelliJ IDEA.
 * User: Dr. Failov
 * Date: 23.01.13
 * Time: 23:26
 * To change this template use File | SettingsScreen | File Templates.
 */
public class SettingsScreen extends LinearLayout /*implements OnClickListener, SeekBar.OnSeekBarChangeListener*/ {
    public final int UPDATE_BRUSH_COLOR = 1;
    public final int UPDATE_BACKGROUND_COLOR = 2;
    public final int UPDATE_SIZES = 3;
    //public final int UPDATE_MANAGE_METHOD = 4;
    public final int UPDATE_CHECKBOXES = 5;
    public final int UPDATE_ALL = 6;
    Context context;
    TextView text_depth_brush;
    TextView text_depth_eraser;
    RadioGroup manage_method_radiogroup;
    CheckBox antialiasing_checkbox;
    CheckBox smoothing_checkbox;
    Button preview_button;
    TextView other_settings_info;

    Bitmap preview_bitmap;
    Canvas preview_canvas;
    Paint preview_paint;

    public SettingsScreen(){
        super(Settings.context);
        Settings.___________________LOG("Инициализация...", false);
        context = Settings.context;
        preview_bitmap=Bitmap.createBitmap((int)Settings.preview_size.x, (int)Settings.preview_size.y, Bitmap.Config.ARGB_4444);
        preview_canvas=new Canvas(preview_bitmap);
        preview_paint = new Paint();

        Settings.___________________LOG("Задание параметров формы...", false);
        this.setOrientation(VERTICAL);
        ScrollView scroll=new ScrollView(context);
        LayoutParams layout_params_common=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        scroll.setLayoutParams(layout_params_common);
        this.addView(scroll);
        LinearLayout linear=new LinearLayout(context);
        linear.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        scroll.addView(linear);
        linear.setOrientation(VERTICAL);
        //   //--------------------------------------------------------------------------------------------------------------   main text
        {
            //TextView others
            TextView other_text=new TextView(context);
            other_text.setText("Настройки");
            other_text.setTextSize((int)(Settings.settings_header_text_size*1.5));
            other_text.setLayoutParams(Settings.layout_params_header);
            other_text.setGravity(Gravity.CENTER);
            other_text.setTypeface(Typeface.DEFAULT_BOLD);
            linear.addView(other_text);
        }
        {
            //-----------------------------------------add delimiter
            View delimiter =new View(context);
            delimiter.setBackgroundColor(Color.DKGRAY);
            delimiter.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 1));
            linear.addView(delimiter);
        }
        //   //--------------------------------------------------------------------------------------------------------------   palette brush
        Settings.___________________LOG("Подготовка блока palette brush...", false);
        {
            //TextView palttte
            TextView palette_brush_text=new TextView(context);
            palette_brush_text.setText("Цвет кисти");
            palette_brush_text.setGravity(Gravity.CENTER);
            palette_brush_text.setTextSize(Settings.settings_header_text_size);
            palette_brush_text.setTypeface(Typeface.DEFAULT_BOLD);
            palette_brush_text.setLayoutParams(Settings.layout_params_header);
            linear.addView(palette_brush_text);

            //palette
            LinearLayout palette_brush_layout = new LinearLayout(context);
            LinearLayout palette_brush_item_layout=null;
            Button palette_brush_button;
            palette_brush_layout.setOrientation(LinearLayout.HORIZONTAL);
            Bitmap palette_brush_item_bitmap;// = Bitmap.createBitmap(Settings.palette_item_size, Settings.palette_item_size, Bitmap.Config.ARGB_4444);
            Canvas palette_brush_item_canvas;//=new Canvas(palette_brush_item_bitmap);
            Paint palette_brush_item_paint=new Paint();
            palette_brush_item_paint.setAntiAlias(true);
            for(int g=0;g<Settings.palette_brush_counter;g++)
            {
                // preparing icon
                palette_brush_item_bitmap = Bitmap.createBitmap(Settings.palette_item_size, Settings.palette_item_size, Bitmap.Config.ARGB_4444);
                palette_brush_item_canvas = new Canvas(palette_brush_item_bitmap);
                palette_brush_item_paint.setColor(Settings.palette_brush[g]);
                palette_brush_item_paint.setStyle(Paint.Style.FILL);
                palette_brush_item_canvas.drawRoundRect(new RectF(0, 0, Settings.palette_item_size, Settings.palette_item_size), Settings.palette_item_size / 5, Settings.palette_item_size / 5, palette_brush_item_paint);
                palette_brush_item_paint.setStrokeWidth(Settings.palette_item_size / 30);
                palette_brush_item_paint.setColor(Color.argb(100, 255, 255, 255));
                palette_brush_item_paint.setStyle(Paint.Style.STROKE);
                palette_brush_item_canvas.drawRoundRect(new RectF(0, 0, Settings.palette_item_size, Settings.palette_item_size), Settings.palette_item_size / 5, Settings.palette_item_size / 5, palette_brush_item_paint);

                //configure button here
                palette_brush_button=new Button(context);
                palette_brush_button.setId(g);
                palette_brush_button.setBackgroundDrawable(new BitmapDrawable(palette_brush_item_bitmap));
                layout_params_common = new LayoutParams (Settings.palette_item_size, Settings.palette_item_size);
                layout_params_common.setMargins(5, 5, 5, 5);
                palette_brush_button.setLayoutParams(layout_params_common);
                palette_brush_button.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Settings.brush_color = Settings.palette_brush[view.getId()];
                        update(UPDATE_BRUSH_COLOR);
                    }
                });

                //add button to list here
                if(palette_brush_item_layout == null)
                {
                    palette_brush_item_layout = new LinearLayout(context);
                    palette_brush_item_layout.setOrientation(LinearLayout.VERTICAL);
                    palette_brush_item_layout.addView(palette_brush_button);
                }
                else
                {
                    palette_brush_item_layout.addView(palette_brush_button);
                    if(palette_brush_item_layout.getChildCount() >= Settings.palette_height){
                        palette_brush_layout.addView(palette_brush_item_layout);
                        palette_brush_item_layout=null;
                    }
                }
            }

            //button edit
            Button palette_brush_edit_button=new Button(context);
            palette_brush_edit_button.setText("Другой");
            layout_params_common = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.FILL_PARENT);
            layout_params_common.setMargins(0, 5, 0, 5);
            palette_brush_edit_button.setLayoutParams(layout_params_common);
            palette_brush_edit_button.setOnClickListener(new OnClickListener() {
                @Override public void onClick(View view) {
                    ((sDraw)context).set(sDraw.SET_PALETTE_BRUSH);//palette brush editor
                } });
            palette_brush_layout.addView(palette_brush_edit_button);

            HorizontalScrollView palette_brush_scroll = new HorizontalScrollView(context);
            palette_brush_scroll.addView(palette_brush_layout);
            linear.addView(palette_brush_scroll);
        }
        {
        //-----------------------------------------add delimiter
        View delimiter =new View(context);
        delimiter.setBackgroundColor(Color.DKGRAY);
        delimiter.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 1));
        linear.addView(delimiter);
        }

        //   //--------------------------------------------------------------------------------------------------------------   palette background
        Settings.___________________LOG("Подготовка блока palette background...", false);
        {
            //TextView palttte
            TextView palette_background_text=new TextView(context);
            palette_background_text.setText("Цвет фона");
            palette_background_text.setGravity(Gravity.CENTER);
            palette_background_text.setTextSize(Settings.settings_header_text_size);
            palette_background_text.setTypeface(Typeface.DEFAULT_BOLD);
            palette_background_text.setLayoutParams(Settings.layout_params_header);
            linear.addView(palette_background_text);

            //palette
            LinearLayout palette_background_layout = new LinearLayout(context);
            LinearLayout palette_background_item_layout=null;
            Button palette_background_button;
            palette_background_layout.setOrientation(LinearLayout.HORIZONTAL);
            Bitmap palette_background_item_bitmap;// = Bitmap.createBitmap(Settings.palette_item_size, Settings.palette_item_size, Bitmap.Config.ARGB_4444);
            Canvas palette_background_item_canvas;//=new Canvas(palette_brush_item_bitmap);
            Paint palette_background_item_paint=new Paint();
            palette_background_item_paint.setAntiAlias(true);
            for(int g=0;g<Settings.palette_background_counter;g++)
            {
                // preparing icon
                palette_background_item_bitmap = Bitmap.createBitmap(Settings.palette_item_size, Settings.palette_item_size, Bitmap.Config.ARGB_4444);
                palette_background_item_canvas = new Canvas(palette_background_item_bitmap);
                palette_background_item_paint.setColor(Settings.palette_background[g]);
                palette_background_item_paint.setStyle(Paint.Style.FILL);
                palette_background_item_canvas.drawRoundRect(new RectF(0, 0, Settings.palette_item_size, Settings.palette_item_size), Settings.palette_item_size / 5, Settings.palette_item_size / 5, palette_background_item_paint);
                palette_background_item_paint.setStrokeWidth(Settings.palette_item_size / 30);
                palette_background_item_paint.setColor(Color.argb(100, 255, 255, 255));
                palette_background_item_paint.setStyle(Paint.Style.STROKE);
                palette_background_item_canvas.drawRoundRect(new RectF(0, 0, Settings.palette_item_size, Settings.palette_item_size), Settings.palette_item_size / 5, Settings.palette_item_size / 5, palette_background_item_paint);


                //configure button here
                palette_background_button=new Button(context);
                palette_background_button.setId(g);
                palette_background_button.setBackgroundDrawable(new BitmapDrawable(palette_background_item_bitmap));
                layout_params_common = new LayoutParams (Settings.palette_item_size, Settings.palette_item_size);
                layout_params_common.setMargins(5, 5, 5, 5);
                palette_background_button.setLayoutParams(layout_params_common);
                palette_background_button.setOnClickListener(new OnClickListener() {
                    @Override public void onClick(View view) {
                        Settings.background_color=Settings.palette_background[view.getId()];
                        update(UPDATE_BACKGROUND_COLOR);
                    }});
                //add button to list here
                if(palette_background_item_layout == null){
                    palette_background_item_layout = new LinearLayout(context);
                    palette_background_item_layout.setOrientation(LinearLayout.VERTICAL);
                    palette_background_item_layout.addView(palette_background_button);
                }else{
                    palette_background_item_layout.addView(palette_background_button);
                    if(palette_background_item_layout.getChildCount() >= Settings.palette_height){
                        palette_background_layout.addView(palette_background_item_layout);
                        palette_background_item_layout=null;
                    }
                }
            }
            //button edit
            Button palette_background_edit_button=new Button(context);
            palette_background_edit_button.setText("Другой");
                layout_params_common = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.FILL_PARENT);
                layout_params_common.setMargins(0, 5, 0, 5);
            palette_background_edit_button.setLayoutParams(layout_params_common);
            palette_background_edit_button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((sDraw) context).set(sDraw.SET_PALETTE_BACKGROUND);//palette background editor
                }
            });
                palette_background_layout.addView(palette_background_edit_button);

            HorizontalScrollView palette_background_scroll = new HorizontalScrollView(context);
                palette_background_scroll.addView(palette_background_layout);
            linear.addView(palette_background_scroll);
        }
        {
            //-----------------------------------------add delimiter
            View delimiter =new View(context);
            delimiter.setBackgroundColor(Color.DKGRAY);
            delimiter.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 1));
            linear.addView(delimiter);
        }

        //--------------------------------------------------------------------------------------------------------------preview
        {
            preview_button=new Button(context);
            layout_params_common=new LayoutParams((int)Settings.preview_size.x, (int)Settings.preview_size.y);
            layout_params_common.setMargins(0, 10, 0, 10);
            preview_button.setLayoutParams(layout_params_common);
            linear.addView(preview_button);
        }
        {
            //-----------------------------------------add delimiter
            View delimiter =new View(context);
            delimiter.setBackgroundColor(Color.DKGRAY);
            delimiter.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 1));
            linear.addView(delimiter);
        }

        //--------------------------------------------------------------------------------------------------------------brush depth
        Settings.___________________LOG("Настройка блока размера кисти...", false);
        {
            //TextView depth brush
            TextView text_depth_brush1=new TextView(context);
            text_depth_brush1.setText("Толщина кисти");
            text_depth_brush1.setGravity(Gravity.CENTER);
            text_depth_brush1.setTypeface(Typeface.DEFAULT_BOLD);
            text_depth_brush1.setTextSize(Settings.settings_header_text_size);
            text_depth_brush1.setLayoutParams(Settings.layout_params_header);
            linear.addView(text_depth_brush1);
        //SeenBar
            SeekBar slider_depth_brush=new SeekBar(context);
            slider_depth_brush.setId(7);//slider_depth brush 7(?)
            slider_depth_brush.setMax(50);
            slider_depth_brush.setProgress((int)Settings.brush_size);
            slider_depth_brush.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override public void onProgressChanged(SeekBar seekBar, int i, boolean b) {if(b){
                    Settings.brush_size=i;
                    text_depth_brush.setText(String.valueOf((int)Settings.brush_size));
                    update(UPDATE_SIZES);
                }}
                @Override public void onStartTrackingTouch(SeekBar seekBar) { }
                @Override public void onStopTrackingTouch(SeekBar seekBar) { }
            });
            slider_depth_brush.setLayoutParams(Settings.layout_params_slider);
            linear.addView(slider_depth_brush);
            //Text View
            text_depth_brush=new TextView(context);
            text_depth_brush.setText(String.valueOf((int)Settings.brush_size));
            text_depth_brush.setGravity(Gravity.RIGHT);
            linear.addView(text_depth_brush);
        }
        {
            //-----------------------------------------add delimiter
            View delimiter =new View(context);
            delimiter.setBackgroundColor(Color.DKGRAY);
            delimiter.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 1));
            linear.addView(delimiter);
        }
        //--------------------------------------------------------------------------------------------------------------eraser depth
        Settings.___________________LOG("Настройка блока размера ластика...", false);
        {
            //TextView depth brush
            TextView text_depth_eraser1=new TextView(context);
            text_depth_eraser1.setText("Толщина ластика");
            text_depth_eraser1.setGravity(Gravity.CENTER);
            text_depth_eraser1.setTypeface(Typeface.DEFAULT_BOLD);
            text_depth_eraser1.setTextSize(Settings.settings_header_text_size);
            text_depth_eraser1.setLayoutParams(Settings.layout_params_header);
            linear.addView(text_depth_eraser1);
            //SeenBar
            SeekBar slider_depth_brush_eraser=new SeekBar(context);
            slider_depth_brush_eraser.setId(8);//slider_depth eraser 8
            slider_depth_brush_eraser.setMax(50);
            slider_depth_brush_eraser.setProgress((int)Settings.eraser_size);
            slider_depth_brush_eraser.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override public void onProgressChanged(SeekBar seekBar, int i, boolean b) {if(b){
                    Settings.eraser_size=i;
                    text_depth_eraser.setText(String.valueOf((int)Settings.eraser_size));
                }}
                @Override public void onStartTrackingTouch(SeekBar seekBar) { }
                @Override public void onStopTrackingTouch(SeekBar seekBar) { }
            });
            //slider_depth_brush_eraser.setPadding(5, 5, 5, 5);
            slider_depth_brush_eraser.setLayoutParams(Settings.layout_params_slider);
            linear.addView(slider_depth_brush_eraser);
            //Text View
            text_depth_eraser=new TextView(context);
            text_depth_eraser.setText(String.valueOf((int)Settings.eraser_size));
            text_depth_eraser.setGravity(Gravity.RIGHT);
            linear.addView(text_depth_eraser);
        }
        {
            //-----------------------------------------add delimiter
            View delimiter =new View(context);
            delimiter.setBackgroundColor(Color.DKGRAY);
            delimiter.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 1));
            linear.addView(delimiter);
        }
        //--------------------------------------------------------------------------------------------------------------manage method
        Settings.___________________LOG("Настройка блока управления толщиной кисти...", false);
        {
            //TextView depth brush
            TextView manage_method_text=new TextView(context);
            manage_method_text.setText("Управление толщиной кисти");
            manage_method_text.setLayoutParams(Settings.layout_params_header);
            manage_method_text.setGravity(Gravity.CENTER);
            manage_method_text.setTextSize(Settings.settings_header_text_size);
            manage_method_text.setTypeface(Typeface.DEFAULT_BOLD);
            linear.addView(manage_method_text);

            //RadioGroup
            manage_method_radiogroup = new RadioGroup(context);
            RadioButton manage_method_radiobutton1 = new RadioButton(context);
            manage_method_radiobutton1.setText("Сила прикосновения");
            manage_method_radiobutton1.setId(1);
            manage_method_radiogroup.addView(manage_method_radiobutton1);
            RadioButton manage_method_radiobutton2 = new RadioButton(context);
            manage_method_radiobutton2.setText("Скорость движения");
            manage_method_radiobutton2.setId(2);
            manage_method_radiogroup.addView(manage_method_radiobutton2);
            RadioButton manage_method_radiobutton3 = new RadioButton(context);
            manage_method_radiobutton3.setText("Константа");
            manage_method_radiobutton3.setId(3);
            manage_method_radiogroup.addView(manage_method_radiobutton3);
            if(Settings.manage_method == 1)//1=size, 2=speed, 3=none     //select
                manage_method_radiobutton1.toggle();
            else if(Settings.manage_method == 2)
                manage_method_radiobutton2.toggle();
            else if(Settings.manage_method == 3)
                manage_method_radiobutton3.toggle();
            linear.addView(manage_method_radiogroup);
        }
        {
            //-----------------------------------------add delimiter
            View delimiter =new View(context);
            delimiter.setBackgroundColor(Color.DKGRAY);
            delimiter.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 1));
            linear.addView(delimiter);
        }
//   //--------------------------------------------------------------------------------------------------------------   image processing
        Settings.___________________LOG("Настройка блока обработки отрисовки...", false);
        {
            //TextView processing
            TextView processing_test=new TextView(context);
            processing_test.setText("Обработка отрисовки");
            processing_test.setTextSize(Settings.settings_header_text_size);
            processing_test.setLayoutParams(Settings.layout_params_header);
            processing_test.setGravity(Gravity.CENTER);
            processing_test.setTypeface(Typeface.DEFAULT_BOLD);
            linear.addView(processing_test);

            //checkbox antialiasing
            antialiasing_checkbox = new CheckBox(context);
            antialiasing_checkbox.setText("Сглаживание контуров");
            antialiasing_checkbox.setChecked(Settings.antialiasing);
            antialiasing_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    Settings.antialiasing=b;
                    update(UPDATE_CHECKBOXES);
                } });
            linear.addView(antialiasing_checkbox);

            //checkbox smoothing
            smoothing_checkbox = new CheckBox(context);
            smoothing_checkbox.setText("Сглаживание рывков");
            smoothing_checkbox.setChecked(Settings.smoothing);
            linear.addView(smoothing_checkbox);
        }
        {
            //-----------------------------------------add delimiter
            View delimiter =new View(context);
            delimiter.setBackgroundColor(Color.DKGRAY);
            delimiter.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 1));
            linear.addView(delimiter);
        }
        //--------------------------------------------------------------------------------------------------------------   other settings
        {
            OnClickListener ToOthers=new OnClickListener() {
                @Override public void onClick(View view) {
                    ((sDraw)context).set(sDraw.SET_OTHERSETTINGS);
                }
            };
            //TextView others
            TextView other_text=new TextView(context);
            other_text.setText("Другие настройки");
            other_text.setTextSize((int)(Settings.settings_header_text_size*1.5));
            other_text.setLayoutParams(Settings.layout_params_header);
            other_text.setGravity(Gravity.LEFT);
            other_text.setTypeface(Typeface.DEFAULT);
            other_text.setOnClickListener(ToOthers);
            linear.addView(other_text);
            //text info
            other_settings_info=new TextView(context);
            other_settings_info.setText(
                    "Папка сохранения: " + Settings.save_path +
                    "\nПрефикс файла: " + Settings.save_fileprefix +
                    "\nАвтосохранение: " + Settings.autosave +
                    "\nПапка автосохранения: " + Settings.autosave_path +
                    "\nЛимит автосохранения: " + Settings.autosave_limit +
                    "\nРежим отладки: " + Settings.debug +
                    "\nСбросить настройки");
            other_settings_info.setTextSize((int) (Settings.settings_header_text_size * 0.7));
            other_settings_info.setGravity(Gravity.LEFT);
            other_settings_info.setTypeface(Typeface.DEFAULT);
            other_settings_info.setTextColor(Color.GRAY);
            other_settings_info.setOnClickListener(ToOthers);
            linear.addView(other_settings_info);
        }
        {
            //-----------------------------------------add delimiter
            View delimiter =new View(context);
            delimiter.setBackgroundColor(Color.DKGRAY);
            delimiter.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 1));
            linear.addView(delimiter);
        }


//   //--------------------------------------------------------------------------------------------------------------   APPLY button
        Settings.___________________LOG("Настройка кнопочки ПРИМЕНИТЬ...", false);
        //Button Apply
        Button button_save=new Button(context);
        button_save.setId(1);
        button_save.setText("Применить");
        button_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                apply();
            }
        });
        layout_params_common=new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_params_common.setMargins(20, 40, 20, 10);
        button_save.setLayoutParams(layout_params_common);
        button_save.setPadding(0, 30, 0, 30);
        linear.addView(button_save);

        //update(UPDATE_ALL);
    }
    public void apply(){
        Settings.___________________LOG("Обработка данных перед записью...", false);
        Settings.manage_method = manage_method_radiogroup.getCheckedRadioButtonId();
        Settings.antialiasing = antialiasing_checkbox.isChecked();
        Settings.smoothing = smoothing_checkbox.isChecked();
        Settings.___________________LOG("Запись данных в память...", false);
        Settings.save_settings();
        ((sDraw)context).draw.paint.setAntiAlias(Settings.antialiasing);
        Settings.___________________LOG("Переход на холст...", false);
        if(((sDraw)context).draw.isempty())
            ((sDraw)context).draw.clear();
        ((sDraw)context).set(sDraw.SET_DRAW);
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
    public void update(int what){
        Settings.___________________LOG("update "+String.valueOf(what), false);
        if(what == UPDATE_BRUSH_COLOR || what==UPDATE_ALL)
        {
        }
        if(what == UPDATE_BACKGROUND_COLOR || what==UPDATE_ALL)
        {
        }
        if(what==UPDATE_SIZES || what==UPDATE_ALL)
        {
            text_depth_brush.setText(String.valueOf((int)Settings.brush_size));
        }

        //update preview
        //подготовка, фон
        preview_paint.setAntiAlias(true);
        preview_paint.setColor(Settings.background_color);
        preview_paint.setStyle(Paint.Style.FILL);
        preview_canvas.drawRoundRect(new RectF(0, 0, Settings.preview_size.x, Settings.preview_size.y), Settings.preview_size.y / 10, Settings.preview_size.y / 10, preview_paint);
        preview_paint.setStrokeWidth(1);
        preview_paint.setColor(Color.argb(100, 255, 255, 255));
        preview_paint.setStyle(Paint.Style.STROKE);
        preview_canvas.drawRoundRect(new RectF(0, 0, Settings.preview_size.x, Settings.preview_size.y), Settings.preview_size.y / 10, Settings.preview_size.y / 10, preview_paint);
        //рамка фона
        preview_paint.setAntiAlias(Settings.antialiasing);
        preview_paint.setStyle(Paint.Style.STROKE);
        preview_paint.setColor(Settings.brush_color);
        preview_paint.setStrokeWidth((float)Settings.brush_size);
        int width=preview_bitmap.getWidth();
        int height=preview_bitmap.getHeight();
        //линии
        preview_canvas.drawLine(((float)1/(float)8)*width, ((float)3/(float)4)*height, ((float)3/(float)8)*width, ((float)1/(float)4)*height, preview_paint);
        preview_canvas.drawLine(((float)3/(float)8)*width, ((float)1/(float)4)*height, ((float)5/(float)8)*width, ((float)3/(float)4)*height, preview_paint);
        preview_canvas.drawLine(((float)5/(float)8)*width, ((float)3/(float)4)*height, ((float)7/(float)8)*width, ((float)1/(float)4)*height, preview_paint);
        //точки
        preview_paint.setStyle(Paint.Style.FILL);
        preview_canvas.drawCircle(((float)1/(float)8)*width, ((float)3/(float)4)*height, (float)Settings.brush_size / (float)2, preview_paint);
        preview_canvas.drawCircle(((float)3/(float)8)*width, ((float)1/(float)4)*height, (float)Settings.brush_size / (float)2, preview_paint);
        preview_canvas.drawCircle(((float)5/(float)8)*width, ((float)3/(float)4)*height, (float)Settings.brush_size / (float)2, preview_paint);
        preview_canvas.drawCircle(((float)7/(float)8)*width, ((float)1/(float)4)*height, (float)Settings.brush_size / (float)2, preview_paint);
        //отобразить
        preview_button.setBackgroundDrawable(new BitmapDrawable(preview_bitmap));
        //обновить инфо о других настройках
        other_settings_info.setText(
                "Папка сохранения: " + Settings.save_path +
                        "\nПрефикс файла: " + Settings.save_fileprefix +
                        "\nАвтосохранение: " + Settings.autosave +
                        "\nПапка автосохранения: " + Settings.autosave_path +
                        "\nЛимит автосохранения: " + Settings.autosave_limit +
                        "\nРежим отладки: " + Settings.debug +
                        "\nШагов отмены: " + Settings.undo_size +
                        "\nСбросить настройки");
    }
}

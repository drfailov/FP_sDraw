package com.fsoft.FP_sDraw;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.*;

import java.io.File;
import java.io.FilenameFilter;

public class sDraw extends Activity implements View.OnClickListener
{
    //константы
    static public final int SET_DRAW = 1;
    static public final int SET_SETTINGS = 2;
    static public final int SET_ABOUT = 3;
    static public final int SET_PALETTE_BRUSH = 4;
    static public final int SET_PALETTE_BACKGROUND = 5;
    static public final int SET_OPEN = 6;
    static public final int SET_OTHERSETTINGS = 7;
    //базовые модули
    Draw draw;
    SettingsScreen settings;
    OtherSettings other_settings;
    PaletteEditor palette_editor;
    FileSelector fileSelector;
    //Для меню
    int menu_items;
    ScrollView menu_scroll=null;
    //для системы логгирования
    String TAG="myTag";
    //для переключателя виджетов
    int current_view=1;
    @Override public void onCreate(Bundle savedInstanceState){
        //вызвать родительский класс
        super.onCreate(savedInstanceState);  //определить активность
        long time_start=System.currentTimeMillis();
        Log.d(TAG, "Старт программы");
        //сделать так чтобы было красивенько
        requestWindowFeature(Window.FEATURE_NO_TITLE);  //убрать панель уведомлений
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);   //убрать панель названия
        //достать размер дисплея
        DisplayMetrics dm = new DisplayMetrics();                             	//получить размер дисплея
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        point display_size=new point(dm.widthPixels, dm.heightPixels);
        //инициализировать
        Settings.init(this);
        Settings.display_size.set(display_size);
        Settings.___________________LOG("Получен размер дисплея: "+display_size.toString(), false);
        Settings.___________________LOG("Settings инициализирован", false);
        draw = new Draw(display_size);      //создать класс графики
        Settings.___________________LOG("Draw инициализирован", false);
        //setContentView(R.layout.color_selector);  //for experiments
        set(SET_DRAW);
        Settings.___________________LOG("Холст отрисован", false);
        //Чистка автосохранений
        if(Settings.autosave_limit!=0)
        {
            Settings.___________________LOG("Чистка автосохранений...", false);
            //AutoClearCache
            File dir = new File(Settings.autosave_path);
            String[] files;
            if(dir.isDirectory()){
                files=new File(Settings.autosave_path).list(new FilenameFilter() { @Override public boolean accept(File file, String s) {
                    return s.endsWith(".jpg");
                } });
            }
            else  {
                Settings.___________________LOG("Где-то произошла ошибка", true);
                files=null;
            }
            if(files != null && files.length>Settings.autosave_limit) {
                int howDelete=files.length-Settings.autosave_limit;
                Settings.___________________LOG("Очистить " + String.valueOf(howDelete) + " файлов.", false);
                java.util.Arrays.sort(files, 0, files.length);
                for(int i=0; i<howDelete;i++)
                    if(!new File(Settings.autosave_path+File.separator+files[i]).delete()) Settings.___________________LOG("Удалить не получилось:"+files[i], false);
            }
        }
        //Измерение времени запуска
        long time_finish=System.currentTimeMillis();
        long difference=time_finish-time_start;
        Settings.___________________LOG("Программа готова к работе. Время запуска: " + String.valueOf(difference) + " миллисекунд.", false);
    }
    @Override  public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_DOWN) //глобальная обработка кнопки "меню"
        {
        //захерачить меню
            menu_items=8;
            Object[][] menu=new Object[menu_items][3];//item, field (String item_text,    int id_image,     View.OnClickListener listener)
            int n=0;
            menu[n][0]="Сохранить файл";
            //menu[n][1]=android.R.drawable.ic_menu_save;
            menu[n][1]=R.drawable.menu_save;
            menu[n][2]=new View.OnClickListener() { @Override public void onClick(View view) {
                draw.save_file(true);
                ((AlertDialog)Settings.postbox).cancel(); } };

            n++;
            menu[n][0]="Открыть файл";
            //menu[n][1]=android.R.drawable.ic_menu_upload;
            menu[n][1]=R.drawable.menu_open;
            menu[n][2]=new View.OnClickListener() { @Override public void onClick(View view) {
                set(SET_OPEN);
                ((AlertDialog)Settings.postbox).cancel(); } };

            n++;
            menu[n][0]=draw.eraser?"Выключить ластик":"Включить ластик";
            //menu[n][1]=android.R.drawable.ic_menu_edit;
            menu[n][1]=R.drawable.menu_eraser;
            menu[n][2]=new View.OnClickListener() { @Override public void onClick(View view) {
                draw.setEraser(!draw.eraser);
                ((AlertDialog)Settings.postbox).cancel(); } };

            n++;
            menu[n][0]="Очистить холст";
            //menu[n][1]=android.R.drawable.ic_menu_delete;
            menu[n][1]=R.drawable.menu_clear;
            menu[n][2]=new View.OnClickListener() { @Override public void onClick(View view) {
                draw.clear();
                ((AlertDialog)Settings.postbox).cancel(); } };
            //-----------------------------------------------------------------------------palette here

            n++;
            menu[n][0]="Шаг назад";
            //menu[n][1]=android.R.drawable.ic_menu_revert;
            menu[n][1]=R.drawable.menu_undo;
            menu[n][2]=new View.OnClickListener() {  @Override public void onClick(View view) {
                UndoProvider.undo();
                ((AlertDialog) Settings.postbox).cancel(); } };

            n++;
            menu[n][0]="Шаг вперед";
            //menu[n][1]=android.R.drawable.ic_menu_rotate;
            menu[n][1]=R.drawable.menu_redo;
            menu[n][2]=new View.OnClickListener() {  @Override public void onClick(View view) {
                UndoProvider.redo();
                ((AlertDialog) Settings.postbox).cancel(); } };

            n++;
            menu[n][0]="Настройки";
            //menu[n][1]=android.R.drawable.ic_menu_preferences;
            menu[n][1]=R.drawable.menu_settings;
            menu[n][2]=new View.OnClickListener() { @Override public void onClick(View view) {
                set(SET_SETTINGS);
                ((AlertDialog)Settings.postbox).cancel(); } };

            n++;
            menu[n][0]="О программе";
            //menu[n][1]=android.R.drawable.ic_menu_info_details;
            menu[n][1]=R.drawable.menu_about;
            menu[n][2]=new View.OnClickListener() { @Override public void onClick(View view) {
                set(SET_ABOUT);
                ((AlertDialog)Settings.postbox).cancel(); } };
        //разметка
            Settings.___________________LOG("Ваше меню готовится...", false);
            menu_scroll =new ScrollView(this);
            LinearLayout linear=new LinearLayout(this);
            linear.setOrientation(LinearLayout.VERTICAL);
            menu_scroll.addView(linear);
        //Палитра  //   //--------------------------------------------------------------------------------------------------------------   palette brush
            Settings.___________________LOG("Подготовка блока palette brush...", false);
            HorizontalScrollView palette_brush_scroll;
            TextView palette_brush_text;
            {
                LinearLayout.LayoutParams layout_params_common;
//                //TextView palttte
//                palette_brush_text=new TextView(this);
//                palette_brush_text.setText("Цвет кисти");
//                palette_brush_text.setGravity(Gravity.CENTER);
//                palette_brush_text.setTextSize(Settings.settings_header_text_size);
//                palette_brush_text.setTypeface(Typeface.DEFAULT_BOLD);
//                palette_brush_text.setLayoutParams(Settings.layout_params_header);

                //palette
                LinearLayout palette_brush_layout = new LinearLayout(this);
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
                    palette_brush_button=new Button(this);
                    palette_brush_button.setId(g);
                    palette_brush_button.setBackgroundDrawable(new BitmapDrawable(palette_brush_item_bitmap));
                    layout_params_common = new LinearLayout.LayoutParams(Settings.palette_item_size, Settings.palette_item_size);
                    layout_params_common.setMargins(5, 5, 5, 5);
                    palette_brush_button.setLayoutParams(layout_params_common);
                    palette_brush_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Settings.brush_color = Settings.palette_brush[view.getId()];
                            Settings.save_settings();
                            draw.setEraser(false);
                            ((AlertDialog)Settings.postbox).cancel();
                            //update(UPDATE_BRUSH_COLOR);
                        }
                    });

                    //add button to list here
                    if(palette_brush_item_layout == null)
                    {
                        palette_brush_item_layout = new LinearLayout(this);
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
                palette_brush_scroll = new HorizontalScrollView(this);
                palette_brush_scroll.addView(palette_brush_layout);
            }
        //Наполнение
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.weight=1;
            LinearLayout row=new LinearLayout(this);
            linear.addView(row);
            row.setOrientation(LinearLayout.HORIZONTAL);
            int cur_col=0;
            for(int i=0; i<menu_items; i++)
            {

                if(i==2)
                {
                    //add delimiter
                    View delimiter =new View(this);
                    delimiter.setBackgroundColor(Color.DKGRAY);
                    delimiter.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 1));
                    linear.addView(delimiter);
                    //add patette
                    //linear.addView(palette_brush_text);
                    linear.addView(palette_brush_scroll);
                    Settings.___________________LOG("Мы добавили палитру в Ваше меню.", false);
                }
                //make item layout
                LinearLayout item=new LinearLayout(this) ;
                item.setOrientation(LinearLayout.VERTICAL);
                item.setLayoutParams(layoutParams);
                //fill item
                ImageView image=new ImageView(this);
                image.setImageResource((Integer) menu[i][1]);
                LinearLayout.LayoutParams tlp=new LinearLayout.LayoutParams((int)(Settings.DPI*0.27), (int)(Settings.DPI*0.27));
                tlp.gravity=Gravity.CENTER;
                image.setLayoutParams(tlp);
                item.addView(image);
                TextView text=new TextView(this);
                text.setGravity(Gravity.CENTER);
                //text.setTypeface(Typeface.DEFAULT_BOLD);
                text.setTextColor(Color.WHITE);
                text.setText((String) menu[i][0]);
                text.setTextSize(Settings.settings_header_text_size);
                item.addView(text);
                item.setOnClickListener((View.OnClickListener)menu[i][2]);
                Settings.___________________LOG("Мы добавили "+menu[i][0]+" в Ваше меню.", false);
                //add it to grid
                row.addView(item);
                cur_col++;
                if(cur_col >= 2)
                {
                    //add delimiter
                    View delimiter =new View(this);
                    delimiter.setBackgroundColor(Color.DKGRAY);
                    delimiter.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 1));
                    linear.addView(delimiter);
                    //create new row
                    row=new LinearLayout(this);
                    linear.addView(row);
                    row.setOrientation(LinearLayout.HORIZONTAL);
                    cur_col=0;
                }
                else
                {
                    //add delimiter
                    View delimiter =new View(this);
                    delimiter.setBackgroundColor(Color.DKGRAY);
                    delimiter.setLayoutParams(new LinearLayout.LayoutParams(1, LinearLayout.LayoutParams.FILL_PARENT));
                    row.addView(delimiter);
                }
            }
            {
                TextView text=new TextView(this);
                text.setGravity(Gravity.CENTER);
                text.setText(R.string.version);
                text.setTextSize(Settings.settings_header_text_size);
                text.setTextColor(Color.DKGRAY);
                linear.addView(text);
            }
            //Отображение
            AlertDialog.Builder menu_dialog=new AlertDialog.Builder(this);
            menu_dialog.setView(menu_scroll);
            Settings.postbox=menu_dialog.show();
            Settings.___________________LOG("Меню подано, сэр!", false);
            return true;
        }
        else if(current_view == SET_DRAW)//1 - draw     2 - settings     3 - about)
            return draw.keyEvent(event);
        else if(current_view == SET_SETTINGS)
            return settings.keyEvent(event);
        else if(current_view == SET_OTHERSETTINGS)
            return other_settings.keyEvent(event);
        else if(current_view == SET_PALETTE_BRUSH)
            return palette_editor.keyEvent(event);
        else if(current_view == SET_PALETTE_BACKGROUND)
            return palette_editor.keyEvent(event);
        else
            return keyEvent_backToCanvas(event);
    }
    public boolean keyEvent_backToCanvas(KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN)
        {
            if(event.getKeyCode()==KeyEvent.KEYCODE_BACK){
                return true;
            }
        }
        if(event.getAction() == KeyEvent.ACTION_UP)
        {
            if(event.getKeyCode()==KeyEvent.KEYCODE_BACK){
                set(1);
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }
    public void set(int num){
        Settings.___________________LOG("Отобразить экран " + String.valueOf(num), false);
        current_view = num; //1=draw     2=settings     3=about
        if(num == SET_DRAW)//draw
        {
            draw.setEraser(false);
            setContentView(draw);     //отобразить именно созданный класс
            //setContentView(new MyView(this));
        }
        if(num == SET_SETTINGS)//settings
        {
            if (settings == null){
                Settings.___________________LOG("Старт конструктора SettingsScreen", false);
                settings=new SettingsScreen();
            }
            settings.update(settings.UPDATE_ALL);
            setContentView(settings);     //отобразить именно созданный класс
        }
        if(num == SET_ABOUT)//about
            setContentView(R.layout.about);     //отобразить именно созданный класс
        if(num == SET_PALETTE_BRUSH)//palette_editor_brush
        {
            if(palette_editor == null) {
                Settings.___________________LOG("Старт конструктора PaletteEditor", false);
                palette_editor =new PaletteEditor();
            }
            palette_editor.update(palette_editor.AIM_BRUSH_COLOR);
            setContentView(palette_editor);
        }
        if(num == SET_PALETTE_BACKGROUND)//palette_editor_background
        {
            if(palette_editor == null) {
                Settings.___________________LOG("Старт конструктора PaletteEditor", false);
                palette_editor =new PaletteEditor(/*settings_storage*/);
            }
            palette_editor.update(palette_editor.AIM_BACKGROUD_COLOR);
            setContentView(palette_editor);
        }
        if(num == SET_OPEN)//open_file
        {
            if(fileSelector == null)
            {
                Settings.___________________LOG("Старт конструктора FileSelector", false);
                fileSelector =new FileSelector(this, new FileSelector.OnSelected() {
                    @Override public void onSelected(String filename) {
                        draw.open_file(filename);
                        set(SET_DRAW);
                    }
                });
            }
            fileSelector.update(fileSelector.dock_current);
            setContentView(fileSelector);
        }
        if(num == SET_OTHERSETTINGS)//settings
        {
            if (other_settings == null){
                Settings.___________________LOG("Старт конструктора OtherSettings", false);
                other_settings=new OtherSettings();
            }
            setContentView(other_settings);     //отобразить именно созданный класс
        }
    } //1 - draw     2 - settings     3 - about      4 - palette_editor     5 - palette_editor_background
    public Object onRetainNonConfigurationInstance() {
    return draw;
}
    public void onClick(View v) {      //для обработки кнопки ОК на экране about
        // по id определеяем кнопку, вызвавшую этот обработчик
        if (v.getId() == R.id.button_OK)
            set(SET_DRAW);
    }
    @Override public void onDestroy(){
        Settings.___________________LOG("Уничтожение...", false);
        if(Settings.autosave)
            draw.save_file(false);
        super.onDestroy();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if(requestCode==Settings.REQUEST_OPEN_FILE && resultCode == RESULT_OK) {
            Uri selectedImage = imageReturnedIntent.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            set(SET_DRAW);
            draw.open_file(filePath);
        }
    }
}
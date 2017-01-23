package com.fsoft.FP_sDraw;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: Dr. Failov
 * Date: 16.03.13
 * Time: 17:33
 */

//singletone
public class Settings {
    static final int REQUEST_OPEN_FILE=42;
    static Context context;
    static String  TAG="myTag";//temporary value
    static int[] palette_brush_default = {
            Color.parseColor("#810049"),   //magenta
            Color.parseColor("#ff44a0"),
            Color.parseColor("#8067ee"),   //violet
            Color.parseColor("#fea3f1"),
            Color.parseColor("#3f48cc"),   //blue
            Color.parseColor("#00a2e8"),
            Color.parseColor("#22b14c"),   //green
            Color.parseColor("#b5e61d"),
            Color.parseColor("#ff9e20"),   //orange
            Color.parseColor("#fff200"),   //yellow
            Color.parseColor("#ed1c24"),   //red
            Color.parseColor("#ffaec9"),
            Color.parseColor("#6c423d"),   //brown
            Color.parseColor("#a06755"),
            Color.parseColor("#7f7f7f"),   //gray
            Color.parseColor("#c3c3c3"),
            Color.parseColor("#000000"),   //black
            Color.parseColor("#ffffff"),   //white
    };
    static int[] palette_background_default = {
            Color.parseColor("#ffb5b5"), //red
            Color.parseColor("#3c0000"), //red
            Color.parseColor("#ffb5eb"), //violet
            Color.parseColor("#3c0037"), //violet
            Color.parseColor("#b7b5ff"), //blue
            Color.parseColor("#01003c"), //blue
            Color.parseColor("#b5fcff"), //cyan
            Color.parseColor("#003a3c"), //cyan
            Color.parseColor("#bcffb5"), //green
            Color.parseColor("#003c12"), //green
            Color.parseColor("#feffb5"), //yellow
            Color.parseColor("#3c3800"), //yellow
            Color.parseColor("#ffdab5"), //brown
            Color.parseColor("#452400"), //brown
            Color.parseColor("#b6b6b6"), //black
            Color.parseColor("#000000") //black
    };
    static String save_path_default;
    static String autosave_path_default;
    static LinearLayout.LayoutParams layout_params_slider=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    static LinearLayout.LayoutParams layout_params_header=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    static int palette_height=2;
    static int palette_item_size;
    static int settings_header_text_size=17;
    static point display_size =new point(480, 800);
    static point preview_size;
    static int DPI;
    static public Object postbox=null;

    static Integer background_color = Color.argb(255, 30, 0, 10);
    static Integer brush_color = Color.argb(255, 200, 40, 100);
    static double brush_size = 4;
    static double eraser_size = 40;
    static int manage_method = 3; //brush size managing //1=size, 2=speed, 3=none
    static boolean antialiasing = true;
    static boolean smoothing = true;
    static String save_path;
    static String save_fileprefix;
    static String autosave_path;
    static boolean autosave;
    static int autosave_limit;
    static boolean debug;
    static int palette_brush_counter;
    static int palette_background_counter;
    static int[] palette_brush;
    static int[] palette_background;
    static int undo_size=5;

    private Settings(){}
    static public void init(Context c){
        //Определение констант...
        context=c;
        TAG=((sDraw)context).TAG;
        ___________________LOG("Инициализация Settings...", false);
        save_path_default = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/sDraw";
        autosave_path_default = context.getCacheDir().toString();
        DPI=context.getResources().getDisplayMetrics().densityDpi;
        layout_params_slider.setMargins(20, 0, 20, 5);
        layout_params_header.setMargins(0, 5, 0, 0);
        palette_item_size = DPI/4;
        preview_size = new point((int)display_size.x, palette_item_size*2);
        ___________________LOG("Получены данные: "+
                "\n context="+context.toString()+
                "\n save_path_default=" + save_path_default +
                "\n autosave_path_default=" + autosave_path_default +
                "\n DPI=" + DPI +
                "\n layout_params_slider=" + layout_params_slider.toString() +
                "\n layout_params_header=" + layout_params_header.toString() +
                "\n palette_item_size=" + palette_item_size +
                "\n preview_size=" + preview_size  , false);//create different variables
        load_settings();
    }
    static public void load_settings(){
        ___________________LOG("Загрузка настроек...", false);//create different variables
        SharedPreferences prefs = ((Activity)context).getPreferences(Context.MODE_PRIVATE);
        //colors
        brush_color = prefs.getInt("brush_color", -8847426);//trust me - i'm ingeneer
        background_color = prefs.getInt("background_color", -15456226);
        //sizes
        brush_size = prefs.getFloat("brush_size", 8.0f);
        eraser_size = prefs.getFloat("eraser_size", 40.0f);
        //processing
        manage_method = prefs.getInt("manage_method", 2);
        antialiasing = prefs.getBoolean("antialiasing", true);
        smoothing = prefs.getBoolean("smoothing", true);
        //saving files
        save_path = prefs.getString("save_path", save_path_default);
        save_fileprefix = prefs.getString("save_fileprefix", "sDraw");
        autosave_path = prefs.getString("autosave_path", autosave_path_default);
        autosave = prefs.getBoolean("autosave", true);
        autosave_limit = prefs.getInt("autosave_limit", 40);
        //developer tools
        debug = prefs.getBoolean("debug", false);
        undo_size = prefs.getInt("undo_size", 5);

        ___________________LOG("Получены данные: "+
                "\n brush_color="+brush_color+
                "\n background_color=" + background_color +
                "\n brush_size=" + brush_size +
                "\n eraser_size=" + eraser_size +
                "\n manage_method=" + manage_method +
                "\n antialiasing=" + antialiasing +
                "\n smoothing=" + smoothing +
                "\n save_path=" + save_path +
                "\n save_fileprefix=" + save_fileprefix +
                "\n autosave_path=" + autosave_path +
                "\n autosave=" + autosave +
                "\n autosave_limit=" + autosave_limit +
                "\n undo_size=" + undo_size +
                "\n debug=" + debug  , false);//create different variables

        //palette brush
        palette_brush_counter=prefs.getInt("palette_brush_counter", palette_brush_default.length);
        palette_brush=new int[palette_brush_counter];
        for(int i=0; i<palette_brush_counter; i++)
            palette_brush[i]=prefs.getInt("palette_brush_"+String.valueOf(i),palette_brush_default[i]);

        //palette background
        palette_background_counter=prefs.getInt("palette_background_counter", palette_background_default.length);
        palette_background=new int[palette_background_counter];
        for(int i=0; i<palette_background_counter; i++)
            palette_background[i]=prefs.getInt("palette_background_"+String.valueOf(i),palette_background_default[i]);
    }
    static public void save_settings(){
        ___________________LOG("Сохранение настроек...", false);
        SharedPreferences prefs = ((Activity)context).getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt("background_color", background_color);
        edit.putInt("brush_color", brush_color);
        edit.putFloat("brush_size", (float) brush_size);
        edit.putFloat("eraser_size", (float) eraser_size);
        edit.putInt("manage_method", manage_method);
        edit.putBoolean("antialiasing", antialiasing);
        edit.putBoolean("smoothing", smoothing);
        edit.putString("save_path", save_path);
        edit.putBoolean("autosave", autosave);
        edit.putString("autosave_path", autosave_path);
        edit.putInt("autosave_limit", autosave_limit);
        edit.putString("save_fileprefix", save_fileprefix);
        edit.putBoolean("debug", debug);
        edit.putInt("undo_size", undo_size);
        edit.commit();
    }
    static public void clear_settings(){
        ___________________LOG("Запрошено полное стирание параметров", false);
        SharedPreferences prefs = ((Activity)context).getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }
    static public boolean isTutor(String subject, int limit){
        ___________________LOG("Проверка требования обучения на тему " + subject, false);
        SharedPreferences prefs = ((Activity)context).getPreferences(Context.MODE_PRIVATE);
        String tag = "Tutor_" + subject;
        int current_times = prefs.getInt(tag, 0);
        if(current_times < limit)
        {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putInt(tag, current_times+1);
            edit.commit();
            return true;
        }
        else
            return false;
    }
    static public void ___________________LOG(String text, boolean display){
        Log.d(TAG, text);
        if(debug || display)
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}

package com.fsoft.FP_sDraw;

import android.content.Context;
import android.graphics.*;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;

/**
 * Created with IntelliJ IDEA.
 * User: Dr. Failov
 * Date: 23.03.13
 * Time: 19:34
 */
public class OtherSettings extends LinearLayout {
    Context context;
    EditText undo_steps_edittext;
    EditText save_path_edittext;
    EditText save_fileprefix_edittext;
    EditText autosave_path_edittext;
    EditText autosave_limit_edittext;
    CheckBox autosave_checkbox;
    CheckBox debug_checkbox;

    public OtherSettings(){
        super(Settings.context);
        Settings.___________________LOG("Инициализация...", false);
        context = Settings.context;

        Settings.___________________LOG("Задание параметров формы...", false);
        this.setOrientation(VERTICAL);
        ScrollView scroll=new ScrollView(context);
        LinearLayout.LayoutParams layout_params_common=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
        scroll.setLayoutParams(layout_params_common);
        this.addView(scroll);
        LinearLayout linear=new LinearLayout(context);
        linear.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        scroll.addView(linear);
        linear.setOrientation(VERTICAL);
//   //--------------------------------------------------------------------------------------------------------------   main text
        {
            //TextView others
            TextView other_text=new TextView(context);
            other_text.setText("Другие настройки");
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
//   //--------------------------------------------------------------------------------------------------------------   undo_limit_text
        {
            Settings.___________________LOG("Настройка блока отмены действий...", false);
            TextView undo_limit_text=new TextView(context);
            undo_limit_text.setText("Отмена\\Повтор действий");
            undo_limit_text.setTextSize(Settings.settings_header_text_size);
            undo_limit_text.setGravity(Gravity.CENTER);
            undo_limit_text.setLayoutParams(Settings.layout_params_header);
            undo_limit_text.setTypeface(Typeface.DEFAULT_BOLD);
            linear.addView(undo_limit_text);

            //TextView saving_path_text
            TextView saving_path_text=new TextView(context);
            saving_path_text.setText("Количество шагов отмены");
            saving_path_text.setGravity(Gravity.LEFT);
            saving_path_text.setTypeface(Typeface.DEFAULT);
            linear.addView(saving_path_text);

            //text info
            TextView other_settings_info=new TextView(context);
            other_settings_info.setText("Чем больше шагов, тем больше оперативной памяти будет использоваться!\n" +
                    "Переполнение оперативной памяти будет приводить к вылетам программы\n" +
                    "Рукомендуемое значение - 5");
            other_settings_info.setTextSize((int) (Settings.settings_header_text_size * 0.7));
            other_settings_info.setGravity(Gravity.LEFT);
            other_settings_info.setTypeface(Typeface.DEFAULT);
            other_settings_info.setTextColor(Color.GRAY);
            linear.addView(other_settings_info);

            //EditText undo steps
            undo_steps_edittext=new EditText(context);
            undo_steps_edittext.setText(String.valueOf(Settings.undo_size));
            linear.addView(undo_steps_edittext);
        }
//   //--------------------------------------------------------------------------------------------------------------   saving file
        {
            Settings.___________________LOG("Настройка блока сохранения файлов...", false);
            TextView saving_file_text=new TextView(context);
            saving_file_text.setText("Сохранение файлов");
            saving_file_text.setTextSize(Settings.settings_header_text_size);
            saving_file_text.setGravity(Gravity.CENTER);
            saving_file_text.setLayoutParams(Settings.layout_params_header);
            saving_file_text.setTypeface(Typeface.DEFAULT_BOLD);
            linear.addView(saving_file_text);

            //TextView saving_path_text
            TextView saving_path_text=new TextView(context);
            saving_path_text.setText("Место сохранения ");
            saving_path_text.setGravity(Gravity.LEFT);
            saving_path_text.setTypeface(Typeface.DEFAULT);
            linear.addView(saving_path_text);

            //EditText saving path
            save_path_edittext=new EditText(context);
            save_path_edittext.setText(Settings.save_path);
            linear.addView(save_path_edittext);

            //Button restore
            Button save_path_restore_button = new Button(context);
            save_path_restore_button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    save_path_edittext.setText(Settings.save_path_default);
                }});
            save_path_restore_button.setText("Восстановить адрес по-умолчанию");
            layout_params_common = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layout_params_common.gravity=Gravity.RIGHT;
            save_path_restore_button.setLayoutParams(layout_params_common);
            linear.addView(save_path_restore_button);

            //TextView saving_prefix
            TextView saving_prefix=new TextView(context);
            saving_prefix.setText("Префикс файла ");
            saving_prefix.setGravity(Gravity.LEFT);
            saving_prefix.setTypeface(Typeface.DEFAULT);
            linear.addView(saving_prefix);

            //EditText prefix prefix
            save_fileprefix_edittext=new EditText(context);
            save_fileprefix_edittext.setText(Settings.save_fileprefix);
            linear.addView(save_fileprefix_edittext);

            //checkbox autosave_checkbox
            autosave_checkbox = new CheckBox(context);
            autosave_checkbox.setText("Автосохранение при выходе");
            autosave_checkbox.setChecked(Settings.autosave);
            linear.addView(autosave_checkbox);

            //TextView autosave
            TextView autosaving_limit_text=new TextView(context);
            autosaving_limit_text.setText("Лимит автосохранения");
            autosaving_limit_text.setGravity(Gravity.LEFT);
            autosaving_limit_text.setTypeface(Typeface.DEFAULT);
            linear.addView(autosaving_limit_text);
            //text info
            TextView other_settings_info=new TextView(context);
            other_settings_info.setText("Рекомендуемое значение - 40\n" +
                    "0 - не чистить");
            other_settings_info.setTextSize((int) (Settings.settings_header_text_size * 0.7));
            other_settings_info.setGravity(Gravity.LEFT);
            other_settings_info.setTypeface(Typeface.DEFAULT);
            other_settings_info.setTextColor(Color.GRAY);
            linear.addView(other_settings_info);

            //EditText autosaving limit
            autosave_limit_edittext=new EditText(context);
            autosave_limit_edittext.setText(String.valueOf(Settings.autosave_limit));
            autosave_limit_edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
            linear.addView(autosave_limit_edittext);

            //TextView autosave
            TextView autosaving_path_text=new TextView(context);
            autosaving_path_text.setText("Место  автосохраниения");
            autosaving_path_text.setGravity(Gravity.LEFT);
            autosaving_path_text.setTypeface(Typeface.DEFAULT);
            linear.addView(autosaving_path_text);

            //EditText autosaving path
            autosave_path_edittext=new EditText(context);
            autosave_path_edittext.setText(Settings.autosave_path);
            linear.addView(autosave_path_edittext);

            //Button restore
            Button autosave_path_restore_button = new Button(context);
            autosave_path_restore_button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    autosave_path_edittext.setText(Settings.autosave_path_default);
                }});
            autosave_path_restore_button.setText("Восстановить адрес по-умолчанию");
            layout_params_common = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layout_params_common.gravity=Gravity.RIGHT;
            autosave_path_restore_button.setLayoutParams(layout_params_common);
            linear.addView(autosave_path_restore_button);
        }
        {
            //-----------------------------------------add delimiter
            View delimiter =new View(context);
            delimiter.setBackgroundColor(Color.DKGRAY);
            delimiter.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 1));
            linear.addView(delimiter);
        }
//   //--------------------------------------------------------------------------------------------------------------   developer toolls
        {
            Settings.___________________LOG("Настройка блока для разработчика...", false);
            //TextView developer
            TextView developer_text=new TextView(context);
            developer_text.setText("Для разработчика");
            developer_text.setTextSize(Settings.settings_header_text_size);
            developer_text.setLayoutParams(Settings.layout_params_header);
            developer_text.setGravity(Gravity.CENTER);
            developer_text.setTypeface(Typeface.DEFAULT_BOLD);
            linear.addView(developer_text);

            //checkbox debug
            debug_checkbox = new CheckBox(context);
            debug_checkbox.setText("Сообщения отладки");
            debug_checkbox.setChecked(Settings.debug);
            linear.addView(debug_checkbox);

            //Button delete
            Button delete_all_button = new Button(context);
            delete_all_button.setOnClickListener(new OnClickListener() {
                @Override public void onClick(View view) {
                    Settings.clear_settings();
                    ((sDraw)context).finish();
                }});
            delete_all_button.setText("Стереть все параметры полностью");
            layout_params_common = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layout_params_common.gravity=Gravity.RIGHT;
            layout_params_common.setMargins(0, 0, 10, 10);
            delete_all_button.setLayoutParams(layout_params_common);
            linear.addView(delete_all_button);
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
        {
            //Button Apply
            Button button_save=new Button(context);
            button_save.setId(1);
            button_save.setText("Применить");
            button_save.setOnClickListener(new OnClickListener(){ @Override  public void onClick(View view){ apply(); }});
            layout_params_common=new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
            layout_params_common.setMargins(20, 30, 20, 10);
            button_save.setLayoutParams(layout_params_common);
            button_save.setPadding(0, 30, 0, 30);
            linear.addView(button_save);
        }
    }
    public void apply(){
        Settings.___________________LOG("Обработка данных перед записью...", false);
        Settings.undo_size = Integer.parseInt(undo_steps_edittext.getText().toString());
        Settings.debug = debug_checkbox.isChecked();
        Settings.autosave = autosave_checkbox.isChecked();
        Settings.save_path = save_path_edittext.getText().toString();
        Settings.autosave_path = autosave_path_edittext.getText().toString();
        Settings.autosave_limit = Integer.parseInt(autosave_limit_edittext.getText().toString());
        Settings.save_fileprefix = save_fileprefix_edittext.getText().toString();
        Settings.___________________LOG("Переход на настройки...", false);
        ((sDraw)context).set(sDraw.SET_SETTINGS);
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
}

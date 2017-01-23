package com.fsoft.FP_sDraw;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import java.io.File;
import java.io.FilenameFilter;

/**
 * Created with IntelliJ IDEA.
 * User: Dr. Failov
 * Date: 08.03.13
 * Time: 22:26
 */
public class FileSelector extends LinearLayout {
    public interface OnSelected
    {
        public void onSelected(String filename);
    }
    final int DOCK_SAVED=1;
    final int DOCK_AUTOSAVED=2;
    int dock_current=DOCK_SAVED;
    Context context;
    LinearLayout primary_layout=this;
    LinearLayout header;
    ScrollView body;
    ProgressDialog dialog_loading;
    int cols;
    int item_w;
    int item_h;

    private OnSelected onSelected = null;
    public FileSelector(Context co, OnSelected listener)
    {
        super(co);
        Settings.___________________LOG("Инициализация: FileSelector...", false);
        context=co;
        onSelected=listener;
        dialog_loading=new ProgressDialog(context);
        cols=(int)(Settings.display_size.x/(0.6*Settings.DPI));
        cols = cols<2 ? 2 : cols;
        item_w=(int)((Settings.display_size.x-20)/cols);
        item_h=(int)(item_w*Settings.display_size.y/Settings.display_size.x);
        dialog_loading.setMessage("Загрузка...");
        Settings.___________________LOG("Инициализация: primary_layout...", false);
        primary_layout.setOrientation(LinearLayout.VERTICAL);
        {
            //   //--------------------------------------------------------------------------------------------------------------   main text
            {
                //TextView others
                TextView other_text=new TextView(context);
                other_text.setText("Открыть файл");
                other_text.setTextSize((int)(Settings.settings_header_text_size*1.5));
                other_text.setLayoutParams(Settings.layout_params_header);
                other_text.setGravity(Gravity.CENTER);
                other_text.setTypeface(Typeface.DEFAULT_BOLD);
                primary_layout.addView(other_text);
            }
            {
                //-----------------------------------------add delimiter
                View delimiter =new View(context);
                delimiter.setBackgroundColor(Color.DKGRAY);
                delimiter.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 1));
                primary_layout.addView(delimiter);
            }
            Settings.___________________LOG("Инициализация: header...", false);
            header=new LinearLayout(context);
            header.setOrientation(LinearLayout.HORIZONTAL);
            primary_layout.addView(header);
            {
                LayoutParams header_lp=new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                header_lp.setMargins(2,1,2,1);

                Button header_saved = new Button(co);
                header_saved.setText                        ("Сохранено");
                header_saved.setLayoutParams(header_lp);
                header_saved.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View view) {update(DOCK_SAVED); }});
                header.addView(header_saved);

                Button header_autosaved = new Button(co);
                header_autosaved.setText                        ("Автосохранения");
                header_autosaved.setLayoutParams(header_lp);
                header_autosaved.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View view) { update(DOCK_AUTOSAVED);  }  });
                header.addView(header_autosaved);

                Button header_gallery = new Button(co);
                header_gallery.setText                        ("Галерея");
                header_gallery.setLayoutParams(header_lp);
                header_gallery.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View view) {
                    // select a file
                    Intent i = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    ((Activity)context).startActivityForResult(i, Settings.REQUEST_OPEN_FILE);
                }  });
                header.addView(header_gallery);
            }
            Settings.___________________LOG("Инициализация: body...", false);
            body=new ScrollView(context);
            primary_layout.addView(body);
            update(DOCK_SAVED);
        }
    }
    public void update(int aim) {
         Settings.___________________LOG("Обновление данных по запросу "+String.valueOf(aim), false);
        for(int i=0; i<header.getChildCount(); i++)
            header.getChildAt(i).setBackgroundColor(Color.GRAY);
         if(aim==DOCK_SAVED)
         {
             dock_current=DOCK_SAVED;
             header.getChildAt(0).setBackgroundColor(Color.WHITE);
             new setTask().execute( Settings.save_path, context);
         }
         if(aim==DOCK_AUTOSAVED)
         {
             dock_current=DOCK_AUTOSAVED;
             header.getChildAt(1).setBackgroundColor(Color.WHITE);
             new setTask().execute(Settings.autosave_path, context);
         }
    }
    class setTask extends AsyncTask<Object, String, Void> {
        LinearLayout aim;
        String folder;
        Context context1;

        @Override protected void onPreExecute() {
            super.onPreExecute();
            Settings.___________________LOG("onPreExecute...", false);
            dialog_loading.show();

        }
        @Override protected Void doInBackground(Object... params)
        {
            publishProgress("doInBackground...", "0");
            aim=new LinearLayout(context);
            folder = (String)params[0];
            context1 = (Context)params[1];
            if(aim.getChildCount() == 0)
            {
                publishProgress("Get files...", "0");
                aim.setOrientation(LinearLayout.VERTICAL);
                File dir = new File(folder);
                String[] files;
                if(dir.isDirectory()){
                    files = dir.list(new FilenameFilter() { @Override public boolean accept(File file, String s) {
                        return s.endsWith(".jpg");
                    } });
                }else
                {
                    publishProgress("Где-то произошла ошибка", "1");
                    files=null;
                }
                publishProgress("create LayoutParams...", "0");
                LayoutParams lp;
                if(files==null || files.length == 0)
                {
                    TextView txt=new TextView(context);
                    txt.setText("Тут абсолютно пусто.");
                    aim.addView(txt);
                    publishProgress("create Button...", "0");
                    Button button_refresh=new Button(context);
                    button_refresh.setText("Обновить");
                    button_refresh.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            body.removeView(aim);
                            aim.removeAllViews();
                            update(dock_current);
                        }
                    });
                    aim.addView(button_refresh);
                }
                else
                {
                   //java.util.Arrays.sort(files, 0, files.length);
                    for(int i=0;i<files.length;i++)               //sort
                        for(int j=1;j<files.length;j++)
                            if(files[j].compareToIgnoreCase(files[j-1])>0) {
                                 String tmp = files[j-1];
                                 files[j-1] = files[j];
                                files[j] = tmp;
                            }
                    publishProgress("Creating grid...", "0");
                    LinearLayout row=new LinearLayout(context);
                    aim.addView(row);
                    row.setOrientation(LinearLayout.HORIZONTAL);
                    int cur_col=0;
                    for(int i=0; i<files.length; i++)
                    {
                        //prepare button
                        ImageButton image=new ImageButton(context);
                        image.setOnLongClickListener(new OnLongClickListener() {
                            @Override public boolean onLongClick(View view) {
                                AlertDialog.Builder a=new AlertDialog.Builder(context);
                                a.setIcon(new BitmapDrawable(((sDraw) context).draw.decodeFile(folder + File.separator + view.getTag(), (int)((float)Settings.DPI*(2/3)), Settings.DPI)));
                                a.setTitle("Удалить");
                                a.setMessage("Вы действительно хотите удалить файл " + view.getTag() + "?");
                                Settings.postbox = view.getTag();//можно использовать для передачи данных между потоками, интерфейсами, и много другого
                                a.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                                    @Override public void onClick(DialogInterface dialogInterface, int i) {
                                        //удалить файл
                                        if(!new File(folder + File.separator + Settings.postbox).delete()) Settings.___________________LOG("Удалить не получилось", true);
                                        //обновить список
                                        body.removeView(aim);
                                        aim.removeAllViews();
                                        update(dock_current);
                                }});
                                a.show();
                                return true;  //To change body of implemented methods use File | Settings | File Templates.
                            }
                        });
                        //image.setImageBitmap(Bitmap.createScaledBitmap( BitmapFactory.decodeFile(folder + File.separator + files[i]), item_w, item_h, false));
                        image.setImageBitmap(Bitmap.createScaledBitmap( ((sDraw)context).draw.decodeFile(folder + File.separator + files[i], item_w, item_h), item_w, item_h, false));
                        lp=new LinearLayout.LayoutParams(item_w, item_h);
                        lp.setMargins(4,4,4,4);
                        image.setLayoutParams(lp);
                        image.setTag(files[i]);
                        image.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View view) {
                            body.removeView(aim);
                            aim.removeAllViews();
                            onSelected.onSelected(folder + File.separator + view.getTag());}});
                        //add it to grid
                        row.addView(image);
                        cur_col++;
                        if(cur_col >= cols)
                        {
                            row=new LinearLayout(context);
                            aim.addView(row);
                            row.setOrientation(LinearLayout.HORIZONTAL);
                            cur_col=0;
                        }
                    }

                    //кнопка "обновить" и количество элементов
                    lp=new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.weight=1;
                    lp.setMargins(10, 0, 10, 20);
                    publishProgress("create TextView...", "0");
                    TextView text_count=new TextView(context);
                    text_count.setLayoutParams(lp);
                    text_count.setText("Всего: " + String.valueOf(files.length));
                    publishProgress("create Button...", "0");
                    Button button_refresh=new Button(context);
                    button_refresh.setText("Обновить");
                    button_refresh.setLayoutParams(lp);
                    button_refresh.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            body.removeView(aim);
                            aim.removeAllViews();
                            update(dock_current);
                        }
                    });
                    publishProgress("create LinearLayout...", "0");
                    LinearLayout first_row=new LinearLayout(context);
                    first_row.setOrientation(LinearLayout.HORIZONTAL);
                    first_row.addView(text_count);
                    first_row.addView(button_refresh);
                    first_row.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    aim.addView(first_row);
                }
            }
            return null;
        }
        @Override protected void onProgressUpdate(String... message) {
            Settings.___________________LOG(message[0], message[1]=="1");
        }
        @Override protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Settings.___________________LOG("onPostExecute...", false);
            body.removeAllViews();
            body.addView(aim);
            dialog_loading.dismiss();
        }
    }
}

package com.fsoft.FP_sDraw;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.*;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.content.Context;
import android.graphics.Bitmap.Config;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

public class Draw extends View
{
    Context context;
    Bitmap bitmap;
    Canvas mCanvas;
    Paint paint;
    int multitouch_max=10;
    int touch_history_size = 2;
    point[][] touch_history = new point[touch_history_size][multitouch_max];
    point touch[]=new point[multitouch_max];
    point display_size =new point(480, 800);
    boolean eraser=false;  //активен ли сейчас ластик
    //для обработки жестов
    boolean gesture=false;   //обрабатывает ли сейчас программа жесты
    point gesture_start=new point(0,0);
    boolean gesture_hint=false;
    // для стабилизации стачков толщины
    double last_size=5;
    //для управления особенными типами нажатий (типа двойного)
    int btndown_times=0;
    long btndown_last_time;
    int press_speed=500;
    //для ситсемы логгирования
    String TAG="MyTag";  //temporary
    public Draw(point new_display_size){
        //запуск родительского конструктора
        super(Settings.context);//base
        Settings.___________________LOG("Родительский конструктор выполнен", false);
        //получение служебных данных
        context = Settings.context;
        TAG = ((sDraw)context).TAG;
        last_size=Settings.brush_size;
        display_size.set(new_display_size);
        Settings.___________________LOG("Данные получены: размер кисти="+String.valueOf(last_size)+" размер холста="+display_size.toString()+" TAG="+TAG, false);
        //Инициализация истории
        for(int i=0;i< touch_history_size;i++)         { //history keys
            for(int j=0; j<multitouch_max; j++)//fingers
                touch_history[i][j] = new point(-1, -1);    //Да, это вызов конструктора для каждого элемента массива отдельно...о_О
        }
        //Инициализация: массив прикосновений...
        for(int i=0;i<touch.length;i++)
            touch[i]=new point(0,0);
        //Инициализация: UndoProvider...
        UndoProvider.init(this);
        Settings.___________________LOG("Инициализировано touch_history,  touch и UndoProvider: touch_history_size="+String.valueOf(touch_history_size)+ " touch_size="+String.valueOf(touch.length)+ " multitouch_max="+String.valueOf(multitouch_max), false);
        //Инициализация: Bitmap & Canvas, Рaint и красим Bitmap...
        bitmap = Bitmap.createBitmap((int) display_size.x, (int) display_size.y, Config.ARGB_8888);//создаем новый битмап для рислвания на нем
        mCanvas = new Canvas(bitmap);//создаем холст для рисования на битмапе
        //Инициализация: Рaint и красим Bitmap...
        paint=new Paint();// закрашиваем begin
        paint.setAntiAlias(Settings.antialiasing);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Settings.background_color);
        mCanvas.drawPaint(paint);// закрашиваем end
        paint.setColor(Settings.brush_color);// подготавливаем к рисованию
        paint.setStrokeWidth(9);
        Settings.___________________LOG("Bitmap создан, Canvas инициализирован, Paint настроен", false);
        if(Settings.isTutor("start", 1)) //знакомство с программой
        {
            Settings.___________________LOG("Подготовка экрана приветствия...", false);
            Bitmap tmp_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.help);
            Matrix matrix = new Matrix();
            point scale=new point(display_size.x/(float)tmp_bitmap.getWidth() , display_size.y/(float)tmp_bitmap.getHeight());
            Settings.___________________LOG("Маштабирование: "+scale.toString(), false);
            matrix.postScale(scale.x, scale.y);
            Settings.___________________LOG("Отрисовка приветствия...", false);
            mCanvas.drawBitmap(tmp_bitmap, matrix, paint);
        }

        UndoProvider.backup();
    }  //constructor
    public String save_file(boolean from_user){
        Settings.___________________LOG("Инициализация сохранения файла...", false);
        String filename;
        File path;
        boolean empty = false;
        boolean save;
        boolean folder;
        //принять решения
        if(from_user)  //from user
        {
            path = new File(Settings.save_path);
            filename = Settings.save_fileprefix+"_";
            save = true;
        }
        else            //autosave
        {
            path = new File(Settings.autosave_path);
            filename = "autosave_";
            empty = isempty();
            save = !empty;
        }
        Settings.___________________LOG( "\nФайл " + (save?"сохранить. \n":"не сохранять. \n") + "Рисунок " + (empty?"пустой. \n":"не пустой. \n") + "По адресу: " + path , false);
        if(save)//принято решение сохранять
        {
            //дополнить имя файла
            Calendar date = Calendar.getInstance();
            filename += String.valueOf(date.get(Calendar.YEAR)) + "-";
            if(date.get(Calendar.MONTH)+1 < 10) filename += "0";
            filename += String.valueOf(date.get(Calendar.MONTH)+1) + "-";
            if(date.get(Calendar.DAY_OF_MONTH) < 10) filename += "0";
            filename += String.valueOf(date.get(Calendar.DAY_OF_MONTH)) + "_";
            if(date.get(Calendar.HOUR_OF_DAY) < 10) filename += "0";
            filename += String.valueOf(date.get(Calendar.HOUR_OF_DAY)) + "-";
            if(date.get(Calendar.MINUTE) < 10) filename += "0";
            filename += String.valueOf(date.get(Calendar.MINUTE)) + "-";
            if(date.get(Calendar.SECOND) < 10) filename += "0";
            filename += String.valueOf(date.get(Calendar.SECOND)) + ".jpg";

            //если надо - создать папку
            folder = path.isDirectory();
            if(!folder) {
                Settings.___________________LOG("Создание папки " + path + "...", false);
                folder = path.mkdirs();
            }

            //если папку созать не удалось
            if(!folder){
                Settings.___________________LOG("Возникла какая-то проблема с папкой " + path, false);
                Toast.makeText(context, "Возникла какая-то проблема с папкой", Toast.LENGTH_SHORT).show();
            }

            //если с папкой всё ок - пробуем сохранить файл
            if(folder){
                File file = new File(path, filename);
                boolean error = false;
                Settings.___________________LOG("Сохранение по адресу " + path + "/" + filename + " ...", false);
                try{
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
                }catch (Exception e) {
                    Settings.___________________LOG("Ошибка: " + e.toString(), true);
                    error = true;
                    //Toast.makeText(context, "Ошибка: " + e.toString(), Toast.LENGTH_SHORT).show();
                }
                if(!error){
                    if(from_user)
                        Settings.___________________LOG("Файл успешно сохранен по адресу: " + path.getAbsolutePath() + File.separator + filename, true);
                    //Обновить содержимое галареи
                    if(!from_user){
                        File nomedia = new File(path, ".nomedia");
                        Settings.___________________LOG("Создание файла "+ nomedia, false);
                        try{
                            if(!nomedia.createNewFile()) Settings.___________________LOG("Файл .nomedia уже есть", false);
                        }catch (Exception e) {
                            Settings.___________________LOG("Не удалось спокойно создать файл nomedia. Грядут проблемы...\n"+ e.toString(), true);
                        }
                    }
                    else { //обновить содержимое галереи для того чтобы файл сразу там появился
                        Settings.___________________LOG("Попытка внести файл в галерею: " + file, false);
                        add_to_gallery(file.getAbsolutePath());
                    }
                }
            }
        }
        return path.getAbsolutePath() + File.separator + filename;
    }   //сохранить файл по адресу указанному в настройках
    public void open_file(String path){
        Settings.___________________LOG("Открыть файл "+path, false);
        if(new File(path).isFile())
        {
            Bitmap tmp_bitmap=decodeFile(path, (int)display_size.x, (int)display_size.y);
            if(tmp_bitmap!=null)
            {
                Settings.___________________LOG("Bitmap инициализирован: "+tmp_bitmap.toString(), false);
                Matrix matrix = new Matrix();
                Settings.___________________LOG("Matrix инициализирован: "+matrix.toShortString(), false);
                point scale=new point(display_size.x/(float)tmp_bitmap.getWidth() , display_size.y/(float)tmp_bitmap.getHeight());
                Settings.___________________LOG("Маштабирование: "+scale.toString(), false);
                matrix.postScale(scale.x, scale.y);
                Settings.___________________LOG("Исполнение drawBitmap...", false);
                mCanvas.drawBitmap(tmp_bitmap, matrix, paint);
                UndoProvider.backup();
                invalidate();
            }
        }
        else {
            Settings.___________________LOG("Файл не существует: "+path, true);
        }
    }   //прорисовать файл на битмапе
    public boolean isempty(){
        boolean result_empty=true;
        int zero=bitmap.getPixel(0, 0);
        for(int i=0; i<bitmap.getWidth(); i+=5)//x
            for(int j=0; j<bitmap.getHeight(); j+=5)//y
                if(bitmap.getPixel(i, j) != zero)
                    result_empty=false;
        Settings.___________________LOG("Проверка: явлиется ли рисунок пустым? - " + (result_empty?"да":"нет"), false);
        return result_empty;
    }   //проверка является рисунок пустым (сравнивается с 0 пикселем)
    public void setGesture(boolean val){
        gesture=val;
        gesture_hint=gesture;
        invalidate();
        Settings.___________________LOG(val ? "Режим обработки жестов" : "Режим рисования", false);
    }  //включить или выключить ластик
    public void setEraser(boolean val){
        eraser=val;
        Settings.___________________LOG(val ? "Ластик активирован" : "Ластик деактивирован", false);
    }  //включить или выключить ластик
    public void clear(){
        setEraser(false);
        save_file(false);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Settings.background_color);
        mCanvas.drawPaint(paint);// закрашиваем
        Settings.___________________LOG("Рисунок очищен", false);
        UndoProvider.backup();
        invalidate();
    }          //очитстить рисунок (залить цветом фона
    public void line(point begin, point end, float depth, int color){
        paint.setAntiAlias((color != Settings.background_color) && Settings.antialiasing);
        paint.setColor(color);
        paint.setStrokeWidth(depth);
        mCanvas.drawLine(begin.x, begin.y, end.x, end.y, paint);
        mCanvas.drawCircle(end.x, end.y, depth / 2, paint);
    }   //провести линию с точки 1 в точку 2 заданого цвета и толщины
    @Override protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        //canvas.drawBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.help), 0, 0, paint);   //попытка рисовать под низом фон
        canvas.drawBitmap(bitmap, 0, 0, paint);
        if(gesture_hint)
        {
            //int size=(int)((display_size.y < display_size.x ? display_size.y : display_size.x)*0.6);
            //int size=300;
            int size=(int)(Settings.DPI*1.2);
            //check
//             canvas.drawRect(
//                     (display_size.x-size)/2,
//                     (display_size.y-size)/2,
//                     (display_size.x+size)/2,
//                     (display_size.y+size)/2, paint);
            //canvas.drawBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.gesture_hint), m, paint);   //попытка рисовать под низом фон

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(context.getResources(), R.drawable.gesture_hint, options);
            int height = options.outHeight;
            int width = options.outWidth;

            Rect drawableRect = new Rect(0, 0, width, height);
            RectF viewRect = new RectF(
                    (display_size.x-size)/2,
                    (display_size.y-size)/2,
                    (display_size.x+size)/2,
                    (display_size.y+size)/2);
            options=new BitmapFactory.Options();
            options.inScaled=false;
            canvas.drawBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.gesture_hint, options), drawableRect, viewRect, new Paint());
        }
    }        //всего-то прорисовать битмап
    @Override public boolean onTouchEvent(MotionEvent event){
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        if(!gesture)     // =======================================================================================================================  //Режим рисования
        {
            //для multitioch
            int touch_counter = event.getPointerCount();
            touch_counter = touch_counter <= multitouch_max ? touch_counter : multitouch_max;
            //для оптимизации отрисовки
            int top=-1, left=-1, right=-1, bottom=-1;
            if(action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN){ //-----------------------------------------ACTION_DOWN
                int reason=(event.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;
                reason = event.getPointerId(reason);
                if(reason<multitouch_max)
                {
                    last_size=Settings.brush_size;
                    for(int i=0 ; i<touch_counter ; i++)
                    {
                        //запихнуть сюда координаты по-индексам
                        touch[i].set(event.getX(i), event.getY(i));
                        touch[i].helper=event.getPointerId(i);
                        touch_history[0][touch[i].helper].set(touch[i]);//set lasttouch from all fingers
                        for(int j=0 ; j<touch_history_size && touch[i].helper == reason ; j++)//set all history of reason finger
                            touch_history[j][reason].set(touch[i]);
                        //calculating area
                        top = top == -1 ? (int)touch[i].y - (int)Settings.brush_size - 10 :
                                touch[i].y - Settings.brush_size - 10 < top ? (int)touch[i].y - (int)Settings.brush_size - 10 :
                                        top;
                        bottom = bottom == -1 ? (int)touch[i].y + (int)Settings.brush_size + 10 :
                                touch[i].y + Settings.brush_size + 10 > bottom ? (int)touch[i].y + (int)Settings.brush_size + 10 :
                                        bottom;
                        left = left == -1 ? (int)touch[i].x - (int)Settings.brush_size - 10 :
                                touch[i].x - Settings.brush_size - 10 < left ? (int)touch[i].x - (int)Settings.brush_size - 10 :
                                        left;
                        right = right == -1 ? (int)touch[i].x + (int)Settings.brush_size + 10 :
                                touch[i].x + Settings.brush_size + 10 > right ? (int)touch[i].x + (int)Settings.brush_size + 10 :
                                        right;
                    }
                    if(eraser)   //стирать
                            line(touch[reason], touch[reason], (float)Settings.eraser_size, Settings.background_color);
                    else   //рисовать
                            line(touch[reason], touch[reason], (float)Settings.brush_size, Settings.brush_color);
                }
            }
            else if(action == MotionEvent.ACTION_MOVE){ //-------------------------------------------------------------------------------ACTION_MOVE
                //запихнуть сюда координаты по-индексам
                for(int i = 0 ; i < touch_counter ; i++){
                    touch[i].set(event.getX(i), event.getY(i));
                    touch[i].helper=event.getPointerId(i);

                    //Вычисчение области перерисовки (для оптимизации)
                    top = top == -1 ? (int)touch[i].y - (eraser ? (int)Settings.eraser_size : (int)Settings.brush_size) - 10 :
                            touch[i].y - Settings.brush_size - 10 < top ? (int)touch[i].y - (eraser ? (int)Settings.eraser_size : (int)Settings.brush_size) - 10 :
                                    top;
                    top = touch_history[0][touch[i].helper].y - Settings.brush_size - 10 < top ? (int)touch_history[0][touch[i].helper].y - (eraser ? (int)Settings.eraser_size : (int)Settings.brush_size) - 10 :
                                    top;
                    bottom = bottom == -1 ? (int)touch[i].y + (eraser ? (int)Settings.eraser_size : (int)Settings.brush_size) + 10 :
                            touch[i].y + Settings.brush_size + 10 > bottom ? (int)touch[i].y + (eraser ? (int)Settings.eraser_size : (int)Settings.brush_size) + 10 :
                                    bottom;
                    bottom = touch_history[0][touch[i].helper].y + Settings.brush_size + 10 > bottom ? (int)touch_history[0][touch[i].helper].y + (eraser ? (int)Settings.eraser_size : (int)Settings.brush_size) + 10 :
                            bottom;

                    left = left == -1 ? (int)touch[i].x - (eraser ? (int)Settings.eraser_size : (int)Settings.brush_size) - 10 :
                            touch[i].x - Settings.brush_size - 10 < left ? (int)touch[i].x - (eraser ? (int)Settings.eraser_size : (int)Settings.brush_size) - 10 :
                                    left;
                    left = touch_history[0][touch[i].helper].x - Settings.brush_size - 10 < left ? (int)touch_history[0][touch[i].helper].x - (eraser ? (int)Settings.eraser_size : (int)Settings.brush_size) - 10 :
                                    left;
                    right = right == -1 ? (int)touch[i].x + (eraser ? (int)Settings.eraser_size : (int)Settings.brush_size) + 10 :
                            touch[i].x + Settings.brush_size + 10 > right ? (int)touch[i].x + (eraser ? (int)Settings.eraser_size : (int)Settings.brush_size) + 10 :
                                    right;
                    right =  touch_history[0][touch[i].helper].x + Settings.brush_size + 10 > right ? (int)touch_history[0][touch[i].helper].x + (eraser ? (int)Settings.eraser_size : (int)Settings.brush_size) + 10 :
                            right;
                }
                //применить к каждому пальцy по-очереди
                for(int i = 0 ; i < touch_counter ; i++)
                {
                    //отрисовка
                    if(eraser)//стирать
                        line(touch_history[0][touch[i].helper], touch[i], (float)Settings.eraser_size, Settings.background_color);
                    else       //рисовать
                    {
                        //расчет толщины
                        double actual_brush_size=0;   //1=size, 2=speed, 3=none
                        if(Settings.manage_method == 3)//constant
                            actual_brush_size = Settings.brush_size;
                        else if(Settings.manage_method == 2){//speed
                            double dx= touch[i].x-touch_history[0][touch[i].helper].x;
                            double dy= touch[i].y-touch_history[0][touch[i].helper].y;
                            double spd=Math.sqrt(dx*dx + dy*dy);
                            double decrement=Settings.brush_size*(0.7*spd/50);
                            double result = Settings.brush_size-decrement;
                            if(result<last_size && last_size>1)
                                actual_brush_size=last_size-0.3;
                            else if(result>last_size)
                                actual_brush_size=last_size+0.3;
                            else
                                actual_brush_size=last_size;
                            last_size=actual_brush_size;
                        }
                        else if(Settings.manage_method == 1){//pressure
                            actual_brush_size = 5*Settings.brush_size*event.getSize(i);
                        }
                        //сглаживание резких угллов
                        if(Settings.smoothing){
                            //------------------------метод сглаживания неточности срабатывания сенсора при помощи кривой Безье
                            // что мы имеем  touch[i]
                            //last    touch_history[0][touch[i].helper]
                            //prelast   touch_history[1][touch[i].helper]
                            point p1=new point(
                                    (float)(touch_history[0][touch[i].helper].x-0.25*(touch_history[1][touch[i].helper].x-touch_history[0][touch[i].helper].x)),
                                    (float)(touch_history[0][touch[i].helper].y-0.25*(touch_history[1][touch[i].helper].y-touch_history[0][touch[i].helper].y))
                            );
                            point p2 = new point(0,0);
                            p2.set(touch[i]);
                            point begin;
                            point end = touch_history[0][touch[i].helper];   //touch[i];
                            for(double t=0.1 ; t<1 ; t+=0.2){
                                point current=new point(
                                        (float)(Math.pow((1 - t), 3) * touch_history[0][touch[i].helper].x + 3 * Math.pow((1 - t), 2) * t * p1.x + 3 * (1 - t) * Math.pow(t, 2) * p2.x + Math.pow(t, 3) * touch[i].x),
                                        (float)(Math.pow((1 - t), 3) * touch_history[0][touch[i].helper].y + 3 * Math.pow((1 - t), 2) * t * p1.y + 3 * (1 - t) * Math.pow(t, 2) * p2.y + Math.pow(t, 3) * touch[i].y)
                                );
                                begin = end;
                                end = current;
                                if(t<=0.1)
                                    begin = touch_history[0][touch[i].helper];
                                else if(t>=0.8)
                                    end = touch[i];
                                line(begin, end, (float)(actual_brush_size), Settings.brush_color);
                            }
                        }
                        else{   //без сглаживания
                            line(touch_history[0][touch[i].helper], touch[i], (float)(actual_brush_size), Settings.brush_color);
                        }
                        if(Settings.debug)
                        {
                            paint.setColor(Settings.background_color);
                            mCanvas.drawRect(touch[i].x-20, touch[i].y-120, touch[i].x+20, touch[i].y-80, paint);
                            paint.setColor(Settings.brush_color);
                            mCanvas.drawText(String.valueOf(touch[i].helper), touch[i].x, touch[i].y-100, paint);
                        }
                    }
                }
                //запись истории со сдвигом
                point tmp[] = touch_history[touch_history_size -1];
                for(int j= touch_history_size -1 ; j>0 ; j--)
                    touch_history[j]=touch_history[j-1];
                touch_history[0]=tmp;
                for(int i=0;i<touch_counter ; i++) //set lasttouch from all fingers
                    touch_history[0][touch[i].helper].set(touch[i]);
            }
            else if(event.getAction() == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP){//-------------------------------------------------------------------------ACTION_UP
                int reason=(event.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;
                reason = event.getPointerId(reason);
                if(reason==0)
                    UndoProvider.backup();
            }
            //drawing
            if(Settings.debug)
            { //checking refresh area
                paint.setColor(Color.argb(10, 255, 255, 255));
                mCanvas.drawRect(left, top, right, bottom, paint);
                paint.setColor(Settings.brush_color);
            }
            invalidate(left, top, right, bottom);
        }
        else   //=======================================================================================================================  //Режим обработки жестов
        {
            if(action == MotionEvent.ACTION_DOWN)
                gesture_start.set(event.getX(), event.getY());
            if(action == MotionEvent.ACTION_UP)
            {
                //hide hint
                gesture_hint=false;
                invalidate();
                // Get instance of Vibrator from current Context
                Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                //calculate
                point gesture_end=new point(event.getX(), event.getY());
                float dx=gesture_end.x-gesture_start.x;
                float dy=gesture_end.y-gesture_start.y;
                if(dx>0 && Math.abs(dx)>Math.abs(dy))//свайп вправо
                {
                    v.vibrate(50);
                    Settings.___________________LOG("Шаг вперед", false);
                    UndoProvider.redo();
                }
                if(dx<0 && Math.abs(dx)>Math.abs(dy))//свайп влево
                {
                    v.vibrate(50);
                    Settings.___________________LOG("Шаг назад", false);
                    UndoProvider.undo();
                }
                if(dy>0 && Math.abs(dx)<Math.abs(dy))//свайп вниз
                {
                    v.vibrate(50);
                    Settings.___________________LOG("Очистить рисунок", false);
                    clear();
                }
            }
        }
        return true;
    }
    public boolean keyEvent(KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN)
        {
            if(event.getKeyCode()==KeyEvent.KEYCODE_VOLUME_UP){
                if(!eraser) setEraser(true);
                return true;
            }
            if(event.getKeyCode()==KeyEvent.KEYCODE_VOLUME_DOWN){
                if(!gesture) setGesture(true);
                return true;
            }
            if(event.getKeyCode()==KeyEvent.KEYCODE_BACK){
                return true;
            }
        }
        if(event.getAction() == KeyEvent.ACTION_UP)
        {
            if(event.getKeyCode()==KeyEvent.KEYCODE_VOLUME_UP){
                setEraser(false);
                return true;}
            if(event.getKeyCode()==KeyEvent.KEYCODE_VOLUME_DOWN){
                setGesture(false);
//                long now=System.currentTimeMillis();
//                long difference=now-btndown_last_time;
//                if(difference < press_speed)
//                    btndown_times++;
//                else
//                    btndown_times=1;
//                btndown_last_time=System.currentTimeMillis();
//                Settings.___________________LOG("dif=" + String.valueOf(difference) + "ms., press=" + String.valueOf(btndown_times), false);
//                if(btndown_times >= 2)
//                    clear();
//                else
//                if( Settings.isTutor("clear", 5) )
//                    Toast.makeText(context, "Нажмите дважды для очистки", Toast.LENGTH_SHORT).show();
                return true;
            }
            if(event.getKeyCode()==KeyEvent.KEYCODE_BACK){
                long now=System.currentTimeMillis();
                long difference=now-btndown_last_time;
                if(difference < press_speed)
                    btndown_times++;
                else
                    btndown_times=1;
                btndown_last_time=System.currentTimeMillis();
                Settings.___________________LOG("dif=" + String.valueOf(difference) + "ms., press=" + String.valueOf(btndown_times), false);
                if(btndown_times >= 2)
                    ((sDraw)context).finish();
                else
                if( Settings.isTutor("exit", 5) )
                    Toast.makeText(context, "Нажмите дважды для выхода", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    } //обработка клавиш аппаратных
    public boolean add_to_gallery(String filename){
        Settings.___________________LOG("Регистрация файла в галерее: " + filename, false);
        //новый
        ContentValues v = new ContentValues();
        v.put(MediaStore.Images.Media.TITLE, "sDraw image");
        v.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis()/1000);
        v.put(MediaStore.Images.Media.DATE_MODIFIED, System.currentTimeMillis()/1000);
        v.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        v.put(MediaStore.Images.Media.DATA, filename);
        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, v);
        if(uri != null){
            //сообщаем другим, что добавили
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            Settings.___________________LOG("Регистрация прошла успешно", false);
            return true;
        }
        else
        {
            Toast.makeText(context,"Не удалось зарегистрировать файл в галерее. Он появится там позже.",Toast.LENGTH_SHORT).show();
            Settings.___________________LOG("В процессе регистрации возникли ошибки", false);
            return false;
        }
    } //регистрация файла в галерее. Вызывается при сохранении файла
    public Bitmap decodeFile(String path, int w, int h) {
        point required=new point(w,h);
        Log.d(Settings.TAG, "Decoding file: "+path+" as "+required.toString());
        //Settings.___________________LOG("Decoding file: "+path+" as "+required.toString(), false);
        // First decode with inJustDecodeBounds=true to check dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        // Calculate inSampleSize
        final int height = options.outHeight;
        final int width = options.outWidth;
        if (height > required.y || width > required.x) {
            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / required.y);
            final int widthRatio = Math.round((float) width /  required.x);
            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image with both dimensions larger than or equal to the requested height and width.
            options.inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }   //декодирование файла с оптимизацией до определенного размера
}
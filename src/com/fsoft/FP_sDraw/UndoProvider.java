package com.fsoft.FP_sDraw;

import android.graphics.Bitmap;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: Dr. Failov
 * Date: 18.03.13
 * Time: 1:07
 */
//Singletone is here
public class UndoProvider {
    static Draw draw;
    static Bitmap[] image;
    static int[] top, left, right,  bottom;
    static int current = 0; //с какой точке отмены мы сейчас находимся
    public static void init(Draw d)
    {
        draw=d;
        image=new Bitmap[Settings.undo_size];
        top=new int[Settings.undo_size];
        left=new int[Settings.undo_size];
        right=new int[Settings.undo_size];
        bottom=new int[Settings.undo_size];
        Settings.___________________LOG("UndoProvider инициализирован", false);
    }
    public static void backup(/*int ntop, int nleft, int nright, int nbottom*/)
    {
        Settings.___________________LOG("Создание контрольной точки...", false);
        //current=0;
        try{
/*      //защита от неправильных координат
            //больше нуля
            if(ntop<0)ntop=0;
            if(nleft<0)nleft=0;
            if(nright<0)nright=0;
            if(nbottom<0)nbottom=0;
            //меньше размера экрана
            int maxx=draw.bitmap.getWidth();
            int maxy=draw.bitmap.getHeight();
            if(ntop>maxy)ntop=maxy-1;
            if(nleft>maxx)nleft=maxx-1;
            if(nright>maxx)nright=maxx-1;
            if(nbottom>maxy)nbottom=maxy-1;
            Settings.___________________LOG("Координаты нормированы");*/
        //сдвиг массива
              if(current<=0)
              {
                    Bitmap tmp=image[Settings.undo_size-1];
                    for(int i=Settings.undo_size-1; i>0; i--)
                    {
                        image[i]=image[i-1];
                        left[i]=left[i-1];
                        top[i]=top[i-1];
                        right[i]=right[i-1];
                        bottom[i]=bottom[i-1];
                    }
                    image[0]=tmp;
               }
               else
                    current--;
               Settings.___________________LOG("Массив сдвинут", false);
        //запись
               image[current]=Bitmap.createBitmap(draw.bitmap);//Bitmap.createBitmap(copy, nleft, ntop, nright - nleft, nbottom - ntop);
    /*         left[0]=nleft;
               top[0]=ntop;
               right[0]=nright;
               bottom[0]=nbottom;*/
               Settings.___________________LOG("Bitmap вырезан, данные записаны в ячейку "+String.valueOf(current), false);
        }catch (Exception e){Settings.___________________LOG("Где-то произошла ошибка" + e.toString(), true);}
    }
    public static void undo()
    {
        if(current<Settings.undo_size-1 && image[current+1]!=null)
        {
            current++;
            Settings.___________________LOG("Отмена до ячейки "+String.valueOf(current), false);
            draw.mCanvas.drawBitmap(image[current],/*left[current],top[current]*/0,0,draw.paint);
            draw.invalidate();
        }
        else
        {
            Settings.___________________LOG("Уже некуда отменять", true);
        }
    }
    public static void redo()
    {
        if(current>0)
        {
            current--;
            Settings.___________________LOG("Повтор ячейки "+String.valueOf(current), false);
            draw.mCanvas.drawBitmap(image[current],/*left[current],top[current]*/0,0,draw.paint);
            draw.invalidate();
        }
        else
        {
            Toast.makeText(Settings.context, "Уже некуда повторять", Toast.LENGTH_SHORT).show();
        }
    }
}

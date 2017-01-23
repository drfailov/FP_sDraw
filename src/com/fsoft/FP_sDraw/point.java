package com.fsoft.FP_sDraw;

/**
 * Created with IntelliJ IDEA.
 * User: Dr. Failov
 * Date: 20.01.13
 * Time: 23:26
 * To change this template use File | SettingsScreen | File Templates.
 */
public class point{
    public float x=-1;
    public float y=-1;
    public int helper=0;
    point(float nx, float ny)
    {
        x=nx;
        y=ny;
    }
    public void set(float nx, float ny)
    {
        x=nx;
        y=ny;
    }
    public void set(point np)
    {
        x=np.x;
        y=np.y;
        helper=np.helper;
    }
    public @Override String toString()
    {
        String result="(";
        result+=String.valueOf(x);
        result+=", ";
        result+=String.valueOf(y);
        result+=")";
        return result;
    }
}

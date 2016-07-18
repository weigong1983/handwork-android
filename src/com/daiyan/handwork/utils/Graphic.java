package com.daiyan.handwork.utils;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
public class Graphic{

	
	/**
     * 图片任意形状的放大缩小
     */
    static public Bitmap ZoomToFixShape(Bitmap pic1,int w,int h) {
        Bitmap tempBitmap = null;
    	int bitH = pic1.getHeight();
    	int bitW = pic1.getWidth();	
        Matrix mMatrix = new Matrix();
        
        
        float scoleW = (float)w/(float)bitW;
        float scoleH = (float)h/(float)bitH;    
        
    	mMatrix.reset();
    	mMatrix.postScale(scoleW, scoleH);
    	tempBitmap = Bitmap.createBitmap(pic1,0,0,bitW,bitH,mMatrix,true);
    	return tempBitmap;
    	
    }
	
    
    /**
     * 图片任意形状的放大缩小,从资源ID读取图片
     */
    static public Bitmap ZoomToFixShape(Context context, int resID,int w,int h) {
    	
		Bitmap pic1 = BitmapFactory.decodeResource(context.getResources(), resID);
        Bitmap tempBitmap = null;
    	int bitH = pic1.getHeight();
    	int bitW = pic1.getWidth();	
        Matrix mMatrix = new Matrix();
        
        
        float scoleW = (float)w/(float)bitW;
        float scoleH = (float)h/(float)bitH;    
        
    	mMatrix.reset();
    	mMatrix.postScale(scoleW, scoleH);
    	tempBitmap = Bitmap.createBitmap(pic1,0,0,bitW,bitH,mMatrix,true);
    	return tempBitmap;
    	
    }
    
	
	/**
	 * 压缩成指定大小的JPG文件
	 * @param bitmap
	 * @return
	 */
	
	public static byte[] ConvertBitmapToByteArray(Bitmap bitmap, int width, int height)
	{
		if (bitmap == null)
		{
			return null;
		}
		
		// 压缩成指定大小的JPG文件
		Bitmap avatarBitmap = ZoomToFixShape(bitmap, width, height);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		avatarBitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);		
		return baos.toByteArray();
	}
	
	
	/**
	 * 压缩成指定大小的JPG文件
	 * @param bitmap
	 * @return
	 */
	
	public static byte[] ConvertBitmapToByteArray(Bitmap bitmap)
	{
		if (bitmap == null)
			return null;
		
		// 压缩成指定大小的JPG文件
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);		
		return baos.toByteArray();
	}
	
	/**
	 * 图片圆角处理
	 * @param bitmap
	 * @param roundPx
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {  
		
		if (bitmap == null)
			return null;
		
	    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);     
	    Canvas canvas = new Canvas(output);     
	      
	    final int color = 0xff424242; 
	    final Paint paint = new Paint();     
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());     
	    final RectF rectF = new RectF(rect);     
	    //final float roundPx = 12;     
	      
	    paint.setAntiAlias(true);     
	    canvas.drawARGB(0, 0, 0, 0);     
	    paint.setColor(color);     
	    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);     
	      
	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));     
	    canvas.drawBitmap(bitmap, rect, rect, paint);     
	   // bitmap.recycle();
	    return output;     
	  }
	
}
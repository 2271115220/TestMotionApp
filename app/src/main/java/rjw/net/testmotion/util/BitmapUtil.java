package rjw.net.testmotion.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BitmapUtil {

    /*λͼ���ֽ�������໥ת�������ڴ洢�ڶ�ȡ*/
    public static Bitmap byteArrayToBitmap(byte[] array) {
        if (null == array) {
            return null;
        }

        return BitmapFactory.decodeByteArray(array, 0, array.length);
    }

    public static byte[] bitampToByteArray(Bitmap bitmap) {
        byte[] array = null;
        try {
            if (null != bitmap) {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                array = os.toByteArray();
                os.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return array;
    }

    /*��ͼƬ���浽SDCard*/
    public static void saveBitmapToSDCard(Bitmap bmp, String strPath) {
        if (null != bmp && null != strPath && !strPath.equalsIgnoreCase("")) {
            try {
                File file = new File(strPath);
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = BitmapUtil.bitampToByteArray(bmp);
                fos.write(buffer);
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*��sdcard��ȡͼƬ*/
    public static Bitmap loadBitmapFromSDCard(String strPath) {
        File file = new File(strPath);

        try {
            FileInputStream fis = new FileInputStream(file);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = 2;   //width��hight��Ϊԭ���Ķ���һ
            Bitmap btp = BitmapFactory.decodeStream(fis, null, options);
            return btp;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap returnBitMap(final String url) {
        Bitmap bitmap = null;
        URL imageurl = null;
        try {
            imageurl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageurl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

}

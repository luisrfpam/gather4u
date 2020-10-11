package org.ufam.gather4u.utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

import id.zelory.compressor.Compressor;
import id.zelory.compressor.FileUtil;

public class BitmapHandle {
    private static final String TAG = BitmapHandle.class.getName();

    public static final int QUALITY_IMAGE_CROP = 100;

    public Bitmap resizeSquare(String path){
       Log.i(TAG,"getbitpam(String path)");
        Bitmap imgthumBitmap=null;
        int DEFAULT_SIZE_W=256;
        int DEFAULT_SIZE_H=256;
        try
        {
            FileInputStream fis = new FileInputStream(path);
            imgthumBitmap = BitmapFactory.decodeStream(fis);

            if(imgthumBitmap.getWidth()>DEFAULT_SIZE_W && imgthumBitmap.getHeight()>DEFAULT_SIZE_H){
                imgthumBitmap = Bitmap.createScaledBitmap(imgthumBitmap, DEFAULT_SIZE_W, DEFAULT_SIZE_H, false);
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        return imgthumBitmap;
    }

    public Bitmap resizeWide(String path){
       Log.i(TAG,"getbitpam(String path)");
        Bitmap imgthumBitmap=null;
        int DEFAULT_SIZE_W=720;
        int DEFAULT_SIZE_H=380;
        try
        {
            FileInputStream fis = new FileInputStream(path);
            imgthumBitmap = BitmapFactory.decodeStream(fis);

            if(imgthumBitmap.getWidth()>DEFAULT_SIZE_W && imgthumBitmap.getHeight()>DEFAULT_SIZE_H){
                imgthumBitmap = Bitmap.createScaledBitmap(imgthumBitmap, DEFAULT_SIZE_W, DEFAULT_SIZE_H, false);
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        return imgthumBitmap;
    }

    public Bitmap resize(String path) throws Exception{
        return resize(path, -1, -1);
    }

    public Bitmap resize(String path, int w, int h) throws Exception{
       Log.i(TAG,"resize(String path)");
        Bitmap imgthumBitmap=null;

        FileInputStream fis = new FileInputStream(path);
        imgthumBitmap = BitmapFactory.decodeStream(fis);

        int file_w = imgthumBitmap.getWidth();
        int file_h = imgthumBitmap.getHeight();

        Dimension fileImage = new Dimension(file_w,file_h);

        Dimension limit = new Dimension(1280,1280);
        if(w != -1 && h != -1){
            limit = new Dimension(w,h);
        }

        Dimension result = getScaledDimension(fileImage,limit);
        imgthumBitmap = Bitmap.createScaledBitmap(imgthumBitmap, result.getWidth(), result.getHeight(), false);

        return imgthumBitmap;
    }

    public static Bitmap getBitmapFromIFile(Uri uri) throws Exception {
        File f = new File(uri.toString());
        Log.e(TAG,"FILE: "+f.toString());
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),bmOptions);
        bitmap = Bitmap.createBitmap (bitmap);
        return bitmap;
    }

    public String compressFile(Bitmap bmp){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, QUALITY_IMAGE_CROP, stream);
        byte[] byte_arr = stream.toByteArray();
        return  Base64.encodeToString(byte_arr, Base64.DEFAULT);
    }

    public Bitmap resizeCustom(String path, int DEFAULT_SIZE_W, int DEFAULT_SIZE_H){
       Log.i(TAG,"resizeCustom(String path, int DEFAULT_SIZE_W, int DEFAULT_SIZE_H)");
        Bitmap imgthumBitmap=null;
        try
        {
            FileInputStream fis = new FileInputStream(path);
            imgthumBitmap = BitmapFactory.decodeStream(fis);

            if(imgthumBitmap.getWidth()>DEFAULT_SIZE_W && imgthumBitmap.getHeight()>DEFAULT_SIZE_H){
                imgthumBitmap = Bitmap.createScaledBitmap(imgthumBitmap, DEFAULT_SIZE_W, DEFAULT_SIZE_H, false);
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        return imgthumBitmap;
    }

    public String getBase64File(Uri uri) throws Exception{
        Bitmap myImg = new BitmapHandle().resize(uri.getPath());
        Log.w(TAG, "NEW SIZE: H - "+myImg.getHeight()+" | W - "+myImg.getWidth());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        myImg.compress(Bitmap.CompressFormat.JPEG, QUALITY_IMAGE_CROP, stream);
        byte[] byte_arr = stream.toByteArray();
        return  Base64.encodeToString(byte_arr, Base64.DEFAULT);
    }

     public Bitmap decodeFromBase64(String input) {
        try {

            ///byte[] raw = Base64.decode(input.substring(22), Base64.DEFAULT);
            ///Bitmap bitmap = BitmapFactory.decodeByteArray(raw, 0, raw.length);

            byte[] decodedByte = Base64.decode(input, Base64.DEFAULT );
            //byte[] decodedByte = Base64.decode(input.substring(22), Base64.DEFAULT );
            return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
        }
        catch (Exception ex)
        {
            Log.w(TAG, "decodeFromBase64: "+ ex.getMessage());
        }
        return null;
    }

    public File saveImage_(Bitmap finalBitmap, String path) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        finalBitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY_IMAGE_CROP, stream);

        String root = Environment.getExternalStorageDirectory().toString();
//        File myDir = new File(root + "/teewa_files/images_send/");
        File myDir = new File(root + path);
        myDir.mkdirs();

        String dateString = String.valueOf(new Date().getTime());
        String fname = dateString+".jpg";

        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public File saveLoginImage(Bitmap finalBitmap, String login, String path) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        finalBitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY_IMAGE_CROP, stream);

        String root = Environment.getExternalStorageDirectory().toString();
//        File myDir = new File(root + "/teewa_files/images_send/");

        String fname = login +".jpeg";

        File myDir = null;
        File file = null;
        if (path.contains(root)){
            myDir = new File(path);
            file = new File(myDir,"");
        }
        else
        {
            myDir = new File(root + path);
            file = new File(myDir, fname);
        }

        myDir.mkdirs();

        try { if (file.exists()) file.delete(); }
        catch (Exception ex) {
        }


        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }

    public String getAvatarPath( String login){

        String avatPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator + "gather4u" + File.separator +
                "profiles"+ File.separator + login + "." +
                Bitmap.CompressFormat.JPEG.name().toLowerCase();

        try {

            File f = new File(avatPath);
            if (f.exists()){
                f.createNewFile();
            }
        }
        catch (Exception ex){

        }
        return avatPath;
    }

    public File getAvatarFile(String login){
        File myImageFile = null;
        String path = getAvatarPath(login);

        if (path.trim().length() > 0 )
        {
            return new File(path);
        }
        return null;
    }

    public void loadImage(Activity context, ImageView iv, String login, String image){

        Bitmap bmp = decodeFromBase64(image);

        if (bmp != null){

            String path = getAvatarPath(login);
            bmp = getCircleBitmap(bmp);

            if (iv != null){
                if (bmp != null){
                    final File myImageFile = saveLoginImage(bmp, login, path);
                    Picasso.with(context)
                            .load(myImageFile)
                            .into(iv);
                }
            }
        }
    }

    public void loadImageFromDevice(Activity context, ImageView iv, String login){

        Bitmap bmp = null;
        String path = getAvatarPath(login);
        saveLoginImage( bmp, login, path );
        if (iv != null){
                if (bmp != null){
                    final File myImageFile = saveLoginImage(bmp, login, path);
                    Picasso.with(context)
                            .load(myImageFile)
                            .into(iv);
                }
            }
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {

        int original_width = imgSize.getWidth();
        int original_height = imgSize.getHeight();
        int bound_width = boundary.getWidth();
        int bound_height = boundary.getHeight();
        int new_width = original_width;
        int new_height = original_height;

        // first check if we need to scale width
        if (original_width > bound_width) {
            //scale width to fit
            new_width = bound_width;
            //scale height to maintain aspect ratio
            new_height = (new_width * original_height) / original_width;
        }

        // then check if we need to scale even with the new height
        if (new_height > bound_height) {
            //scale height to fit instead
            new_height = bound_height;
            //scale width to maintain aspect ratio
            new_width = (new_height * original_width) / original_height;
        }

        return new Dimension(new_width, new_height);
    }

    /**
     * Métodos responsável para rotacionar a imagem tirarda ou da galeria caso necessário (SAMSUNG)
     */
    public Bitmap handleSamplingAndRotationBitmap(Uri selectedItem) throws Exception {
      return this.handleSamplingAndRotationBitmap(selectedItem,-1,-1);
    }

    public Bitmap handleSamplingAndRotationBitmap(Uri selectedItem, int w, int h) throws Exception {
       Log.d(TAG, "handleSamplingAndRotationBitmap");
        Bitmap image = null;
        if(w != -1 && h != -1){
            image = resize(selectedItem.getPath(),w,h);
        }else{
            image = resize(selectedItem.getPath());
        }

        image = rotateImageIfRequired(image, selectedItem);
        return image;
    }

    /**
     * Usado para calcular uma inSampleSize para uso em uma {BitmapFactory.Options} objeto quando decodificação
     * bitmaps usando os métodos de decodificação de {@link BitmapFactory}. Esta implementação calcula
     * o inSampleSize mais próximo que irá resultar no mapa de bits descodificados final que tem uma largura
     * e uma altura igual a ou maior do que a largura e a altura requerida. Esta aplicação não garante uma
     * potência de 2 é devolvido para inSampleSize que pode ser mais rápida quando a descodificação, mas
     * resulta em um bitmap maior que não é tão útil para fins de cache.
     * @param options
     * @param requiredWidth
     * @param requiredHeight
     * @return
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int requiredWidth, int requiredHeight){
       Log.d(TAG, "calculateInSampleSize");
        final int height = options.outHeight;
        final int width  = options.outWidth;

        int inSampleSize = 1;

        if(height > requiredHeight || width > requiredHeight){
            /*
             Calcula as proporções de altura e largura para altura e largura solicitada
             */
            final int heightRadio = Math.round((float) height / (float) requiredHeight);
            final int widthRadio = Math.round((float) width / (float) requiredWidth);

            // Escolhe a menor proporção que o valor inSampleSize, isso vai garantir uma imagem final
            // Com as duas dimensões maiores ou iguais à altura e largura solicitado.

            inSampleSize = (heightRadio < widthRadio) ? heightRadio: widthRadio;

            /**
             * Isso oferece alguma lógica adicional no caso de a imagem tem uma relação de aspecto estranho.
             * Por exemplo, um panorama pode ter uma largura muito maior do que a altura. Nestes casos, o
             * total de pixels ainda pode acabar sendo muito grande para caber confortavelmente na memória,
             * de modo que devemos ser mais agressivos com a amostra para baixo a imagem (= maior inSampleSize).
             */

            final float totalPixels = width * height;
            final float totalRequiredPixelscap = requiredHeight * requiredWidth * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalRequiredPixelscap){
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    /**
     * Rotaciona a imagem caso necessário.
     * @param image
     * @param selectedImage
     * @return
     * @throws IOException
     */
    public static Bitmap rotateImageIfRequired(Bitmap image, Uri selectedImage) throws IOException {
       Log.d(TAG, "rotateImageIfRequired");
        ExifInterface exifInterface = new ExifInterface(selectedImage.getPath());
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation){
            case ExifInterface.ORIENTATION_ROTATE_90:
               Log.d(TAG, "Rodou 90");
                return rotateImage(image, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
               Log.d(TAG, "Rodou 180");
                return rotateImage(image, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
               Log.d(TAG, "Rodou 270");
                return rotateImage(image, 270);
            default:
               Log.d(TAG, "Não precisou rodar: "+orientation);
                    return image;
        }
    }

    /**
     * Método de rotação propriamente dito.
     * @param image
     * @param degree
     * @return
     */
    private static Bitmap rotateImage(Bitmap image, int degree){
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotateImage = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
//        image.recycle();
        return rotateImage;
    }

    public static String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

    public static void downloadBitmap(Context context, String url, final DownloadedBitmap dbmp){
        Picasso.with(context).load(url).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                dbmp.onSuccess(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                dbmp.onError(errorDrawable);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    public static void downloadBitmap(String url, final DownloadedBitmap dbmp){

        try {
            URL urlConn = new URL(url);
            Bitmap bitmap = BitmapFactory.decodeStream(urlConn.openConnection().getInputStream());
            dbmp.onSuccess(bitmap);
        } catch(IOException e) {
            e.printStackTrace();
            dbmp.onError(null);
        }
    }

    public interface DownloadedBitmap{
        void onSuccess(Bitmap bitmap);
        void onError(Drawable errorDrawable);
    }

    public static File compressImageToFile(Activity activity, Uri image) throws IOException{
        File actualImage = FileUtil.from(activity, image);

        return new Compressor.Builder(activity)
                .setMaxWidth(1920)
                .setMaxHeight(1920)
                .setQuality(100)
                .setCompressFormat(Bitmap.CompressFormat.WEBP)
                .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES).getAbsolutePath())
                .build()
                .compressToFile(actualImage);
    }

    public static Bitmap compressImageToBitmap(Activity activity, Uri image) throws IOException{
        File actualImage = FileUtil.from(activity, image);

        return new Compressor.Builder(activity)
                .setMaxWidth(1920)
                .setMaxHeight(1920)
                .setQuality(100)
                .setCompressFormat(Bitmap.CompressFormat.WEBP)
                .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES).getAbsolutePath())
                .build()
                .compressToBitmap(actualImage);
    }
}

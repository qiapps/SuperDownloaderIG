package com.qiapps.superdownloaderig.Helper;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import com.qiapps.superdownloaderig.Model.InstagramResource;
import com.qiapps.superdownloaderig.R;

public class FileManager {


    public static void openInstagramWithUrl(Context context, String url) {
        try {
            Uri uri = Uri.parse(url);

            if (hasInstagramInstalled(context)) {
                Intent insta = new Intent(Intent.ACTION_VIEW, uri);
                insta.setPackage("com.instagram.android"); // Garante que abre no app do Instagram
                context.startActivity(insta);
            } else {
                // Se o Instagram não estiver instalado, abre no navegador
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(browserIntent);
            }
        } catch (Exception e) {
            Log.e("InstagramError", "Erro ao abrir o link do Instagram: " + e.getMessage());
        }
    }

    /**
     * Verifica se o Instagram está instalado no dispositivo
     */
    private static boolean hasInstagramInstalled(Context context) {
        try {
            context.getPackageManager().getPackageInfo("com.instagram.android", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    public static void openInstagram(Activity activity){
        if(hasInstagramInstalled(activity)) {
            Intent launchIntent = activity.getPackageManager().getLaunchIntentForPackage("com.instagram.android");
            activity.startActivity(launchIntent);
        }else{

        }
    }

    public static void openMediaInGallery(Context context, String filePath) {
        try {
            if (filePath.contains(";")) {
                filePath = filePath.split(";")[0]; // Pegando apenas a primeira URI
            }

            Uri uri = Uri.parse(filePath);
            String mimeType = context.getContentResolver().getType(uri); // Obtendo o tipo MIME correto

            if (mimeType == null) {
                // Se não conseguir determinar, assume como imagem ou vídeo genérico
                mimeType = "image/*";
            }

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, mimeType);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e("OpenGalleryError", "Erro ao abrir mídia na galeria: " + e.getMessage());
            Toast.makeText(context, "Não foi possível abrir o arquivo", Toast.LENGTH_SHORT).show();
        }
    }



    public static void deleteFile(File f, Context context){
        try {
            f.delete();
        }catch (Exception e){

        }
    }

    public static Uri getUriByImagePath(Context context, String path){
        try{
            String[] ff = path.split("/");
            String name = ff[ff.length - 1];
            Uri ur = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            ContentResolver contentResolver = context.getContentResolver();
            String selection = MediaStore.MediaColumns.DISPLAY_NAME + " =? ";
            String[] args = new String[]{name};
            String order = MediaStore.MediaColumns.DISPLAY_NAME + " ASC";

            Cursor cursor = contentResolver.query(ur, null, selection, args, order);
            cursor.moveToFirst();

            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            long id = cursor.getLong(idColumn);

            Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            return contentUri;
        }catch (Exception e){
            return null;
        }

    }

    public static Uri getUriByVideoPath(Context context, String path) {
        if (path == null) return null; // Evita erro com path nulo

        if (path.startsWith("content://")) {
            // Se já for um content:// válido, apenas retorna
            return Uri.parse(path);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            // Para Android 9 ou inferior, retorna um Uri baseado no caminho absoluto
            File file = new File(path);
            return Uri.fromFile(file);
        }

        Uri contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = context.getContentResolver();

        try {
            String[] ff = path.split("/");
            String name = ff[ff.length - 1];

            // Definição da projeção para selecionar apenas a coluna necessária
            String[] projection = {MediaStore.Video.Media._ID};
            String selection = MediaStore.MediaColumns.DISPLAY_NAME + " = ?";
            String[] selectionArgs = new String[]{name};

            try (Cursor cursor = contentResolver.query(contentUri, projection, selection, selectionArgs, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
                    long id = cursor.getLong(idColumn);
                    return ContentUris.withAppendedId(contentUri, id);
                }
            }
        } catch (Exception e) {
            Log.e("MediaStoreError", "Erro ao buscar URI do vídeo: " + e.getMessage());
        }

        return null; // Retorna null se o vídeo não for encontrado
    }



    public static Bitmap getBitmapFromFilePath(Context context,String filePath){
        Uri uri = FileManager.getUriByImagePath(context,filePath);
        if(uri != null){
            Bitmap bt = null;
            try {
                bt = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bt;
        }else{
            return null;
        }

    }

    public static void shareImg(Context context, String path){


        Intent share = new Intent(Intent.ACTION_SEND);
        ArrayList<Uri> uris = new ArrayList<>();
        for (String p:path.split(";")) {
            Uri contentUri = getUriByImagePath(context,p);
            if(contentUri != null){
                uris.add(contentUri);
            }
        }

        // Setamos a Uri da imagem
        if(uris.size() > 0){
            share.setType("image/*");
            share.setAction(Intent.ACTION_SEND_MULTIPLE);
            share.putExtra(Intent.EXTRA_STREAM, uris);
            // chama o compartilhamento
            context.startActivity(Intent.createChooser(share, "Selecione"));
        }else{
            Toast.makeText(context, R.string.erro_compartilhar, Toast.LENGTH_SHORT).show();
        }
    }

    public static void shareVideo(Context context, String path){

        Intent share = new Intent(Intent.ACTION_SEND);
        //Uri imageUri =  Uri.parse(path);
        // Setamos a Uri da imagem
        ArrayList<Uri> uris = new ArrayList<>();
        for (String p:path.split(";")) {

            Uri contentUri = getUriByVideoPath(context,p);
            if(contentUri != null){
                uris.add(contentUri);
            }
        }
        if(uris.size() > 0){
            share.setType("video/mp4");
            share.setAction(Intent.ACTION_SEND_MULTIPLE);
            share.putExtra(Intent.EXTRA_STREAM, uris);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            // chama o compartilhamento
            context.startActivity(Intent.createChooser(share, "Selecione"));
        }else{
            Toast.makeText(context, R.string.erro_compartilhar, Toast.LENGTH_SHORT).show();
        }

    }

    public static void repostInstagram(Context context, String path){
        //if(hasInstagramInstalled(context)) {
            if(isUrlVideo(path.split(";")[0])){

                Intent share = new Intent(Intent.ACTION_SEND);
                //Uri imageUri =  Uri.parse(path);
                // Setamos a Uri da imagem
                ArrayList<Uri> uris = new ArrayList<>();
                for (String p:path.split(";")) {

                    Uri contentUri = getUriByVideoPath(context,p);
                    if(contentUri != null){
                        uris.add(contentUri);
                    }
                }
                if(uris.size() > 0){
                    share.setType("video/mp4");
                    share.setAction(Intent.ACTION_SEND_MULTIPLE);
                    share.putExtra(Intent.EXTRA_STREAM, uris);
                    share.setPackage("com.instagram.android");
                    // chama o compartilhamento
                    context.startActivity(Intent.createChooser(share, "Selecione"));
                }else{
                    Toast.makeText(context, R.string.erro_compartilhar, Toast.LENGTH_SHORT).show();
                }

            }else{

                Intent share = new Intent(Intent.ACTION_SEND);
                ArrayList<Uri> uris = new ArrayList<>();
                for (String p:path.split(";")) {
                    Uri contentUri = getUriByImagePath(context,p);
                    if(contentUri != null){
                        uris.add(contentUri);
                    }
                }

                // Setamos a Uri da imagem
                if(uris.size() > 0){
                    share.setType("image/*");
                    share.setAction(Intent.ACTION_SEND_MULTIPLE);
                    share.putExtra(Intent.EXTRA_STREAM, uris);
                    share.setPackage("com.instagram.android");
                    // chama o compartilhamento
                    context.startActivity(Intent.createChooser(share, "Selecione"));
                }else{
                    Toast.makeText(context, R.string.erro_compartilhar, Toast.LENGTH_SHORT).show();
                }

            }
//        }else{
//            Log.d("shareVideo","instagram nao inatalado");
//            Toast.makeText(context, R.string.erro_compartilhar, Toast.LENGTH_SHORT).show();
//        }
    }

    public static String getImagePath(Context context) {
        long date = Calendar.getInstance().getTimeInMillis();
        String name = "image" + date + ".jpg";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Android 10+ (Scoped Storage)
            return getFilePathUsingMediaStore(context, name, "image/jpeg", Environment.DIRECTORY_PICTURES);
        } else { // Android 9 e abaixo
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath());
            if (!dir.exists()) dir.mkdirs();
            return dir.getAbsolutePath() + File.separator + name;
        }
    }

    public static String getVideoPath(Context context) {
        long date = Calendar.getInstance().getTimeInMillis();
        String name = "video" + date + ".mp4";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Android 10+ (Scoped Storage)
            return getFilePathUsingMediaStore(context, name, "video/mp4", Environment.DIRECTORY_MOVIES);
        } else { // Android 9 e abaixo
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath());
            if (!dir.exists()) dir.mkdirs();
            return dir.getAbsolutePath() + File.separator + name;
        }
    }

    /**
     * Método para criar um arquivo no Android 10+ via MediaStore (Scoped Storage)
     */
    private static String getFilePathUsingMediaStore(Context context, String fileName, String mimeType, String directory) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, directory + "/InstagramDownloads");

        Uri collection;
        if (mimeType.startsWith("image/")) {
            collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        }

        Uri fileUri = context.getContentResolver().insert(collection, values);
        return fileUri != null ? fileUri.toString() : null;
    }


    public static Bitmap getBitmapFromFile(File file){
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static boolean isValidUrl(String url){
        String u[] = url.split("'");
        if(u.length > 1) return false;
        if(url.equals("")){
            return false;
        }else if(url.contains("http")){
            return true;
        }else{
            return false;
        }
    }

    public static boolean isUrlVideo(String url){
        return !url.equals("") && (url.contains(".mp4") || url.contains("/video"));
    }

    public static boolean isFileExists(Context context, String filePath) {
        if (filePath.startsWith("content://")) {
            // Verifica se o arquivo existe usando ContentResolver
            return isUriExists(context, Uri.parse(filePath));
        } else {
            // Verifica para Android 9 ou inferior (caminho tradicional)
            File file = new File(filePath);
            return file.exists();
        }
    }

    /**
     * Verifica se uma URI existe no MediaStore (Android 10+)
     */
    private static boolean isUriExists(Context context, Uri uri) {
        try (Cursor cursor = context.getContentResolver().query(
                uri, new String[]{MediaStore.MediaColumns._ID}, null, null, null)) {
            return cursor != null && cursor.getCount() > 0;
        }
    }



    public static boolean isSideCar(InstagramResource instagramResource){
        return instagramResource.getVideo_url().split(";").length > 1;
    }
}

package com.qiapps.superdownloaderig.Helper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.system.ErrnoException;
import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import com.qiapps.superdownloaderig.Model.InstagramResource;

public class DownloadFileFromInstagramResource extends AsyncTask<Void, String, String> {

    InstagramResource instagramResource;
    DownloadFileUtils downloadFileUtils;
    private Context context;

    public DownloadFileFromInstagramResource(InstagramResource instagramResource,DownloadFileUtils downloadFileUtils,Context context){
        this.instagramResource = instagramResource;
        this.downloadFileUtils = downloadFileUtils;
        this.context = context;

    }
    /**
     * Before starting background thread Show Progress Bar Dialog
     * */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //showDialog(progress_bar_type);
    }

    /**
     * Downloading file in background thread
     * */
    @Override
    protected String doInBackground(Void... voids) {
        int count;
        try {
            String g = instagramResource.getVideo_url();
            String[] moreUrls = g.split(";");
            String[] morePaths = instagramResource.getFilepath().split(";");

            Log.d("DownloadFile","moreUrls: " + moreUrls.toString());
            Log.d("DownloadFile","morePaths: " + morePaths.toString());
            for (int i = 0; i < moreUrls.length; i++) {
                String u = moreUrls[i];
                Log.d("DownloadFile","video Url to Download: " + u);
                URL url = new URL(u);
                URLConnection connection = url.openConnection();
                connection.connect();

                int lengthOfFile = connection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                OutputStream output = null;
                Uri uri = null;

                try {
                    String path = morePaths[i];

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        ContentValues values = new ContentValues();
                        String[] ss = path.split("/");
                        String displayName = ss[ss.length - 1];
                        String mime = FileManager.isUrlVideo(u) ? "video/mp4" : "image/jpeg";
                        String directory = FileManager.isUrlVideo(u) ? Environment.DIRECTORY_MOVIES : Environment.DIRECTORY_PICTURES;

                        values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName);
                        values.put(MediaStore.MediaColumns.MIME_TYPE, mime);
                        values.put(MediaStore.MediaColumns.RELATIVE_PATH, directory + "/InstagramDownloads");


                        if(i==0){
                            //adiciona nome e descrição
                            instagramResource.setUsername(displayName);
                            instagramResource.setTitle(mime);
                        }
                        Log.d("DownloadFile","values: " + values.toString());



                        ContentResolver resolver = context.getContentResolver();
                        Uri contentUri = FileManager.isUrlVideo(u) ? MediaStore.Video.Media.EXTERNAL_CONTENT_URI : MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        Log.d("DownloadFile","contentUri: " + contentUri.toString());
                        uri = resolver.insert(contentUri, values);

                        if (uri == null) {
                            Log.e("DownloadError", "Falha ao criar URI no MediaStore");
                            continue; // Pula este arquivo e tenta o próximo
                        }

                        output = resolver.openOutputStream(uri);
                    } else {
                        File file = new File(path);
                        File parentDir = file.getParentFile();
                        if (parentDir != null && !parentDir.exists()) {
                            parentDir.mkdirs();
                        }
                        output = new FileOutputStream(file);

                    }

                    if(i==0 && MyDownloadManager.isStories(instagramResource.getInstagramPath())){
                        //adiciona nome e descrição
                        String mime = FileManager.isUrlVideo(u) ? "video/mp4" : "image/jpeg";
                        instagramResource.setUsername("Stories");
                        instagramResource.setTitle(mime);
                    }

                    if (output == null) {
                        Log.e("DownloadError", "Output stream nulo, pulando arquivo");
                        continue;
                    }

                    byte[] data = new byte[1024];
                    long total = 0;

                    while ((count = input.read(data)) != -1) {
                        total += count;
                        String progress = "" + (int) ((total * 100) / lengthOfFile);
                        String totalDownloads = ""+instagramResource.getVideo_url().split(";").length;
                        String currentDownload = "" + (i+1);
                        publishProgress(progress,totalDownloads,currentDownload);
                        output.write(data, 0, count);
                    }

                    // Garantindo a indexação na Galeria para Android 9 ou inferior
                    //if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    String mime = FileManager.isUrlVideo(u) ? "video/mp4" : "image/jpeg";
                        MediaScannerConnection.scanFile(context,
                                new String[]{morePaths[i]},
                                new String[]{mime},
                                (path1, uri1) -> Log.d("MediaScanner", "Arquivo indexado: " + path1));
                    //}

                } catch (IOException e) {
                    Log.e("DownloadError", "Erro ao salvar arquivo: " + e.getMessage());
                } finally {
                    if (output != null) {
                        try {
                            output.flush();
                            output.close();
                        } catch (IOException e) {
                            Log.e("StreamError", "Erro ao fechar OutputStream: " + e.getMessage());
                        }
                    }
                    if (input != null) {
                        try {
                            input.close();
                        } catch (IOException e) {
                            Log.e("StreamError", "Erro ao fechar InputStream: " + e.getMessage());
                        }
                    }
                }
            }
        } catch (FileNotFoundException er) {
            Log.e("DownloadError", "Arquivo não encontrado: " + er.toString());
            downloadFileUtils.onError("FileNotFoundException");
        } catch (Exception e) {
            Log.e("DownloadError", "Erro geral: " + e.toString());
            downloadFileUtils.onError("Exception");
        }

        return null;
    }


    /**
     * Updating progress bar
     * */
    protected void onProgressUpdate(String... progress) {
        // setting progress percentage
        int i = Integer.parseInt(progress[0]);
        int t = Integer.parseInt(progress[1]);
        int c = Integer.parseInt(progress[2]);
        downloadFileUtils.onProgress(i,t,c);
    }

    /**
     * After completing background task Dismiss the progress dialog
     * **/
    @Override
    protected void onPostExecute(String file_url) {
        // dismiss the dialog after the file was downloaded
        downloadFileUtils.onFinish(instagramResource);

    }

    public interface DownloadFileUtils{
        void onProgress(int progress, int totalDownloads, int currentDownload);
        void onFinish(InstagramResource instagramResource);
        void onError(String error);
        void onPermissionDenied(String url);
    }
}

package com.qiapps.superdownloaderig.Helper;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.system.ErrnoException;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;

import com.qiapps.superdownloaderig.Model.InstagramResource;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyDownloadManager {


    private String link;
    private DownloadUtils downloadUtils;
    public static String error_url = "";
    public static Exception exception = null;
    private static final String API_URL = "https://instagram-downloader-scraper-reels-igtv-posts-stories.p.rapidapi.com/scraper?url=";
    private static final String RAPIDAPI_KEY = "c4c5bd7005msh026389a05f48015p1c7b67jsnc931a24f798a"; // 🔹 Insira su
    private Context context;

    public MyDownloadManager(String link,DownloadUtils downloadUtils,Context context) {
        this.link = link;
        this.downloadUtils = downloadUtils;
        this.context = context;
    }

    public void init(){
        new InstagramApiTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    public class InstagramApiTask extends AsyncTask<Void, InstagramResource, InstagramResource> {
        @Override
        protected InstagramResource doInBackground(Void... voids) {
            //String s = convertStringUrlInstagran(link);
            if(isStories(link)){
                return getInstagramResource(link);
            }else{
                return getResourceByAutoDownloadAllinOneAPI(link);
            }

        }

        @Override
        protected void onPostExecute(InstagramResource instagramResource) {
            super.onPostExecute(instagramResource);
            if(instagramResource == null){
                //Log.d("videoDownload","onPostExecute");
                downloadUtils.onError(false);
            }else{
                downloadUtils.onInstagramResourceBuild(instagramResource);
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            downloadUtils.onStartDownload();
        }

    }


    public static boolean isStories(String link){
        return link.contains("instagram.com/stories/") || link.contains("instagram.com/s/");
    }


    public InstagramResource getInstagramResource(String postUrl){

        InstagramResource instagramResource = new InstagramResource();
        try {

        Log.d("InstagramApiTask", "posturl: " + postUrl);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(API_URL + postUrl)
                .get()
                .addHeader("x-rapidapi-key", RAPIDAPI_KEY)
                .addHeader("x-rapidapi-host", "instagram-downloader-scraper-reels-igtv-posts-stories.p.rapidapi.com")
                .build();


            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) return null;

            String responseBody = response.body().string();
            Log.d("InstagramApi", "response: " + responseBody);
            JSONObject jsonResponse = new JSONObject(responseBody);
            JSONArray dataArray = jsonResponse.optJSONArray("data");

            instagramResource.setTitle("");
            instagramResource.setUsername(postUrl);
            instagramResource.setDuration(0);
            instagramResource.setInstagramPath(postUrl);

            if (dataArray != null) {
                if(dataArray.length()==1){
                    //post unico ou video
                    JSONObject mediaObject = dataArray.getJSONObject(0);
                    String downloadUrl = mediaObject.optString("media");
                    String thumb = mediaObject.optString("thumb");
                    int isVideo = mediaObject.optBoolean("isVideo") ? 1 : 0;

                    instagramResource.setUrl(thumb);
                    instagramResource.setVideo_url(downloadUrl);
                    instagramResource.setIsVideo(isVideo);

                    String filePath = "";
                    if(isVideo == 1){
                        filePath = FileManager.getVideoPath(context);
                    }else{
                        filePath = FileManager.getImagePath(context);
                    }

                    instagramResource.setFilepath(filePath);

                }else{
                    //carrossel ou conjunto de stories
                    instagramResource.setIsVideo(0);//não é video
                    StringBuilder filePaths = new StringBuilder();
                    StringBuilder downloadUrls = new StringBuilder();

                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject mediaObject = dataArray.getJSONObject(i);

                        if(i==0){//pega a thumb do primeiro video ou imagem
                            String thumb = mediaObject.optString("thumb");
                            instagramResource.setUrl(thumb);
                        }

                        String downloadUrl = mediaObject.optString("media");
                        int isVideo = mediaObject.optBoolean("isVideo") ? 1 : 0;

                        downloadUrls.append(downloadUrl).append(";");

                        String filePath = "";
                        if(isVideo == 1){
                            filePath = FileManager.getVideoPath(context);
                        }else{
                            filePath = FileManager.getImagePath(context);
                        }

                        filePaths.append(filePath).append(";");

                    }

                    instagramResource.setVideo_url(downloadUrls.toString());
                    instagramResource.setFilepath(filePaths.toString());

                }

            }

            //return jsonResponse.optString("video_url", null); // 🔹 Captura a URL do vídeo
        } catch (IOException e) {
            Log.e("InstagramApi", "Erro ao obter URL da mídia: " + e.getMessage());
            downloadUtils.onError(true);
            return null;
        } catch (JSONException e) {
            downloadUtils.onError(true);
            Log.e("InstagramApi", "Erro ao obter URL da mídia: " + e.getMessage());
            return null;
        }


        return instagramResource;

    }

    public InstagramResource getResourceByAutoDownloadAllinOneAPI(String postUrl) {
        InstagramResource instagramResource = null;
        Log.d("getResourceByAutoDownloadAllinOneAPI", "posturl: " + postUrl);

        try {
        OkHttpClient client = new OkHttpClient();

        // 🔹 Criar o JSON para o corpo da requisição
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"url\":\"" + postUrl + "\"}");

        // 🔹 Criar a requisição HTTP
        Request request = new Request.Builder()
                .url("https://auto-download-all-in-one.p.rapidapi.com/v1/social/autolink")
                .post(body)
                .addHeader("x-rapidapi-key", RAPIDAPI_KEY)
                .addHeader("x-rapidapi-host", "auto-download-all-in-one.p.rapidapi.com")
                .addHeader("Content-Type", "application/json")
                .build();


            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) return null;
            String responseBody = response.body().string();
            JSONObject jsonResponse = new JSONObject(responseBody);
            instagramResource = new InstagramResource();
            // 🔹 Preencher os dados básicos
            instagramResource.setTitle(jsonResponse.optString("title", ""));
            instagramResource.setUsername(jsonResponse.optString("author", ""));
            int duration = (int) jsonResponse.optDouble("duration", 0);
            instagramResource.setDuration(duration);
            instagramResource.setInstagramPath(postUrl);
            instagramResource.setUrl(jsonResponse.optString("thumbnail", ""));

            // 🔹 Processar os "medias"
            JSONArray mediasArray = jsonResponse.optJSONArray("medias");
            String source = jsonResponse.optString("source");
            Log.d("getResourceByAutoDownloadAllinOneAPI", "source: " + source);
            if (mediasArray != null) {
                if (mediasArray.length() == 1 || source.equals("tiktok") || source.equals("facebook")) {//se tiver apenas uma midia ou se for do tiktok
                    // 🔹 Post único (imagem ou vídeo)
                    Log.d("getResourceByAutoDownloadAllinOneAPI", "imagem ou video unico");
                    JSONObject mediaObject = mediasArray.getJSONObject(0);
                    String downloadUrl = mediaObject.optString("url");
                    String type = mediaObject.optString("type");
                    boolean isVideo = type.equalsIgnoreCase("video");

                    instagramResource.setVideo_url(downloadUrl);
                    instagramResource.setIsVideo(isVideo ? 1 : 0);

                    String filePath = isVideo ? FileManager.getVideoPath(context) : FileManager.getImagePath(context);
                    instagramResource.setFilepath(filePath);

                } else {
                    // 🔹 Carrossel ou conjunto de stories
                    instagramResource.setIsVideo(0); // Não é um único vídeo
                    StringBuilder filePaths = new StringBuilder();
                    StringBuilder downloadUrls = new StringBuilder();

                    for (int i = 0; i < mediasArray.length(); i++) {
                        JSONObject mediaObject = mediasArray.getJSONObject(i);
                        String downloadUrl = mediaObject.optString("url");
                        String type = mediaObject.optString("type");
                        boolean isVideo = type.equalsIgnoreCase("video");

                        downloadUrls.append(downloadUrl).append(";");

                        String filePath = isVideo ? FileManager.getVideoPath(context) : FileManager.getImagePath(context);
                        filePaths.append(filePath).append(";");
                    }

                    instagramResource.setVideo_url(downloadUrls.toString());
                    instagramResource.setFilepath(filePaths.toString());
                }
            }

        } catch (IOException e) {
            Log.e("getResourceByAutoDownloadAllinOneAPI", "Erro ao obter URL da mídia: " + e.getMessage());
            return null;
        } catch (JSONException e) {
            Log.e("getResourceByAutoDownloadAllinOneAPI", "Erro ao processar JSON: " + e.getMessage());
            return null;
        }

        return instagramResource;
    }





    public static String convertStringUrlInstagran(String url){
        String validUrl = "";
        String []vl = url.split("https:");
        if(vl.length > 1){
            validUrl = "https:" + vl[1];
        }else{
            validUrl = url;
        }
        String []ur = validUrl.split("\\?");
        String g = ur[0] + "?" + "__a=1";
        return g;

    }

    public static String sanitizeUrl(String url){
        String []ss = url.split("amp;");
        String result = "";
        for (String t:ss) {
            result += t;
        }

        return result;
    }




    public interface DownloadUtils{
        public abstract void onStartDownload();
        public abstract void onInstagramResourceBuild(InstagramResource instagramResource);
        public abstract void onError(boolean showMessage);
        public abstract void onFoundPrivateAccount();
        void onFoundProblemLink();
        void onConnectAbort();
    }

}

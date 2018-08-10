package com.example.checkmoney;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Bitmap bitmap;
    private Button btnProses;
    private ImageView imgView;
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private int PICK_PHOTO_REQUEST = 99;
    private String picturePath;
    private File f;
    private Boolean isProses = false;
    private JSONArray array = new JSONArray();
    private JSONObject object1 = new JSONObject();
    private JSONObject object2 = new JSONObject();
    private JSONObject object3 = new JSONObject();
    private JSONObject object4 = new JSONObject();
    private JSONObject object5 = new JSONObject();
    private JSONObject object6 = new JSONObject();
    private JSONObject object7 = new JSONObject();
    private JSONObject object8 = new JSONObject();
    private JSONObject object9 = new JSONObject();
    private JSONObject object10 = new JSONObject();

    private JSONObject object11 = new JSONObject();
    private JSONObject object12 = new JSONObject();
    private JSONObject object13 = new JSONObject();
    private JSONObject object14 = new JSONObject();
    private JSONObject object15 = new JSONObject();
    private JSONObject object16 = new JSONObject();
    private JSONObject object17 = new JSONObject();
    private JSONObject object18 = new JSONObject();
    private JSONObject object19 = new JSONObject();
    private int red, green, blue;
    private Boolean isOriginal = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnProses = findViewById(R.id.btnProses);
        imgView = findViewById(R.id.imghologram);
        DefaultValue();
        setPermissionForM();

        mAlbumStorageDirFactory = new BaseAlbumDirFactory();

        if (isProses) {
            btnProses.setText("PROSES");
        } else {
            btnProses.setText("TAKE HOLOGRAM");
        }

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isProses) {
                    try {
                        for (int i = 0; i < array.length(); i++) {
                            int selisihRed = array.getJSONObject(i).getInt("r") - red;
                            int selisihGreed = array.getJSONObject(i).getInt("g") - green;
                            int selisihBlue = array.getJSONObject(i).getInt("b") - blue;
                            int maxValue = 20;
                            int minValue = -20;

                            isOriginal = (
                                    (selisihRed < maxValue && selisihRed > minValue)
                                    && (selisihGreed < maxValue && selisihGreed > minValue)
                                    && (selisihBlue < maxValue && selisihBlue > minValue));

                            Log.d("dataku", String.valueOf(array.getJSONObject(i).getInt("r") - red));
                            Log.d("dataku", String.valueOf(array.getJSONObject(i).getInt("g") - green));
                            Log.d("dataku", String.valueOf(array.getJSONObject(i).getInt("b") - blue));

                            if (isOriginal){
                                Toast.makeText(MainActivity.this, "UANG ASLI" +
                                        String.valueOf(red)
                                        + " "
                                        + String.valueOf(green)
                                        + " "
                                        + String.valueOf(blue), Toast.LENGTH_LONG).show();
                                break;
                            }
                        }

                        if (!isOriginal) {
                            Toast.makeText(MainActivity.this, "UANG PALSU " + "(" +
                                    String.valueOf(red)
                                    + " "
                                    + String.valueOf(green)
                                    + " "
                                    + String.valueOf(blue) + ")", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    takeImage(PICK_PHOTO_REQUEST);
                }
            }
        });

    }

    public void takeImage(int code) {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            f = null;
            f = createImageFile();
            picturePath = f.getAbsolutePath();
            Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    f);

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, code);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File createImageFile() throws IOException {
        String imageFileName = "";
        imageFileName = "img" + "_";

        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, ".png", albumF);
        return imageF;
    }

    private File getAlbumDir() {
        File storageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir("skripsi");
            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }
        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            setPic();
            galleryAddPic();
        }
    }

    private void setPic() {
        /* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, bmOptions);
        bmOptions.inJustDecodeBounds = false;
        final int REQUIRED_SIZE = 1024;
        // Find the correct scale value. It should be the power of 2.
        int width_tmp = bmOptions.outWidth, height_tmp = bmOptions.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        bmOptions.inSampleSize = scale;
        /* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(picturePath, bmOptions);
        imgView.setImageBitmap(bitmap);
        getDominantColor(bitmap);
        //collectRGB(bitmap);

        isProses = true;
        btnProses.setText("PROSES");

    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(picturePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    private void collectRGB(Bitmap bitmap) {
        List<int[]> result = new ArrayList<>();

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ss);
        try {
            result = Color_Quantization.compute(bitmap, 5);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (result != null) {
            //bitmap.recycle();
            int[] dominantColor = result.get(0);

            red = dominantColor[0];
            green = dominantColor[1];
            blue = dominantColor[2];


            //parentView.setBackgroundColor(getDominantColor(bitmap));
        } else {
            Log.d("dataku", "warna tidak dapat di ambil");
        }
    }

    private void DefaultValue() {
        try {

            //133, 103, 83
            object1.put("r", 128);
            object1.put("g", 102);
            object1.put("b", 85);

            object2.put("r", 158);
            object2.put("g", 138);
            object2.put("b", 111);

            object3.put("r", 90);
            object3.put("g", 70);
            object3.put("b", 46);

            object4.put("r", 131);
            object4.put("g", 113);
            object4.put("b", 93);

            object5.put("r", 142);
            object5.put("g", 122);
            object5.put("b", 97);

            object6.put("r", 148);
            object6.put("g", 133);
            object6.put("b", 114);

            object7.put("r", 220);
            object7.put("g", 198);
            object7.put("b", 174);

            object8.put("r", 149);
            object8.put("g", 124);
            object8.put("b", 102);

            object9.put("r", 111);
            object9.put("g", 88);
            object9.put("b", 70);

            object10.put("r", 88);
            object10.put("g", 67);
            object10.put("b", 46);

            //50 Rebu
            object11.put("r", 135);
            object11.put("g", 103);
            object11.put("b", 93);

            object12.put("r", 114);
            object12.put("g", 112);
            object12.put("b", 101);

            object13.put("r", 140);
            object13.put("g", 107);
            object13.put("b", 96);

            object14.put("r", 152);
            object14.put("g", 113);
            object14.put("b", 104);

            object15.put("r", 148);
            object15.put("g", 109);
            object15.put("b", 98);

            object16.put("r", 149);
            object16.put("g", 113);
            object16.put("b", 104);

            object17.put("r", 158);
            object17.put("g", 121);
            object17.put("b", 105);

            object18.put("r", 112);
            object18.put("g", 91);
            object18.put("b", 88);

            object19.put("r", 132);
            object19.put("g", 94);
            object19.put("b", 81);

            //Uang 50 Ribu


            array.put(object1);
            array.put(object2);
            array.put(object3);
            array.put(object4);
            array.put(object5);
            array.put(object6);
            array.put(object7);
            array.put(object8);
            array.put(object9);
            array.put(object10);


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void setPermissionForM() {
        String[] PERMISSIONS = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Map<String, Integer> perms = new HashMap<String, Integer>();
        perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
        perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
        perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
        for (int i = 0; i < permissions.length; i++)
            perms.put(permissions[i], grantResults[i]);
        if (perms.get(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, "Some Permission is Denied", Toast.LENGTH_SHORT).show();
        }
    }

    private int getDominantColor(Bitmap bitmap) {

        //bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ss);

        if (bitmap == null) {
            return Color.TRANSPARENT;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;
        int pixels[] = new int[size];
        //Bitmap bitmap2 = bitmap.copy(Bitmap.Config.ARGB_4444, false);
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        int color;

        int a;
        int count = 0;
        for (int pixel : pixels) {
            color = pixel;
            a = Color.alpha(color);
            if (a > 0) {
                red += Color.red(color);
                green += Color.green(color);
                blue += Color.blue(color);
                count++;
            }
        }
        red /= count;
        green /= count;
        blue /= count;

//        red = (red << 16) & 0x00FF0000;
//        green = (green << 8) & 0x0000FF00;
//        blue = blue & 0x000000FF;
//        color = 0xFF000000 | red | green | blue;
        return 0;
    }
}

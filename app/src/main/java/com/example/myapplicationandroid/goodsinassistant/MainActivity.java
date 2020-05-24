package com.example.myapplicationandroid.goodsinassistant;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private String mCurrentPhotoPath;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    ImageView imageView ;

    private Context mContext=MainActivity.this;
    private static final int REQUEST = 112;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText fileNamePrefixButton = (EditText) findViewById(R.id.fileNamePrefixEditText);

        Button pictureTakingButton = (Button) findViewById(R.id.pictureTakingButton);
        pictureTakingButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                if (Build.VERSION.SDK_INT >= 23) {
                    String[] PERMISSIONS = {android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    if (!hasPermissions(mContext, PERMISSIONS)) {
                        ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, REQUEST );
                    } else {
                        //do here
                    }
                } else {
                    //do here
                }

                File file = createImageFile(fileNamePrefixButton.getText().toString());
                Uri outputFileUri = Uri.fromFile( file );


                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
//        Take a picture and pass results along to onActivityResult
                startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);

            }
        });
        imageView = (ImageView)  findViewById(R.id.imageView);


        //Disable the button if there is no camera on phone
        if(!hasCamera()){
            pictureTakingButton.setEnabled(false);
        }
    }

    private boolean hasCamera(){
        return  getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
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
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //do here
                } else {
                    Toast.makeText(mContext, "The app was not allowed to read your store.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

//    Launching camera
    public void launchCamera(View view){


    }

    private File createImageFile( final String filePrefix ){
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
        String imageFileName = filePrefix + "_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES + "/WorkPictures");
        if (!storageDir.exists()) {
            storageDir.mkdir();
        }
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  // prefix
                    ".png",         // suffix
                    storageDir      // directory
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

//    If you want to return image taken
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
//           Get the photo
           Bundle extras = data.getExtras();
           Bitmap photo = null;
           try {
               photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
           } catch (IOException e) {
               e.printStackTrace();
           }
           imageView.setImageBitmap(photo);
       }
    }


}

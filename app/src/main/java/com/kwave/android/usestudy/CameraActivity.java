package com.kwave.android.usestudy;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class CameraActivity extends AppCompatActivity {

    ImageView btnImage;
    Button btnCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // 위젯을 세팅해준다.
        btnImage = (ImageView) findViewById(R.id.btnImage);
        btnCapture = (Button) findViewById(R.id.btnCapture);
        // 사용할 버튼이 하나이므로 바로 리스너처리까지 해준다.
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();    // 버튼이 눌리면  사진을 찍으면 된다.
            }
        });
    }
    Uri fileUri = null;
    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);    // 인텐터를 호출하면 사진을 찍을 수 있게 세팅해준다.
        // 롤리팝 이상 버전은 권한을 획득해야함
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            File photoFile = null;
            try {
                photoFile = createFile();
                if (photoFile != null) {
                    // 마시멜로 이상 버전은 파일 프로바이더를 통해 권한을 획득
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // 파일프로바이더에게 파일에 대한 URI를 받아온다.
                        fileUri = FileProvider.getUriForFile(getBaseContext(), BuildConfig.APPLICATION_ID+".provider", photoFile);

                    } else {   // 마시멜로우 이하 버전은 권한 없어도 됨
                        fileUri = Uri.fromFile(photoFile);  // 마시멜로우 이하 버전에서는 파일에서 바로 가져올 수 있다.
                    }
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(intent, Const.Camera.REQ_CAMERA);
                }
            }
            catch (Exception e) {
                Toast.makeText(getBaseContext(), "사진파일 저장을 위한 임시파일을 생성할 수 없습니다.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return;
            }
        }else{      // 롤리팝 미만 버전에서만 바로 실행
                startActivityForResult(intent,Const.Camera.REQ_CAMERA);
        }
    }

    private File createFile() throws IOException{
        // 임시 파일명 생성
        String tempFilename = "TEMP" + System.currentTimeMillis();
        // 임시 파일 저장용 디렉토리 생성
        File tempDir = new File(Environment.getExternalStorageDirectory()+"/CameraN/");
        if(!tempDir.exists()){
            tempDir.mkdir();
        }
        // 실제 임시파일을 생성
        File tempFile = File.createTempFile(tempFilename, ".JPG" , tempDir);
        return tempFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 요청 코드 구분
        if(requestCode == Const.Camera.REQ_CAMERA) {
            if (resultCode == RESULT_OK) {
                Uri imageUri = null;
                // 롤리팝 미만 버전에서는 data 인텐트를 찍은 사진의 uri가 담겨온다.
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    imageUri = data.getData();
                } else {       // 롤리팝 미만 버전에서만 바로 실행
                    imageUri = fileUri;
                }
                Log.i("Camera", "fileUri=====================" + fileUri);
                Log.i("Camera", "imageUri=====================" + imageUri);
                btnImage.setImageURI(imageUri);
            }
        }
    }
}

package com.example.qudqj_000.a2017_05_11;

import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity {
    EditText et;
    ListView lv1;
    ArrayList<String> name = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mydiary);
        permissionCheck();
        program();

    }

    void program(){
        et = (EditText)findViewById(R.id.memo);
        lv1 = (ListView)findViewById(R.id.listview);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, name);
        lv1.setAdapter(adapter);

    }

    public void onClick(View v){
        if(v.getId() == R.id.button1){
            try {
                BufferedReader br = new BufferedReader(
                        new FileReader(getFilesDir() + "test.txt"));
                String readStr="";
                String str = null;
                while((str = br.readLine()) !=null)
                    readStr += str+"\n";
                br.close();
                Toast.makeText(this, readStr.substring(0, readStr.length()-1), Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if(v.getId() == R.id.button2){
            try {
                BufferedWriter bw = new BufferedWriter(
                        new FileWriter(getFilesDir() +
                                "test.txt", false));
                bw.write("안녕하세요 Hello\n");
//                bw.newLine();
                bw.close();
                Toast.makeText(this, "저장완료",
                        Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
        if(v.getId() == R.id.button3){
            InputStream is = getResources().openRawResource(R.raw.about);
            try {
                byte[] readStr = new byte[is.available()];
                is.read(readStr);
                is.close();
                Toast.makeText(this, new String(readStr), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(v.getId() == R.id.button4){
            try {
                String path = getExternalPath();
                BufferedReader br = new BufferedReader(
                        new FileReader(path+"mydiary/internal2.txt"));
                String readStr="";
                String str = null;
                while((str = br.readLine()) !=null)
                    readStr += str+"\n";
                br.close();
                Toast.makeText(this, readStr.substring(0, readStr.length()-1), Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(v.getId() == R.id.button5){
            try {
                String path = getExternalPath();
                BufferedWriter bw = new BufferedWriter(
                        new FileWriter(path + "mydiary/internal2.txt", false));
                bw.write("안녕하세요 SDCard Hello\n");
//                bw.newLine();
                bw.close();
                Toast.makeText(this, "저장완료",
                        Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
        if(v.getId() == R.id.button6){
            String path = getExternalPath();

            File file = new File(path + "mydiary");
            file.mkdir();

            String msg = "디렉터리 생성";
            if(file.isDirectory() == false) msg="디렉토리 생성 오류";
            Toast.makeText(this, msg,Toast.LENGTH_SHORT).show();
        }
        if(v.getId() == R.id.button7){
            String path =getExternalPath();
            File[] files = new File(path + "mydiary").listFiles();
            String str = "";
            for(File f:files)
                str += f.getName() + "\n" ;
            et.setText(str);
        }
        if(v.getId() == R.id.btn1){

        }
    }

    void permissionCheck(){
        int permissioninfo = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(permissioninfo == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getApplicationContext(),"SDcard 쓰기권한있음",Toast.LENGTH_SHORT).show();
        }
        else{
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                Toast.makeText(this, "권한설명", Toast.LENGTH_SHORT).show();
            }
            else{
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            }
        }
    }

    public String getExternalPath(){
        String sdPath = "";
        String ext = Environment.getExternalStorageState();
        if(ext.equals(Environment.MEDIA_MOUNTED)) {
            sdPath = Environment.getExternalStorageDirectory ().getAbsolutePath() + "/"; //sdPath = "/mnt/sdcard/";
        }else
            sdPath = getFilesDir()+"";
        Toast.makeText(getApplicationContext(),
                sdPath, Toast.LENGTH_SHORT).show();
        return sdPath;
    }
}

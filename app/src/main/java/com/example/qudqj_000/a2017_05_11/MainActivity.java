package com.example.qudqj_000.a2017_05_11;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity {
    EditText memo;
    TextView tv1;
    ListView lv1;
    Button btnsave;
    ArrayList<String> name = new ArrayList<>();
    ArrayAdapter<String> adapter;
    DatePicker dp1;
    LinearLayout linear1, linear2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mydiary);
        permissionCheck();
        program();
        listViewAction();
    }

    void program(){
        memo = (EditText)findViewById(R.id.memo);
        lv1 = (ListView)findViewById(R.id.listview);
        dp1 = (DatePicker)findViewById(R.id.date);
        tv1 = (TextView)findViewById(R.id.tvCount);
        linear1 = (LinearLayout)findViewById(R.id.linear1);
        linear2 = (LinearLayout)findViewById(R.id.linear2);
        btnsave = (Button)findViewById(R.id.btnsave);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, name);
        lv1.setAdapter(adapter);

        createDirectory();
        showFileList();
    }

    void createDirectory(){
        String path = getExternalPath();

        File file = new File(path + "diary");
        file.mkdir();
//
//        String msg = "디렉터리 생성";
//        if(file.isDirectory() == false) msg="디렉토리 생성 오류";
//        Toast.makeText(this, msg,Toast.LENGTH_SHORT).show();
    }

    void showFileList(){
        String path = getExternalPath();
        int count=0;
        name.clear();

        File[] files = new File(path + "diary").listFiles();

        for(File f:files) {
            name.add(f.getName());
            count++;
        }

        sorting();
        adapter.notifyDataSetChanged();

        tv1.setText("등록된 메모 개수: " + String.valueOf(count));
    }

    Comparator<String> compare = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    };

    void sorting(){
        Collections.sort(name, compare);
    }

    String nameFormat(Date d){
        SimpleDateFormat df =new SimpleDateFormat("yy-MM-dd");
        String name = df.format(d)+".memo";
        return name;
    }

    public void onClick(View v){
//        if(v.getId() == R.id.button4){
//            try {
//                String path = getExternalPath();
//                BufferedReader br = new BufferedReader(
//                        new FileReader(path+"mydiary/internal2.txt"));
//                String readStr="";
//                String str = null;
//                while((str = br.readLine()) !=null)
//                    readStr += str+"\n";
//                br.close();
//                Toast.makeText(this, readStr.substring(0, readStr.length()-1), Toast.LENGTH_SHORT).show();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//                Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        if(v.getId() == R.id.button5){
//            try {
//                String path = getExternalPath();
//                BufferedWriter bw = new BufferedWriter(
//                        new FileWriter(path + "mydiary/internal2.txt", false));
//                bw.write("안녕하세요 SDCard Hello\n");
//
//                bw.close();
//                Toast.makeText(this, "저장완료",
//                        Toast.LENGTH_SHORT).show();
//            } catch (IOException e) {
//                e.printStackTrace();
//                Toast.makeText(this, e.getMessage(),
//                        Toast.LENGTH_SHORT).show();
//            }
//        }
//        if(v.getId() == R.id.button6){
//            String path = getExternalPath();
//
//            File file = new File(path + "mydiary");
//            file.mkdir();
//
//            String msg = "디렉터리 생성";
//            if(file.isDirectory() == false) msg="디렉토리 생성 오류";
//            Toast.makeText(this, msg,Toast.LENGTH_SHORT).show();
//        }
//        if(v.getId() == R.id.button7){
//            String path =getExternalPath();
//            File[] files = new File(path + "mydiary").listFiles();
//            String str = "";
//            for(File f:files)
//                str += f.getName() + "\n" ;
//
//        }
        if(v.getId() == R.id.btn1){
            linear1.setVisibility(View.INVISIBLE);
            linear2.setVisibility(View.VISIBLE);
        }
        if(v.getId()==R.id.btnsave){
            linear1.setVisibility(View.VISIBLE);
            linear2.setVisibility(View.INVISIBLE);

            if(btnsave.getText().toString().equals("저장")) {
                Date date = new Date(dp1.getYear(), dp1.getMonth(), dp1.getDayOfMonth());
                String name = nameFormat(date);
                String path = getExternalPath();

                try {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(path + "diary/" + name + ".txt", false));
                    bw.write(memo.getText().toString());
                    bw.newLine();
                    bw.close();
                    Toast.makeText(this, "저장완료", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{

            }
            showFileList();
        }
        if(v.getId()==R.id.btncancel){
            linear1.setVisibility(View.VISIBLE);
            linear2.setVisibility(View.INVISIBLE);
        }
    }

    void listViewAction(){
        lv1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                dlg.setIcon(R.mipmap.ic_launcher)
                        .setTitle("삭제")
                        .setMessage("삭제하시겠습니까?")
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String path = getExternalPath();
                                String item = name.get(position);

                                File file = new File(path + "diary/" + item);
                                file.delete();

                                name.remove(position);
                                showFileList();
                            }
                        })
                        .setNegativeButton("취소",null)
                        .show();
                return true;
            }
        });

        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String path = getExternalPath();

                try {
                    BufferedReader br = new BufferedReader(new FileReader(path + "diary/" + name.get(position)));
                    String readStr = "";
                    String str;
                    while((str=br.readLine())!=null){
                        readStr += str;
                    }
                    br.close();

                    memo.setText(readStr);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String date = name.get(position).substring(0,8);


                linear1.setVisibility(View.INVISIBLE);
                linear2.setVisibility(View.VISIBLE);
            }
        });
    }
    void permissionCheck(){
        int permissioninfo = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(permissioninfo == PackageManager.PERMISSION_GRANTED){
//            Toast.makeText(getApplicationContext(),"SDcard 쓰기권한있음",Toast.LENGTH_SHORT).show();
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
//        Toast.makeText(getApplicationContext(),
//                sdPath, Toast.LENGTH_SHORT).show();
        return sdPath;
    }
}

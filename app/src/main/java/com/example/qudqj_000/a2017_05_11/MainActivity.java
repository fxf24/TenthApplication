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
    int count=0;
    String temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mydiary);
        permissionCheck();
        program();
        listViewAction();
    }

    void program() {
        memo = (EditText) findViewById(R.id.memo);
        lv1 = (ListView) findViewById(R.id.listview);
        dp1 = (DatePicker) findViewById(R.id.date);
        tv1 = (TextView) findViewById(R.id.tvCount);
        linear1 = (LinearLayout) findViewById(R.id.linear1);
        linear2 = (LinearLayout) findViewById(R.id.linear2);
        btnsave = (Button) findViewById(R.id.btnsave);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, name);
        lv1.setAdapter(adapter);

        createDirectory();
        showFileList();
    }

    void createDirectory() {
        String path = getExternalPath();

        File file = new File(path + "diary");
        file.mkdir();
    }

    void showFileList() {
        String path = getExternalPath();
        int count = 0;
        name.clear();

        File[] files = new File(path + "diary").listFiles();

        for (File f : files) {
            name.add(f.getName().substring(0, 13));
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

    void sorting() {
        Collections.sort(name, compare);
    }

    String nameFormat(Date d) {
        SimpleDateFormat df = new SimpleDateFormat("yy-MM-dd");
        String name = df.format(d) + ".memo";
        return name;
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btn1) {
            linear1.setVisibility(View.INVISIBLE);
            linear2.setVisibility(View.VISIBLE);
            memo.setText("");
        }
        if (v.getId() == R.id.btnsave) {
            linear1.setVisibility(View.VISIBLE);
            linear2.setVisibility(View.INVISIBLE);

            Date date = new Date(dp1.getYear(), dp1.getMonth(), dp1.getDayOfMonth());
            final String filename = nameFormat(date);
            final String path = getExternalPath();

            if (btnsave.getText().toString().equals("저장")) {
                count = 0;
                if (name.contains(filename)) {
                    AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                    dlg.setIcon(R.mipmap.ic_launcher)
                            .setTitle("파일 있음")
                            .setMessage("같은 날짜 파일이 존재합니다. 불러오시겠습니까?")
                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    readFile(path + "diary/" + filename + ".txt");
                                    btnsave.setText("수정");
                                }
                            }).show();
                    linear1.setVisibility(View.INVISIBLE);
                    linear2.setVisibility(View.VISIBLE);
                    temp = filename;
                    count++;
                    return;
                }

                writeFile(path + "diary/" + filename + ".txt");
                Toast.makeText(this, "저장완료", Toast.LENGTH_SHORT).show();

            } else {
                if(count !=0) {
                    deleteExternalFile(path + "diary/" + temp + ".txt");
                    count = 0;
                }
                writeFile(path + "diary/" + filename + ".txt");
                Toast.makeText(this, "수정완료", Toast.LENGTH_SHORT).show();
                btnsave.setText("저장");
            }
            showFileList();
        }
        if (v.getId() == R.id.btncancel) {
            linear1.setVisibility(View.VISIBLE);
            linear2.setVisibility(View.INVISIBLE);
            btnsave.setText("저장");
        }
    }

    void listViewAction() {
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

                                deleteExternalFile(path + "diary/" + item + ".txt");
                                showFileList();
                            }
                        })
                        .setNegativeButton("취소", null)
                        .show();
                return true;
            }
        });

        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String path = getExternalPath();

                readFile(path + "diary/" + name.get(position) + ".txt");

                String[] date = name.get(position).substring(0, 8).split("-");
                if (Integer.parseInt(date[0]) <= 20)
                    date[0] = "20" + date[0];
                else
                    date[0] = "19" + date[0];

                dp1.init(Integer.parseInt(date[0]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[2]), null);

                btnsave.setText("수정");
                linear1.setVisibility(View.INVISIBLE);
                linear2.setVisibility(View.VISIBLE);
            }
        });
    }

    void permissionCheck() {
        int permissioninfo = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissioninfo == PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(getApplicationContext(),"SDcard 쓰기권한있음",Toast.LENGTH_SHORT).show();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "권한설명", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            }
        }
    }

    public String getExternalPath() {
        String sdPath = "";
        String ext = Environment.getExternalStorageState();
        if (ext.equals(Environment.MEDIA_MOUNTED)) {
            sdPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"; //sdPath = "/mnt/sdcard/";
        } else
            sdPath = getFilesDir() + "";
//        Toast.makeText(getApplicationContext(),
//                sdPath, Toast.LENGTH_SHORT).show();
        return sdPath;
    }

    void readFile(String path) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String readStr = "";
            String str;
            while ((str = br.readLine()) != null) {
                readStr += str;
            }
            br.close();

            memo.setText(readStr);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void writeFile(String path) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(path, false));
            bw.write(memo.getText().toString());
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void deleteExternalFile(String path) {
        File file = new File(path);
        file.delete();

    }
}

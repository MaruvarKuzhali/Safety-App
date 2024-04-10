package com.example.safety;
import org.alicebot.ab.*;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import java.util.*;
import java.io.*;

import com.example.safety.Adapter.ChatMessageAdapter;
import com.example.safety.Model.ChatMessage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;

public class MainActivity3 extends AppCompatActivity {

    ListView listView;
    FloatingActionButton btnSend;
    EditText edtTextMsg;
    ImageView imageView;

    public Bot bot;
    public static Chat chat;
    private ChatMessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        listView = findViewById(R.id.listView);
        btnSend = findViewById(R.id.btnSend);
        edtTextMsg = findViewById(R.id.editTextMsg);
        imageView = findViewById(R.id.imageView);

        adapter = new ChatMessageAdapter(this, new ArrayList<ChatMessage>());
        listView.setAdapter(adapter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = edtTextMsg.getText().toString();
                String response = chat.multisentenceRespond(message);
                System.out.println(message);
                System.out.println(response);
                if(TextUtils.isEmpty(message)){
                    Toast.makeText(MainActivity3.this,"Please enter a query..",Toast.LENGTH_SHORT).show();
                    return;
                }
                sendMessage(message);
                botsReply(response);
                edtTextMsg.setText("");
                listView.setSelection(adapter.getCount() - 1 );
            }
        });

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener(){
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report){
                        if(report.areAllPermissionsGranted()){
                            custom();
                            Toast.makeText(MainActivity3.this,"Permission granted",Toast.LENGTH_SHORT).show();
                        }
                        if(report.isAnyPermissionPermanentlyDenied()){
                            Toast.makeText(MainActivity3.this,"Please grant all permission",Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token){
                        token.continuePermissionRequest();
                    }
        }).withErrorListener(new PermissionRequestErrorListener() {
            @Override
            public void onError(DexterError error) {
                Toast.makeText(MainActivity3.this,""+error,Toast.LENGTH_SHORT).show();
            }
        }).onSameThread().check();
    }

    private void botsReply(String response) {
        ChatMessage chatMessage = new ChatMessage(response,false,false);
        adapter.add(chatMessage);
    }

    private void sendMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message,true,false);
        adapter.add(chatMessage);
    }

    private void custom(){
        boolean available = isSDCARDAvailable();

        AssetManager assets = getResources().getAssets();
        File filename = new File(Environment.getExternalStorageDirectory().toString()+"/TBC/bots/bot");

        boolean makeFile = filename.mkdirs();
        if(filename.exists()){
            try{
                for(String dir : assets.list("bot")){
                    File subDir = new File(filename.getPath()+"/"+dir);
                    boolean subDir_check = subDir.mkdirs();

                    for(String file : assets.list("bot/"+dir)){
                        File newfile = new File(filename.getPath()+"/"+dir+"/"+file);
                        if(newfile.exists()){
                            continue;
                        }
                        InputStream in;
                        OutputStream out;

                        in = assets.open("bot/"+dir+"/"+file);
                        out = new FileOutputStream(filename.getPath()+"/"+dir+"/"+file);

                        copyFile(in,out);
                        in.close();
                        out.flush();
                        out.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        MagicStrings.root_path = Environment.getExternalStorageDirectory().toString()+"/TBC";
        AIMLProcessor.extension = new PCAIMLProcessorExtension();

        bot = new Bot("bot",MagicStrings.root_path,"chat");
        chat = new Chat(bot);
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException{
        byte [] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer))!= -1){
            out.write(buffer,0,read);
        }
    }

    private boolean isSDCARDAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)? true : false;
    }
}
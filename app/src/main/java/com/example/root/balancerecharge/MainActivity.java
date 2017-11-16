package com.example.root.balancerecharge;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Thread thread;

    private boolean checkCallPermission(){
        String permission = "android.permission.CALL_PHONE";
        int res = getApplicationContext().checkCallingOrSelfPermission(permission);
        return(res == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button mdialButton = (Button) findViewById(R.id.dialButton);
        final EditText mtext = (EditText) findViewById(R.id.editText);

        this.thread = new Thread(new SocketServer());
        this.thread.start();

        mdialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String telNo = mtext.getText().toString();
                String phoneNoUssd;
                if(telNo.matches("^\\*[0-9]+([0-9*#])*#$")){
                    phoneNoUssd = telNo.substring(0, telNo.indexOf("#")) + Uri.encode("#");
                }
                else {
                    phoneNoUssd = telNo;
                }
                if (!TextUtils.isEmpty(phoneNoUssd)) {
                    if(checkCallPermission()){
                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNoUssd)));
                    }
                }else {
                    Toast.makeText(MainActivity.this, "Enter a phone number", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}


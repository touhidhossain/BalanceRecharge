package com.example.root.balancerecharge;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by root on 11/17/17.
 */

class SocketServer extends Activity implements Runnable{
    private static final int port = 5550;


    @Override
    public void run() {
        try{
            ServerSocket serverSocket = new ServerSocket(port);

            while(true){
                System.out.println("Waiting for recharge request");
                Socket socket = serverSocket.accept();
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                String message = (String) objectInputStream.readObject();
                System.out.println("Message Received: " + message);
                onRechargeRequest(message);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectOutputStream.writeObject("Balance checked ");
                objectInputStream.close();
                objectOutputStream.close();
                socket.close();
                if(message.equalsIgnoreCase("exit")) break;
            }
        }
        catch(IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean checkCallPermission(){
        String permission = "android.permission.CALL_PHONE";
        int res = getApplicationContext().checkCallingOrSelfPermission(permission);
        return(res == PackageManager.PERMISSION_GRANTED);
    }

    private void onRechargeRequest(String mtext){
        String telNo = mtext;
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
            Toast.makeText(this, "Enter a phone number", Toast.LENGTH_SHORT).show();
        }
    }
}


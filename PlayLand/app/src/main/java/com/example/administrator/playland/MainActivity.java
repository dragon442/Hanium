package com.example.administrator.playland;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    EditText edt1, edt2, edt4;
    Button btn1, btn2;
    TextView txt1, txt2;
    RadioGroup rgp;
    RadioButton rdo1, rdo2, rdo3;
    ProgressBar pro1;

    String id = "", nomality, accom_num;
    String myResult;
    String outUrlStr = "http://192.168.123.15:8080/play_land/ride.jsp";
    String inUrlStr = "http://192.168.123.15:8080/play_land/current_wait.jsp";

    Long time1, time2;
    TextHandler texthandler;

    InputTask inTask;
    OutputTask outTask;

    int count=0;
    String DeviceNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edt1 = (EditText)findViewById(R.id.edt1);
        edt2 = (EditText)findViewById(R.id.edt2);
        edt4 = (EditText)findViewById(R.id.edt4);
        btn1 = (Button)findViewById(R.id.btn1);
        btn2 = (Button)findViewById(R.id.btn2);
        txt1 = (TextView)findViewById(R.id.txt1);
        txt2 = (TextView)findViewById(R.id.txt2);
        rgp = (RadioGroup)findViewById(R.id.rgp);
        rdo1 = (RadioButton)findViewById(R.id.rdo1);
        rdo2 = (RadioButton)findViewById(R.id.rdo2);
        rdo3 = (RadioButton)findViewById(R.id.rdo3);
        pro1 = (ProgressBar)findViewById(R.id.pro1);
        texthandler = new TextHandler();

   /*     new Thread() {
            public void run() {
                try {
                    Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
                    while (enumNetworkInterfaces.hasMoreElements())

                    {
                        NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                        Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
                        while (enumInetAddress.hasMoreElements()) {
                            InetAddress inetAddress = enumInetAddress.nextElement();
                            if (inetAddress.isSiteLocalAddress()) {
                                inUrlStr = "http://" + inetAddress.getLocalHost() + ":8080/play_land/current_wait.jsp";
                                outUrlStr = "http://" + inetAddress.getLocalHost() + ":8080/play_land/ride.jsp";
                                txt1.setText("Local Host : " + inetAddress.getLocalHost().toString() + "\n"
                                        + "Address : " + inetAddress.getAddress() + "\n"
                                        + "HostAddress : " + inetAddress.getHostAddress());
                                //Toast.makeText(getApplicationContext(), outUrlStr, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();*/


        //단말기 고유값 얻기
       // if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) == getPackageManager().PERMISSION_GRANTED) {
            TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            DeviceNum = mTelephonyMgr.getDeviceId();
            Toast.makeText(getApplicationContext(), "DeviceNum : " + DeviceNum, Toast.LENGTH_SHORT).show();
      //  }

        //DB쓰기
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = edt1.getText().toString();
                if(id.equals("")) {
                    id = DeviceNum;
                }
                //nomality = edt2.getText().toString();
                outTask = new OutputTask();
                switch (rgp.getCheckedRadioButtonId()) {
                    case R.id.rdo1 :
                        outTask.execute(rdo1.getText().toString());
                        break;
                    case R.id.rdo2 :
                        outTask.execute(rdo2.getText().toString());
                        break;
                    case R.id.rdo3 :
                        outTask.execute(rdo3.getText().toString());
                        break;
                }
            }
        });

        //DB읽기
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = edt4.getText().toString();
                if(id.equals("")) {
                    id = DeviceNum;
                }
                Toast.makeText(getApplicationContext(), "id : " + id, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Reservation.class);
                intent.putExtra("ID", id);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();

                /*
                id = edt4.getText().toString();
                inTask = new InputTask();    //객체 생성
                inTask.execute(id);//AsyncTask 객체 생성후 호출시 정의된 백그라운드 작업 수행 및 메인스레드에서 그 결과 실행 => UI접근
                */
            }
        });

    }

    public class TextHandler extends Handler {  //Handler 클래스를 상속한 클래스 정의 => handleMessage를 재정의하여 메인스레드의 기능 정의
        public void handleMessage(Message msg) {   //전달된 메시지를 처리 => 프로그레스바의 값 업데이트
            txt1.setText(myResult);
        }
    }

    //DB insert(DB에 쓰기)
    class OutputTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                System.out.println("OutpuTask 실행");
                URL url = new URL(outUrlStr);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();

                http.setDefaultUseCaches(false);
                http.setDoInput(true);
                http.setDoOutput(true);
                http.setRequestMethod("POST");

                StringBuffer buffer = new StringBuffer();
                buffer.append("id").append("=").append(id).append("&");
                //buffer.append("nomality").append("=").append(nomality).append("&");
                //buffer.append("accom_num").append("=").append(accom_num).append("&");
                buffer.append("play").append("=").append(params[0]);


                OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF-8");
                PrintWriter writer = new PrintWriter(outStream);
                writer.write(buffer.toString());
                writer.flush();

                InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF-8");
                BufferedReader reader = new BufferedReader(tmp);
                StringBuilder builder = new StringBuilder();
                String str;
                while((str=reader.readLine())!=null) {
                    builder.append(str+"\n");
                }
                myResult = builder.toString();

                Thread thread1 = new Thread(new Runnable() {    //엑티비티 시작시 스레드를 생성하여 시작
                    @Override
                    public void run() {
                        try {
                            //Thread.sleep(1000);         //1초 대기
                            Message msg = texthandler.obtainMessage();      //obtainMessage() : 호출의 결과로 메세지 객체 리턴 => 이를 메세지 객체에 넣음
                            texthandler.sendMessage(msg);       //sendMessage를 이용해 메시지 큐에 넣음 => 처리시 handleMessage()메소드의 내용 수행

                        } catch(Exception e){
                            Log.e("MainActivity", "Exception in progress message.", e);
                        }
                    }
                });
                thread1.start();

                reader.close();
                http.disconnect();

            } catch(Exception e) {
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void integer) {   //백그라운드 작업 종료 후 호출, 메인스레드에서 실행되며 메모리 리소스 해제 등의 작업에 사용
            //백그라운드 작업 결과는 Result 타입 파라미터
            Toast.makeText(getApplicationContext(), "백그라운드 작업 종료", Toast.LENGTH_SHORT).show();
        }
    }

    //DB select(DB 읽어오기)
    class InputTask extends AsyncTask<String, String, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Toast는 OS가 관리하므로 Thread가 사용됨 => AsyncTask 스레드 안에서 스레드 사용하므로 오류 발생
            //이를 해결하기위해 핸들러를 사용하자
            while(true) {
                if (getPro1() < 100) {
                    try {
                        URL url = new URL(inUrlStr);
                        HttpURLConnection http = (HttpURLConnection) url.openConnection();

                        http.setDefaultUseCaches(false);
                        http.setDoInput(true);
                        http.setDoOutput(true);
                        http.setRequestMethod("POST");

                        InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF-8");
                        BufferedReader reader = new BufferedReader(tmp);
                        StringBuilder builder = new StringBuilder();
                        String str;
                        //String edt = params[0];  //아니면 onPreExecute()에서 값 대입
                        while ((str = reader.readLine()) != null) {
                            if (str.contains(id)) {
                                if (str.length() >= 6 && str.substring(6).equals(id)) {
                                    count = 7;
                                }
                            }
                            if (count > 0) {
                                str = str.substring(6);
                                if (count == 2) {
                                    time1 = Long.parseLong(str);
                                } else if (count == 1) {
                                    time2 = Long.parseLong(str);
                                }
                                builder.append(str + "\n");
                                count--;
                            }
                        }
                        builder.append(System.currentTimeMillis());
                        myResult = builder.toString();

                        Thread thread1 = new Thread(new Runnable() {    //엑티비티 시작시 스레드를 생성하여 시작
                            @Override
                            public void run() {
                                try {
                                    //Thread.sleep(1000);         //1초 대기
                                    Message msg = texthandler.obtainMessage();      //obtainMessage() : 호출의 결과로 메세지 객체 리턴 => 이를 메세지 객체에 넣음
                                    texthandler.sendMessage(msg);       //sendMessage를 이용해 메시지 큐에 넣음 => 처리시 handleMessage()메소드의 내용 수행

                                } catch (Exception e) {
                                    Log.e("MainActivity", "Exception in progress message.", e);
                                }
                            }
                        });
                        thread1.start();
                        publishProgress();

                        reader.close();
                        http.disconnect();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                    }
                } else {
                    myResult = "탑승";
                    Thread thread1 = new Thread(new Runnable() {    //엑티비티 시작시 스레드를 생성하여 시작
                        @Override
                        public void run() {
                            try {
                                //Thread.sleep(1000);         //1초 대기
                                Message msg = texthandler.obtainMessage();      //obtainMessage() : 호출의 결과로 메세지 객체 리턴 => 이를 메세지 객체에 넣음
                                texthandler.sendMessage(msg);       //sendMessage를 이용해 메시지 큐에 넣음 => 처리시 handleMessage()메소드의 내용 수행

                            } catch (Exception e) {
                                Log.e("MainActivity", "Exception in progress message.", e);
                            }
                        }
                    });
                    thread1.start();
                    break;
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if(time2 - time1 == 0) {
                pro1.setProgress(100);
            } else{
                //Long l = time1 + 100000;
                Long time = 100 * (System.currentTimeMillis() - time1) / (time2 - time1);
                int value = Integer.parseInt(time.toString());
                pro1.setProgress(value);
            }

            Date date = new Date(time2 - System.currentTimeMillis());
            long l1 = date.getTime();	//date를 다시 ms로 변환
            SimpleDateFormat simpleDate = new SimpleDateFormat("HH:mm:ss");
            String time = (String)simpleDate.format(l1 - 3600*1000*9);
            txt2.setText("남은시간 : " + time.toString());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
    }
    public int getPro1() {
        return pro1.getProgress();
    }
}

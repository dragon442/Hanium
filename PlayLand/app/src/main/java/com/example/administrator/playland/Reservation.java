package com.example.administrator.playland;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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

/**
 * Created by Administrator on 2016-08-26.
 */
public class Reservation extends AppCompatActivity{

    TextView[] rp = new TextView[3];    //예약인원
    TextView[] wt = new TextView[3];    //예상대기시간
    TextView[] rt = new TextView[3];    //남은시간
    ProgressBar[] pro = new ProgressBar[3];
    Button[] btn = new Button[3];
    Button btn4;
    Button btn5;

    Integer[] rpId = {R.id.reverve_people1, R.id.reverve_people2, R.id.reverve_people3};
    Integer[] wtId = {R.id.waiting_time1, R.id.waiting_time2, R.id.waiting_time3};
    Integer[] rtId = {R.id.remain_time1, R.id.remain_time2, R.id.remain_time3};
    Integer[] proId = {R.id.pro1, R.id.pro2, R.id.pro3};
    Integer[] btnId = {R.id.btn1, R.id.btn2, R.id.btn3};

    String id = "";
    String inUrlStr = "http://192.168.123.15:8080/play_land/current_wait.jsp";
    String outUrlStr = "http://192.168.123.15:8080/play_land/ride.jsp";

    InputTask inTask;
    OutputTask outTask;

    SwapHandler swapHandler;

    int count=0, playing=0;
    int swapIndex = 1;
    String DeviceNum;
    Boolean RecordCheck = true;
    Boolean swapCheck = true;   //true면 버튼, false면 텍스트뷰

    Character[] nom = new Character[3];
    Integer[] accom_num = new Integer[3];
    Long[] reg_time = new Long[3];
    Long[] rid_time = new Long[3];
    Boolean[] proTest = {true, true, true};

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        inTask.cancel(true);
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservation);

        TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        DeviceNum = mTelephonyMgr.getDeviceId();

        for(int i=0; i<pro.length; i++) {
            rp[i] = (TextView)findViewById(rpId[i]);
            wt[i] = (TextView)findViewById(wtId[i]);
            rt[i] = (TextView)findViewById(rtId[i]);
            pro[i] = (ProgressBar)findViewById(proId[i]);
            btn[i] = (Button)findViewById(btnId[i]);
        }
        btn4 = (Button)findViewById(R.id.btn4);
        btn5 = (Button)findViewById(R.id.btn5);
        swapHandler = new SwapHandler();


        Intent inIntent = getIntent();
        id = inIntent.getStringExtra("ID");
        if(id == null) {
            id = "";
        }

        if(id.equals("")) {
            id = DeviceNum;
        }

        inTask = new InputTask();
        inTask.execute(id);

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inTask.cancel(true);

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inTask.cancel(true);

         /*       Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();*/
            }
        });
        for(int i=0; i<btn.length; i++) {
            final int index = i;
            btn[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    inTask.cancel(true);
                    if(btn[index].getText().equals("탑승완료")) {
                        pro[index].setProgress(0);
                        btn[index].setText("예약하기");
                    }
                    else {
                    outTask = new OutputTask();
                    switch (index) {
                        case 0 :
                            outTask.execute("play1");
                            break;
                        case 1 :
                            outTask.execute("play2");
                            break;
                        case 2 :
                            outTask.execute("play3");
                            break;
                    }
                    }
                }
            });
        }

    }

    //DB insert(DB에 쓰기)
    class OutputTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL(outUrlStr);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();

                http.setDefaultUseCaches(false);
                http.setDoInput(true);
                http.setDoOutput(true);
                http.setRequestMethod("POST");

                StringBuffer buffer = new StringBuffer();
                buffer.append("id").append("=").append(DeviceNum).append("&");
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
                String str2;
                while((str2=reader.readLine())!=null) {
                    builder.append(str2+"\n");
                }

                swapIndex = Integer.parseInt(params[0].substring(4)) - 1;
                swapCheck = false;

                Thread thread1 = new Thread(new Runnable() {    //엑티비티 시작시 스레드를 생성하여 시작
                    @Override
                    public void run() {
                        try {
                            Message msg = swapHandler.obtainMessage();      //obtainMessage() : 호출의 결과로 메세지 객체 리턴 => 이를 메세지 객체에 넣음
                            swapHandler.sendMessage(msg);       //sendMessage를 이용해 메시지 큐에 넣음 => 처리시 handleMessage()메소드의 내용 수행


                            inTask = new InputTask();
                            inTask.execute(id);

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
            Toast.makeText(getApplicationContext(), "예약완료", Toast.LENGTH_SHORT).show();
        }
    }

    public class SwapHandler extends Handler {  //Handler 클래스를 상속한 클래스 정의 => handleMessage를 재정의하여 메인스레드의 기능 정의
        public void handleMessage(Message msg) {   //전달된 메시지를 처리 => 프로그레스바의 값 업데이트
            final int i = swapIndex;
            if(swapCheck) {
                btn[i].setVisibility(View.VISIBLE);
                rt[i].setVisibility(View.GONE);
            } else {
                btn[i].setVisibility(View.GONE);
                rt[i].setVisibility(View.VISIBLE);
            }
        }
    }

    //DB select(DB 읽어오기)
    class InputTask extends AsyncTask<String, String, Void> {
        @Override
        protected Void doInBackground(String... params) {
            while(isCancelled() == false) {
                if (getPro(0) < 100 || getPro(1) < 100 || getPro(2) < 100) {
                    try {
                        URL url = new URL(inUrlStr);
                        HttpURLConnection http = (HttpURLConnection) url.openConnection();

                        http.setDefaultUseCaches(false);
                        http.setDoInput(true);
                        http.setDoOutput(true);
                        http.setRequestMethod("POST");

                        InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF-8");
                        BufferedReader reader = new BufferedReader(tmp);
                        String str;

                        while ((str = reader.readLine()) != null) {
                            if(playing == 3 && RecordCheck == false) {
                                if(str.equals("</table>")) {
                                    accom_num[playing - 1] = 0;
                                }
                            }
                            if(str.contains("play1")) {
                                playing = 1;
                                RecordCheck = false;
                            } else if(str.contains("play2")) {
                                if(RecordCheck == false) {
                                    accom_num[playing - 1] = 0;
                                }
                                playing = 2;
                                RecordCheck = false;
                            } else if(str.contains("play3")) {
                                if(RecordCheck == false) {
                                    accom_num[playing - 1] = 0;
                                }
                                playing = 3 ;
                                RecordCheck = false;
                            } else {
                                RecordCheck = true;
                            }

                            if(str.contains("accom_num")) {
                                accom_num[playing - 1] = Integer.parseInt(str.substring(15));
                            }

                            if (str.contains(id)) {
                                swapIndex = playing - 1;
                                swapCheck = false;
                                Thread thread1 = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            if(!btn[swapIndex].getText().equals("탑승완료")) {
                                                Message msg = swapHandler.obtainMessage();
                                                swapHandler.sendMessage(msg);
                                            }
                                        } catch (Exception e) {
                                            Log.e("MainActivity", "Exception in progress message.", e);
                                        }
                                    }
                                });
                                thread1.start();

                                if (str.length() >= 6 && str.substring(6).equals(id)) {
                                    count = 4;
                                }
                            }
                            if(count > 0) {
                                //System.out.println((playing-1) + "번 인덱스 : " + str);
                                if(str.length() > 6) {
                                    str = str.substring(6);
                                }
                                switch (count) {
                                    case 5:
                                        break;
                                    case 4:
                                        //nom[playing - 1] = str.charAt(0);
                                        break;
                                    case 3:
                                        //accom_num[playing - 1] = Integer.parseInt(str);
                                        break;
                                    case 2:
                                        reg_time[playing - 1] = Long.parseLong(str);
                                        break;
                                    case 1:
                                        rid_time[playing - 1] = Long.parseLong(str);
                                        break;
                                    default:
                                }
                                count--;

                            }
                            for(int i=0; i<3; i++) {
                                if(getPro(i) >= 100) proTest[i] = false;       //프로그레스바로 판별
                                else proTest[i] = true;
                            }
                        }
                        playing = 0;
                        publishProgress();

                        reader.close();
                        http.disconnect();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    for(int i=0; i<3; i++) {
                        proTest[i] = false;
                    }
                    break;
                }

                try {
                    Thread.sleep(1000);
                } catch (Exception e) {}
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            for(int i=0; i<pro.length; i++) {
                if(proTest[i]) {
                    int wait;
                    if(i==0) {
                        wait = (1 + (accom_num[i] / 3)) * 3;
                    } else if(i==1) {
                        wait = (1 + (accom_num[i] / 2)) * 2;
                    } else {
                        wait = (1 + (accom_num[i] / 4)) * 4;
                    }

                    rp[i].setText("현재 예약 인원 : " + accom_num[i]);
                    wt[i].setText("예상 대기 시간 : \n" + wait/60 + "시간 " + wait%60 + "분");

                    if(rid_time[i] == null) {
                        continue;
                    }
                    Long ti = System.currentTimeMillis();
                    Long time = 100 * (ti - reg_time[i]) / (rid_time[i] - reg_time[i]);
                    int value = Integer.parseInt(time.toString());
                    pro[i].setProgress(value);
                    rt[i].setVisibility(View.VISIBLE);
                    if(pro[i].getProgress() >=100) rt[i].setText("탑승!!");
                } else {
                    rt[i].setText("탑승!!");
                }

                Date date = new Date(rid_time[i] - System.currentTimeMillis());
                long l1 = date.getTime();    //date를 다시 ms로 변환
                remainTimeCheck(i, l1);
                SimpleDateFormat simpleDate = new SimpleDateFormat("HH:mm:ss");
                String t = (String) simpleDate.format(l1 - 3600 * 1000 * 9);

                if(pro[i].getProgress() >=100) rt[i].setText("탑승!!");
                if(pro[i].getProgress() >=100) {
                    swapIndex = i;
                    swapCheck = true;
                    btn[i].setText("탑승완료");
                    btn[i].setVisibility(View.VISIBLE);
                    rt[i].setVisibility(View.GONE);
                }
                else {
                    rt[i].setText("남은시간 : \n" + t.toString());
                }
            }
        }

        @Override
        protected void onCancelled() {

        }
    }

    public int getPro(int i) {
        return pro[i].getProgress();
    }

    public void remainTimeCheck(int i, long l) {

        i++;
        int notifyTime = 300;
        if(l < notifyTime * 1000 && l > (notifyTime-1) * 1000 - 200) {     //남은시간이 59~60초 사이일 때 알림

            NotificationManager nm = null;

            nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            // PendingIntent를 등록 하고, noti를 클릭시에 어떤 클래스를 호출 할 것인지 등록.
            PendingIntent intent = PendingIntent.getActivity(Reservation.this, 0,
                    new Intent(Reservation.this, Reservation.class), 0);


            int src = R.drawable.play1;
            switch(i) {
                case 1 :
                    src = R.drawable.play1;
                    break;
                case 2 :
                    src = R.drawable.play2;
                    break;
                case 3 :
                    src = R.drawable.play3;
                    break;
            }
            String ticker = "스마트 놀이동산 알림";
            String title = "곧 예약한 놀이기구에 탑승이 가능합니다";
            String text = "예약한 play" + i + "놀이기구가 " + notifyTime/60 + "분 후에 탑승 가능합니다";


            // status bar 에 등록될 메시지(Tiker, 아이콘, 그리고 noti가 실행될 시간)
            Notification notification = new Notification(src, ticker, System.currentTimeMillis());

            // noti를 클릭 했을 경우 자동으로 noti Icon 제거
            notification.flags = notification.FLAG_AUTO_CANCEL;

            // List에 표시될 항목
            Notification.Builder builder = new Notification.Builder(this)
                    .setContentIntent(intent)
                    .setSmallIcon(src)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setDefaults(Notification.DEFAULT_VIBRATE);
            notification = builder.getNotification();

            nm.notify(1234, notification);

        } else {
            //System.out.println("남은시간 : " + l);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        inTask.cancel(true);
        inTask = new InputTask();
        inTask.execute(id);
    }

    @Override
    protected void onPause() {
        super.onPause();
        inTask.cancel(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        inTask.cancel(true);
    }
}

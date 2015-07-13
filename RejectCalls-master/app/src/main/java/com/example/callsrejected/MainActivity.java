package com.example.callsrejected;

        import android.app.Activity;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.widget.TextView;

        import org.apache.http.NameValuePair;
        import org.apache.http.message.BasicNameValuePair;

        import java.math.BigInteger;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;


public class MainActivity extends Activity {

    int count=0;
    String url = "http://128.199.206.145/vigo/v1/searchdriver";
    Map<String, Integer> hash = new HashMap<String, Integer>();

    String status="";
    String incomingNumber="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // Get intent object sent from the IncomingCallReceiver
        IntentFilter filter=new IntentFilter("android.intent.action.SEND");
        this.registerReceiver(new receiver(),filter);

    }
    private class receiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("incoming number",intent.getStringExtra("number"));
            incomingNumber=intent.getStringExtra("number");

            new GetContacts().execute();
        }

    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected Void doInBackground(Void... arg0) {

            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // variable to ensure that the control enters the loop only one time because everytime the counter is increased the first part of if statement(mentioned below) holds true
            int flag = 0;



            //the phone no(key) of driver is set in the hashmap on his first call
            //Control enters this loop only when the driver makes his first call
            if(!hash.containsKey(incomingNumber))
            {
                Log.d("Creating user with the phone number",incomingNumber);
                hash.put(incomingNumber,0);//setting 0 as the default value of the key

            }

            count = hash.get(incomingNumber);
            Log.d(" counter check ", Integer.toString(count));

            if (count == 0 && flag == 0) {
                status="reached";
                Log.d("status", status);
                hash.put(incomingNumber, ++count);
                flag++;
            }
            if (count == 1 && flag == 0) {
                status="start";
                Log.d("status", status);
                hash.put(incomingNumber, ++count);
                flag++;
            }
            if (count == 2 && flag == 0) {
                status="end";
                Log.d("status", status);
                hash.put(incomingNumber,++count);
                flag++;
            }

            if(count==3)//Ride is finished
            {
                hash.remove(incomingNumber);//remove the user
            }

            flag = 0;

            String phoneStr = incomingNumber.replaceAll("\\D+","");
            Log.d("ches",phoneStr);

            BigInteger contact_number = null;
            if(phoneStr.startsWith("91"))
            {
                contact_number = new BigInteger(phoneStr.substring(2));
            }
            else
            {
                try
                {
                    Log.d("Error","error");
                }
                catch(NumberFormatException e)
                {
                   // Log.d("iggi","iggi");
                }
            }

            // Making a array list of name value pair
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("contact",String.valueOf(contact_number)));
            nameValuePairs.add(new BasicNameValuePair("action",status));

            //making sevice call
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.POST, nameValuePairs);

            //printing response value in logcat
           Log.d("Response: ", "> " + jsonStr);


            return null;
        }


        //  @Override
        protected void onPostExecute(Void result) {


            super.onPostExecute(result);
            // Dismiss the progress dialog


        }
    }
}

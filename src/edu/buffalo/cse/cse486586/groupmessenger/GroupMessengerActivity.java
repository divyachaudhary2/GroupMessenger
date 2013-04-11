package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.buffalo.cse.cse486586.groupmessenger.R;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class GroupMessengerActivity extends Activity {
	int port;
	private final Uri mUri =buildUri("content", "edu.buffalo.cse.cse486586.groupmessenger.provider");;
	public static Socket socket1=null;
	public static Socket socket2=null;
	public static Socket socket3=null;
	public static int portNo1=11108;
	public static int portNo2=11112;
	public static int portNo3=11116;
	int seqport=11116;
	String ip ="10.0.2.2";
	String portStr=null;
	public static int seqNo=0;
	public static int SeqRead=0;
	public static int SeqDisplayed=0;
	public static int key1=1;
	String name=null;
	public static int j=0;
	public static int k=0;
	private static final String AUTHORITY = "content://edu.buffalo.cse.cse486586.groupmessenger.provider";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + GroupMessengerDatabase.MessegesTable);
	private Uri buildUri(String scheme, String authority) {
		Uri.Builder uriBuilder = new Uri.Builder();
		uriBuilder.authority(authority);
		uriBuilder.scheme(scheme);
		return uriBuilder.build();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_messenger);
		TelephonyManager tel =(TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
		if(portStr.equals("5554"))
			name="avd0";
		else if(portStr.equals("5556"))
			name="avd1";
		else if(portStr.equals("5558"))
			name="avd2";

		try{
			ServerSocket serverSocket = new ServerSocket (10000);
			new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR , serverSocket);


		}catch(Exception e){
			Log.v("ERROR", "In Server establish");
		}
		final EditText editText = (EditText)findViewById(R.id.editText1);
		TextView tv = (TextView) findViewById(R.id.textView1);
		tv.setMovementMethod(new ScrollingMovementMethod());
		findViewById(R.id.button1).setOnClickListener(
				new OnPTestClickListener(tv, getContentResolver()));
		findViewById(R.id.button4).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String msg=editText.getText().toString()+"\n";
				editText.setText("");
				new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,msg);
			}
		});
		findViewById(R.id.button2).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new Testcase1().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,name);

			}
		});
		findViewById(R.id.button3).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String msg=null;
				msg=name + ":"+"TESTCASE2";
				new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,name+":"+k+++"TESTCASE2");
				//


			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
		return true;
	}

	public void insert(Message msg){
		ContentValues keyValueToInsert = new ContentValues();
		String seq=null;
		keyValueToInsert.put("key", msg.getKey());
		keyValueToInsert.put("value", msg.getValue());
		Uri newUri = getContentResolver().insert(
				mUri,   
				keyValueToInsert
				);
		SeqRead++;
	}

	private class ClientTask extends AsyncTask<String, Void, Void> {


		protected Void doInBackground(String... msgs) {
			try {
				Log.v("client", "just connected");
				Log.v("client",portStr);
				SeqMessage objSeqMsg= new SeqMessage();
				objSeqMsg.setSeq(portStr);
				objSeqMsg.setMessage(msgs[0]);
				Log.v("Message", objSeqMsg.getMessage());
				Socket socket=new Socket(ip, portNo1);
				Log.v("client",portStr);
				Log.v("client", "connected to sequencer");
				ObjectOutputStream objOutput= new ObjectOutputStream(socket.getOutputStream());
				objOutput.writeObject(objSeqMsg);
				Log.v("client","sequence message passed");
				Log.v("client", objSeqMsg.getMessage());
				//end of sequence part
				objOutput.flush();

				ObjectInputStream objInput= new ObjectInputStream(socket.getInputStream());
				SeqMessage seqMsg=(SeqMessage)objInput.readObject();
				String seqNo= seqMsg.getSeq();
				Log.v("in client",seqNo);
				Log.v("Messege",msgs[0]);
				Message objMsg= new Message();
				objMsg.setKey(seqNo);
				objMsg.setValue(msgs[0]);
				socket.close();
				new Multicast().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,objMsg);

			} catch (UnknownHostException e) {
				Log.v("ERROR", "Could not create");
				e.printStackTrace();
			} catch (IOException e) {
				Log.v("ERROR", "Could not create");
				e.printStackTrace();
			}catch(Exception e){
				e.printStackTrace();
			}

			return null;
		}


	}

	private class Multicast extends AsyncTask<Message,Void , Void>{

		@Override
		protected Void doInBackground(Message... objMsgs) {
			// TODO Auto-generated method stub
			try {
				Log.v("client","in multicast");
				socket1= new Socket(ip,portNo1);
				socket2= new Socket(ip,portNo2);
				socket3=new Socket(ip,portNo3);
				ObjectOutputStream obj1= new ObjectOutputStream(socket1.getOutputStream());
				ObjectOutputStream obj2= new ObjectOutputStream(socket2.getOutputStream());
				ObjectOutputStream obj3= new ObjectOutputStream(socket3.getOutputStream());
				obj1.writeObject(objMsgs[0]);
				obj2.writeObject(objMsgs[0]);
				obj3.writeObject(objMsgs[0]);
				obj1.flush();
				obj2.flush();
				obj3.flush();
				socket1.close();
				socket2.close();
				socket3.close();

			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

	}
	private class ServerTask extends AsyncTask<ServerSocket, String, Void> {

		@Override
		protected Void doInBackground(ServerSocket... sockets) {
			ServerSocket serverSocket = sockets[0];
			Socket socket=null;
			Log.v("server","in server");
			Log.v("avd is",portStr);
			try{
				while(true){
					Log.v("server","looking for requests");
					socket = serverSocket.accept();
					Log.v("In server","connection accepted");
					ObjectInputStream objInput= new ObjectInputStream(socket.getInputStream());

					Log.v("In server","object read");
					Log.v("avd is",portStr);
					Object O=objInput.readObject();
					if( portStr.equals("5554") &&  O instanceof SeqMessage){ //sequencer

						Log.v("Setting up connection", "Connected");
						SeqMessage objMsg = (SeqMessage)O;
						String avd= objMsg.getSeq();
						objMsg.setSeq(Integer.toString(seqNo));
						seqNo=seqNo+1;
						Log.v(avd, objMsg.getSeq());
						Log.v("server",objMsg.getMessage());
						ObjectOutputStream objOutput=new ObjectOutputStream(socket.getOutputStream());
						objOutput.writeObject(objMsg);

					}
					else{
						Log.v("server", "not in sequencer");

						Message msg= (Message)O;
						String value=msg.getValue();

						if(value.endsWith("TESTCASE2")){
							for(int i=0;i<2;i++){
								new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,name+":"+k++);
							}
							String value1=value.substring(0, value.indexOf("TESTCASE2"));
							msg.setValue(value1);
						}
						insert(msg);
						publish();
					}
					socket.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}

			return null;
		}

		protected void onProgressUpdate(String... strings ){
			super.onProgressUpdate(strings[0]);
			TextView textView = (TextView) findViewById(R.id.textView1);
			textView.append(strings[0] + "\n");
			Log.v("Server reading message", strings[0]);
			return;
		}
		public void publish(){
			Log.v("Sequence Displayed",Integer.toString(SeqDisplayed));
			Log.v("Sequence Read",Integer.toString(SeqRead));
			for(int i=SeqDisplayed;i<SeqRead;i++){
				Cursor resultCursor = getContentResolver().query(
						mUri,    
						null,                
						Integer.toString(SeqDisplayed),    
						null,                
						null                 
						);
				if (resultCursor.moveToFirst()) {

					String msg=resultCursor.getString(resultCursor.getColumnIndex(GroupMessengerDatabase.value));
					String key=resultCursor.getString(resultCursor.getColumnIndex(GroupMessengerDatabase.key));
					Log.v("publish",msg);
					publishProgress(msg);
					SeqDisplayed++;
				}
				else{
					Log.v("publish","nothing to display");
					break;
				}
			}

		}
	}

	private class Testcase1 extends AsyncTask<String, Void, Void>{
		@Override
		protected Void doInBackground(String... names) {
			Log.v("testcase",names[0]);
			// TODO Auto-generated method stub
			String msg=null;
			for(int i=0;i<5;i++){

				msg=names[0] + ":"+Integer.toString(j);
				new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,msg);
				j++;
				try{
					Thread.sleep(3000);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			return null;
		}

	}


}

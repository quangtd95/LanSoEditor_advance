package com.example.advanceDemo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;



import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.example.advanceDemo.camera.CameraLayerKTVDemoActivity;
import com.example.advanceDemo.cool.ParticleDemoActivity;
import com.example.advanceDemo.scene.LayerLayoutDemoActivity;
import com.example.advanceDemo.scene.Video2LayoutActivity;
import com.example.commonDemo.CommonDemoActivity;
import com.lansoeditor.demo.R;
import com.lansosdk.box.AudioSource;
import com.lansosdk.box.AudioPad;
import com.lansosdk.box.DrawPad;
import com.lansosdk.box.FrameInfo;
import com.lansosdk.box.LanSoEditorBox;
import com.lansosdk.box.MicRecorder;
import com.lansosdk.box.ScaleExecute;
import com.lansosdk.box.onAudioPadCompletedListener;
import com.lansosdk.box.onAudioPadProgressListener;
import com.lansosdk.box.onAudioPadThreadProgressListener;
import com.lansosdk.box.onCompressCompletedListener;
import com.lansosdk.box.onCompressProgressListener;
import com.lansosdk.box.onDrawPadCompletedListener;
import com.lansosdk.box.onDrawPadOutFrameListener;
import com.lansosdk.box.onDrawPadProgressListener;
import com.lansosdk.box.onGetFiltersOutFrameListener;
import com.lansosdk.box.onScaleProgressListener;
import com.lansosdk.videoeditor.AVDecoder;
import com.lansosdk.videoeditor.AudioPadExecute;
import com.lansosdk.videoeditor.CopyDefaultVideoAsyncTask;
import com.lansosdk.videoeditor.CopyFileFromAssets;
import com.lansosdk.videoeditor.DrawPadVideoExecute;
import com.lansosdk.videoeditor.LanSoEditor;
import com.lansosdk.videoeditor.MediaInfo;
import com.lansosdk.videoeditor.SDKDir;
import com.lansosdk.videoeditor.SDKFileUtils;
import com.lansosdk.videoeditor.VideoEditor;
import com.lansosdk.videoeditor.VideoOneDo;
import com.lansosdk.videoeditor.onVideoOneDoCompletedListener;
import com.lansosdk.videoeditor.onVideoOneDoProgressListener;
import com.lansosdk.videoplayer.VPlayer;
import com.lansosdk.videoplayer.VideoPlayer;
import com.lansosdk.videoplayer.VideoPlayer.OnPlayerCompletionListener;
import com.lansosdk.videoplayer.VideoPlayer.OnPlayerPreparedListener;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.PermissionChecker;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;


public class ListMainActivity extends Activity implements OnClickListener{


	 private static final String TAG="MainActivity";
	 private static final boolean VERBOSE = false;   
	 private TextView tvVideoPath;
	 private boolean isPermissionOk=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		Thread.setDefaultUncaughtExceptionHandler(new LanSoSdkCrashHandler());
        setContentView(R.layout.activity_main);
        /**
         * 初始化SDK
         */
    	LanSoEditor.initSDK(getApplicationContext(),null);
        
    	/**
    	 * 检查权限
    	 */
        checkPermission();
    	
        initView();
        showHintDialog();
        testFile();
    }
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	LanSoEditor.unInitSo();
    	SDKFileUtils.deleteDir(new File(SDKDir.TMP_DIR));
    }
	@Override
	public void onClick(View v) {
		if(isPermissionOk)
		{
			if(checkPath()==false)
				return;
			switch (v.getId()) {
				case R.id.id_mainlist_camerarecord:
					startDemoActivity(ListCameraRecordActivity.class);
//					startDemoActivity(ScreenRecordDemoActivity.class);
					break;
				case R.id.id_mainlist_somelayer:
					startDemoActivity(ListLayerDemoActivity.class);
					break;
				case R.id.id_mainlist_changjing:
					startDemoActivity(ListSceneDemoActivity.class);
					break;
				case R.id.id_mainlist_xuanku:
					startDemoActivity(ListCoolDemoActivity.class);
					break;
				case R.id.id_mainlist_facedetected:
					startDemoActivity(ListFaceDetectedDemoActivity.class);
					break;
				case R.id.id_mainlist_videoonedo:
					startDemoActivity(VideoOneProcessActivity.class);
					break;
				case R.id.id_mainlist_bitmaps:
					startDemoActivity(ListBitmapAudioActivity.class);
					break;
				case R.id.id_mainlist_videoplay:
					startDemoActivity(VideoPlayerActivity.class);
					break;
				default:
					break;
			}
		}else{
			showHintDialog(R.string.permission_hint);
		}
	}
	//-----------------------------
	private void initView()
	{
			tvVideoPath=(TextView)findViewById(R.id.id_main_tvvideo);
			
			findViewById(R.id.id_mainlist_camerarecord).setOnClickListener(this);
			findViewById(R.id.id_mainlist_somelayer).setOnClickListener(this);
			findViewById(R.id.id_mainlist_changjing).setOnClickListener(this);
			findViewById(R.id.id_mainlist_xuanku).setOnClickListener(this);
			findViewById(R.id.id_mainlist_facedetected).setOnClickListener(this);
			findViewById(R.id.id_mainlist_videoonedo).setOnClickListener(this);
			findViewById(R.id.id_mainlist_bitmaps).setOnClickListener(this);
			findViewById(R.id.id_mainlist_videoplay).setOnClickListener(this);
			///----------------------
	        findViewById(R.id.id_main_select_video).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					startSelectVideoActivity();
				}
			});
	        
	        findViewById(R.id.id_main_use_default_videobtn).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					new CopyDefaultVideoAsyncTask(ListMainActivity.this, tvVideoPath, "ping20s.mp4").execute();
				}
			});
	}
	private void checkPermission()
    {
    	//因为从android6.0系统有各种权限的限制,这里先检查是否有读写的权限,PermissionsManager采用github上开源库,不属于我们sdk的一部分.
		 //下载地址是:https://github.com/anthonycr/Grant,您也可以使用别的方式来检查app所需权限.
       PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
           @Override
           public void onGranted() {
           	isPermissionOk=true;
               Toast.makeText(ListMainActivity.this, R.string.message_granted, Toast.LENGTH_SHORT).show();
           }

           @Override
           public void onDenied(String permission) {
           	isPermissionOk=false;
               String message = String.format(Locale.getDefault(), getString(R.string.message_denied), permission);
               Toast.makeText(ListMainActivity.this, message, Toast.LENGTH_SHORT).show();
           }
       });
    }
	private void showHintDialog()
   	{
    	Calendar c = Calendar.getInstance();
   		int year=c.get(Calendar.YEAR);
   		int month=c.get(Calendar.MONTH)+1;
   		
   		int lyear=VideoEditor.getLimitYear();
   		int lmonth=VideoEditor.getLimitMonth();
   		
   		Log.i(TAG,"current year is:"+year+" month is:"+month +" limit year:"+lyear+" limit month:"+lmonth);
   		String timeHint=getResources().getString(R.string.sdk_limit);
   		String version=VideoEditor.getSDKVersion()+ ";\n BOX:"+LanSoEditorBox.VERSION_BOX;
   		
   		version+=",CPU性能:"+LanSoEditor.getCPULevel();
   		
   		timeHint=String.format(timeHint,version, lyear,lmonth);
   		
   		new AlertDialog.Builder(this)
   		.setTitle("提示")
   		.setMessage(timeHint)
           .setPositiveButton("确定", new DialogInterface.OnClickListener() {
   			
   			@Override
   			public void onClick(DialogInterface dialog, int which) {
   			}
   		})
        .show();
   	}
    private void showHintDialog(int stringId)
   	{
      new AlertDialog.Builder(this)
   		.setTitle("提示")
   		.setMessage(stringId)
           .setPositiveButton("确定", new DialogInterface.OnClickListener() {
   			
   			@Override
   			public void onClick(DialogInterface dialog, int which) {
   			}
   		})
           .show();
   	}
    private boolean checkPath(){
    	if(tvVideoPath.getText()!=null && tvVideoPath.getText().toString().isEmpty()){
    		Toast.makeText(ListMainActivity.this, "请输入视频地址", Toast.LENGTH_SHORT).show();
    		return false;
    	}	
    	else{
    		String path=tvVideoPath.getText().toString();
    		if((new File(path)).exists()==false){
    			Toast.makeText(ListMainActivity.this, "文件不存在", Toast.LENGTH_SHORT).show();
    			return false;
    		}else{
    			MediaInfo info=new MediaInfo(path,false);
    			boolean ret=info.prepare();
    	        Log.i(TAG,"info:"+info.toString());
    			return ret;
    		}
    	}
    }
	 private final static int SELECT_FILE_REQUEST_CODE=10;
	  	private void startSelectVideoActivity()
	    {
	    	Intent i = new Intent(this, FileExplorerActivity.class);
		    startActivityForResult(i,SELECT_FILE_REQUEST_CODE);
	    }
	    @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    	super.onActivityResult(requestCode, resultCode, data);
	    	switch (resultCode) {
			case RESULT_OK:
					if(requestCode==SELECT_FILE_REQUEST_CODE){
						Bundle b = data.getExtras();   
			    		String string = b.getString("SELECT_VIDEO");   
						Log.i("sno","SELECT_VIDEO is:"+string);
						if(tvVideoPath!=null)
							tvVideoPath.setText(string);
					}
				break;
			default:
				break;
			}
	    }
	   	private void startDemoActivity(Class<?> cls)
	   	{
	   		String path=tvVideoPath.getText().toString();
	    	Intent intent=new Intent(ListMainActivity.this,cls);
	    	intent.putExtra("videopath", path);
	    	startActivity(intent);
	   	}
	   	//------------------------------------------------------------
	   @SuppressLint("NewApi") 
		  public static boolean selfPermissionGranted(Context context,String permission) {
		        // For Android < Android M, self permissions are always granted.
		        boolean result = true;
		        int targetSdkVersion = 0;
		        try {
		            final PackageInfo info = context.getPackageManager().getPackageInfo(
		                    context.getPackageName(), 0);
		            targetSdkVersion = info.applicationInfo.targetSdkVersion;
		        } catch (PackageManager.NameNotFoundException e) {
		            e.printStackTrace();
		        }

		        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
		            if (targetSdkVersion >= Build.VERSION_CODES.M) {
		                // targetSdkVersion >= Android M, we can
		                // use Context#checkSelfPermission
		                result = context.checkSelfPermission(permission)
		                        == PackageManager.PERMISSION_GRANTED;
		            } else {
		                // targetSdkVersion < Android M, we have to use PermissionChecker
		                result = PermissionChecker.checkSelfPermission(context, permission)
		                        == PermissionChecker.PERMISSION_GRANTED;
		            }
		        }
		        return result;
		 }
	   //--------------------------------
	
	    private void testFile()
	    {
	    
	    }
	    
}























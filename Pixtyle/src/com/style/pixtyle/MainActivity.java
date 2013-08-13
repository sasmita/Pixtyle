package com.style.pixtyle;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.renderscript.RenderScript;
import android.renderscript.Allocation;
import android.widget.ImageView;
import android.widget.Button;
import android.view.View;
import android.net.Uri;
import android.content.Intent;
import android.provider.MediaStore;
import java.io.File;
import android.os.Environment;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;

import android.widget.Toast;


public class MainActivity extends Activity {

	private Bitmap mBitmapIn;
    private Bitmap mBitmapOut;

    private RenderScript mRS;
    private Allocation mInAllocation;
    private Allocation mOutAllocation;
    private ScriptC_bw mScript_bw;
    private ScriptC_invert mScript_invert;

    // Used for camera activity
	private static final String TAG = "CallCamera";
	private static final int CAPTURE_IMAGE_ACTIVITY_REQ = 0;
    
	Uri fileUri = null;
	
	// To show input and out images (depending on its state)
	ImageView imgView = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
        imgView = (ImageView) findViewById(R.id.display);
		Button callCameraButton = (Button) findViewById(R.id.button_callcamera);
		Button bwButton = (Button) findViewById(R.id.button_bw);
		Button saveButton =(Button) findViewById(R.id.button_saveimg);
		Button invertButton =(Button) findViewById(R.id.button_invert);
		
		callCameraButton.setOnClickListener( new View.OnClickListener() {
			public void onClick(View view) {
				Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				fileUri = Uri.fromFile(getOutputPhotoFile());
				i.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
				startActivityForResult(i, CAPTURE_IMAGE_ACTIVITY_REQ );
			}
		});
		
		bwButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			      applyStyle(fileUri.getPath());
			}
		});
		
		saveButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		invertButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				applyStyleInvert(fileUri.getPath());
				
			}
		});
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		  if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQ) {
		    if (resultCode == RESULT_OK) {
		      Uri photoUri = null;
		      if (data == null) {
		        // A known bug here! The image should have saved in fileUri
		        Toast.makeText(this, "Image saved successfully", 
		                       Toast.LENGTH_LONG).show();
		        photoUri = fileUri;
		      } else {
		        photoUri = data.getData();
		        Toast.makeText(this, "Image saved successfully in: " + data.getData(), 
		                       Toast.LENGTH_LONG).show();
		      }
		      
		      showImage(photoUri.getPath());
		      
		    } else if (resultCode == RESULT_CANCELED) {
		      Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
		    } else {
		      Toast.makeText(this, "Callout for image capture failed!", 
		                     Toast.LENGTH_LONG).show();
		    }
		  }
	}
	
	private File getOutputPhotoFile() {
		  File directory = new File(Environment.getExternalStoragePublicDirectory(
		                Environment.DIRECTORY_PICTURES), getPackageName());
		  
		  if (!directory.exists()) {
		    if (!directory.mkdirs()) {
		      Log.e(TAG, "Failed to create storage directory.");
		      return null;
		    }
		  }
		  
		  String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss", Locale.US).format(new Date());
		  
		  return new File(directory.getPath() + File.separator + "IMG_"  
		                    + timeStamp + ".jpg");
	}
	
	private void showImage(String photoUri) {
		File imageFile = new File (photoUri);
		
		mBitmapIn = BitmapFactory.decodeFile(imageFile.getAbsolutePath());	
		        
        imgView.setImageBitmap(mBitmapIn);
    }
	
	private void applyStyle(String photoUri) {
		File imageFile = new File (photoUri);
		
		mBitmapIn = BitmapFactory.decodeFile(imageFile.getAbsolutePath());	
	    mBitmapOut = Bitmap.createBitmap(mBitmapIn.getWidth(), mBitmapIn.getHeight(),
	                                         mBitmapIn.getConfig());
		  
	    mRS = RenderScript.create(this);
        mInAllocation = Allocation.createFromBitmap(mRS, mBitmapIn,
                                                    Allocation.MipmapControl.MIPMAP_NONE,
                                                    Allocation.USAGE_SCRIPT);
        mOutAllocation = Allocation.createFromBitmap(mRS, mBitmapOut,
                                                     Allocation.MipmapControl.MIPMAP_NONE,
                                                     Allocation.USAGE_SCRIPT);

        mScript_bw = new ScriptC_bw(mRS, getResources(), R.raw.bw);
        mScript_bw.forEach_root(mInAllocation, mOutAllocation);
        
        mOutAllocation.copyTo(mBitmapOut);
        
        imgView.setImageBitmap(mBitmapOut);
    }
	
	private void applyStyleInvert(String photoUri) {
		File imageFile = new File (photoUri);
		
		mBitmapIn = BitmapFactory.decodeFile(imageFile.getAbsolutePath());	
	    mBitmapOut = Bitmap.createBitmap(mBitmapIn.getWidth(), mBitmapIn.getHeight(),
	                                         mBitmapIn.getConfig());
		  
	    mRS = RenderScript.create(this);
        mInAllocation = Allocation.createFromBitmap(mRS, mBitmapIn,
                                                    Allocation.MipmapControl.MIPMAP_NONE,
                                                    Allocation.USAGE_SCRIPT);
        mOutAllocation = Allocation.createFromBitmap(mRS, mBitmapOut,
                                                     Allocation.MipmapControl.MIPMAP_NONE,
                                                     Allocation.USAGE_SCRIPT);

        mScript_invert = new ScriptC_invert(mRS, getResources(), R.raw.invert);
        mScript_invert.forEach_root(mInAllocation, mOutAllocation);
        
        mOutAllocation.copyTo(mBitmapOut);
        
        imgView.setImageBitmap(mBitmapOut);
    }
/*	private void galleryAddPic() {
	    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	    File f = new File(mCurrentPhotoPath);
	    Uri contentUri = Uri.fromFile(f);
	    mediaScanIntent.setData(contentUri);
	    this.sendBroadcast(mediaScanIntent);
	}*/

}

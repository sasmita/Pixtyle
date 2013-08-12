/*
 * Copyright © 2013 Sisinty Sasmita Patra
 * Pixtyle is an Open source android application where you can add different styles to your pictures.
 * This Program is a free software: you can redistribute it and/or modify under the terms of the GNU 
 * General Public License as published by the Free Software Foundation, either version 3 of the 
 * license or, at your option any later version. This Program is distributed in the hope that it 
 * will be useful, but WITHOUT ANY WARRANTY;  without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * See the GNU GENERAL PUBLIC LICENSE for more details: http://www.gnu.org/licenses/.
 * Please see the License file in this distribution for license terms.
 * Link to the License file: http://github.com/sasmita/Pixtyle/blob/master/License.txt
 *
 * Author: Sisinty Sasmita Patra
 * Email:  spatra@pdx.edu
 * Repository Link: https://github.com/sasmita/Pixtyle
 */
package com.example.pixtyle;

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
    private ScriptC_mono mScript;

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

        mScript = new ScriptC_mono(mRS, getResources(), R.raw.mono);
        mScript.forEach_root(mInAllocation, mOutAllocation);
        
        mOutAllocation.copyTo(mBitmapOut);
        
        imgView.setImageBitmap(mBitmapOut);
    }

}

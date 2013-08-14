
/*
 * Copyright © 2013 Sisinty Sasmita Patra
 
 * Pixtyle is an Open source android application where you can add different styles to your pictures.
 * This Program is a free software: you can redistribute it and/or modify under the terms of the GNU 
 * General Public License as published by the Free Software Foundation, either version 3 of the 
 * license or, at your option any later version. This Program is distributed in the hope that it 
 * will be useful, but WITHOUT ANY WARRANTY;  without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE.
 
 * See the GNU GENERAL PUBLIC LICENSE for more details: http://www.gnu.org/licenses/.
 * Please see the License file in this distribution for license terms.
 * Link to the License file: http://github.com/sasmita/Pixtyle/blob/master/License.txt
 
 * Author: Sisinty Sasmita Patra
 * Email:  spatra@pdx.edu
 * Repository Link: https://github.com/sasmita/Pixtyle
 */
 
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
import java.io.IOException;

import android.os.Environment;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;
import java.io.*;

public class MainActivity extends Activity {

	private Bitmap mBitmapIn;
    private Bitmap mBitmapOut;

    private RenderScript mRS;
    private Allocation mInAllocation;
    private Allocation mOutAllocation;
    private ScriptC_bw mScript_bw;
    private ScriptC_invert mScript_invert;

    // Used for camera activity
	int CAPTURE_ACTIVITY_REQ = 0;
    
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
		
		// Initializing mBitmapIn
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        mBitmapIn = BitmapFactory.decodeResource(getResources(), R.drawable.initial, options);
        mBitmapOut = mBitmapIn;
		
		callCameraButton.setOnClickListener( new View.OnClickListener() {
			public void onClick(View view) {
				Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				fileUri = Uri.fromFile(getFile());
				i.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
				startActivityForResult(i, CAPTURE_ACTIVITY_REQ );
			}
		});
		
		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					saveFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		bwButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			      applyStyleBW(); //fileUri.getPath());
			}
		});
		
		invertButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				applyStyleInvert(); //fileUri.getPath());
			}
		});
		
	}
	
	void saveFile() throws IOException
	{
		File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		
		String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss", Locale.US).format(new Date());
			
		File file = new File(path, "IMG_" + timeStamp + ".jpg");
		
		OutputStream fOut = new FileOutputStream(file);
		
		mBitmapOut.compress(Bitmap.CompressFormat.JPEG, 90, fOut);
		
		fOut.flush();
		fOut.close();
		
		MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(),file.getName()); 
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		  
		if (requestCode == CAPTURE_ACTIVITY_REQ) {
			  
		    if (resultCode == RESULT_OK) {   	
		      Uri photoUri = null;
		      
		      if (data == null) {
		        photoUri = fileUri;
		      } 
		      else {
		        photoUri = data.getData();
		      }
		      
		      showImage(photoUri.getPath());		      
		    } 
		  
		}
	}
	
	private File getFile() {
		  File directory = new File(Environment.getExternalStoragePublicDirectory(
		                Environment.DIRECTORY_PICTURES), getPackageName());
		  
		  return new File(directory.getPath() + File.separator + "Temp" + ".jpg");
	}
	
	private void showImage(String photoUri) {
		File imageFile = new File (photoUri);
		
		mBitmapIn = BitmapFactory.decodeFile(imageFile.getAbsolutePath());	
		        
        imgView.setImageBitmap(mBitmapIn);
    }
	
	private void applyStyleBW() {

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
	
	private void applyStyleInvert() {

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

}

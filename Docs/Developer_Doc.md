DEVELOPER DOCUMENT
==================
 
  Copyright © 2013 Sisinty Sasmita Patra
 
  Pixtyle is an Open source android application where you can add different styles to your pictures.
  This Program is a free software: you can redistribute it and/or modify under the terms of the GNU 
  General Public License as published by the Free Software Foundation, either version 3 of the 
  license or, at your option any later version. This Program is distributed in the hope that it 
  will be useful, but WITHOUT ANY WARRANTY;  without even the implied warranty of MERCHANTABILITY 
  or FITNESS FOR A PARTICULAR PURPOSE.
  
  See the GNU GENERAL PUBLIC LICENSE for more details: http://www.gnu.org/licenses/.
  Please see the License file in this distribution for license terms.
  Link to the License file: http://github.com/sasmita/Pixtyle/blob/master/License.txt
 
  Author: Sisinty Sasmita Patra
  Email:  spatra@pdx.edu
  Repository Link: https://github.com/sasmita/Pixtyle
 
### INSTALLATION GUIDE FOR DEVELOPERS

1. Download Android SDK from : http://developer.android.com/sdk/index.html

2. Create an Android virtual device 

   In Eclipse, go to window, select Android Virtual device(AVD) Manager. click New.
   'Create new Android virtual device' dialog appears. Fill up AVD name, select a 
   'Device' and 'Target' from drop-down menu. Provide SD card size. Click on create AVD.

3. Open eclipse and import the project folder in your current workspace.
   File-> Import-> Android-> Existing Android code into workspace
   Select the root directory to be the project folder and tick "Copy projects into workspace". 
   Next > Finish.

4. Build and run the application as an Android application. The emulator will be launched.

5. Optionally to run/debug on a real device, connect the Android device using USB cable and enable "USB debugging" by
   going to Android's settings -> { } Developer options -> check  "USB debugging".
 
### ADDING A NEW STYLE

 Create a RenderScript file inside src folder, e.g. invert.rs. Here is a code snippet of invert.rs

<pre>
    void root(const uchar4 *v_in, uchar4 *v_out) 
    {
        float4 f4_in = rsUnpackColor8888(*v_in);
        float3 f3_out; 
    
        f3_out.r = 1 - f4_in.r;
        f3_out.g = 1 - f4_in.g;
        f3_out.b = 1 - f4_in.b;
    
        *v_out = rsPackColorTo8888(f3_out);
    }
</pre>


### REFERENCES
 
1. http://developer.android.com/training/basics/firstapp/running-app.html


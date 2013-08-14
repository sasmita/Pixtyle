
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

#pragma rs java_package_name(com.style.pixtyle)
#pragma version(1)

void root(const uchar4 *v_in, uchar4 *v_out) {

    float4 f4_in = rsUnpackColor8888(*v_in);

    float3 f3_out; // = (f4_in.r + f4_in.g + f4_in.b)/3;
    
    f3_out.r = 1 - f4_in.r;
    f3_out.g = 1 - f4_in.g;
    f3_out.b = 1 - f4_in.b;
    
    *v_out = rsPackColorTo8888(f3_out);
}
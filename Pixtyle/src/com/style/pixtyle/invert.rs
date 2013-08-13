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
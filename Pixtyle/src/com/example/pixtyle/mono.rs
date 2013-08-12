#pragma rs java_package_name(com.example.pixtyle)
#pragma version(1)

const static float3 gMonoMult = {0.299f, 0.587f, 0.114f};

void root(const uchar4 *v_in, uchar4 *v_out) {

    float4 f4_in = rsUnpackColor8888(*v_in);

    float3 mono = dot(f4_in.rgb, gMonoMult);
    
    *v_out = rsPackColorTo8888(mono);
}
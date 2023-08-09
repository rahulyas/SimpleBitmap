precision mediump float;
uniform sampler2D sTexture;         //   Texture Content Data (Grass)

varying vec2 vTextureCoord; //Receive parameters from the vertex shader
varying vec4 vAmbient;//Receive the ambient light component from the vertex shader
varying vec4 vDiffuse;//Receives the diffuse light component from the vertex shader
varying vec4 vSpecular;//Receives the specular light component from the vertex shader

void main()                         
{
 vec4 gColor=texture2D(sTexture, vTextureCoord); 	// sample the color from the grass texture
 vec4 finalColor = gColor;

    //Give this fragment a color value
 gl_FragColor=finalColor*vAmbient + finalColor*vDiffuse ;
}
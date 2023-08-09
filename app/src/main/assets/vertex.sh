uniform mat4 uMVPMatrix;      //total transformation matrix
uniform mat4 uMMatrix; 	      //Transformation matrix (including translation, rotation, scaling)
attribute vec3 aPosition;     //Vertex position
attribute vec2 aTexCoor;      //Vertex texture coordinates
uniform vec3 uLightLocation;  //Light source position
uniform vec3 uCamera;		      //camera position

varying vec2 vTextureCoord;   //A variable used to pass to the fragment shader
varying vec4 vAmbient;        //Used for the final intensity of the ambient light passed to the fragment shader
varying vec4 vDiffuse;        //Used for the final intensity of diffuse light passed to the fragment shader
varying vec4 vSpecular;      //The final intensity of the specular light used to pass to the fragment shader

void pointLight(inout vec4 diffuse, inout vec4 specular,in vec3 lightLocation,in vec4 lightDiffuse,in vec4 lightSpecular){
  vec3 normalTarget=normalize(aPosition)+aPosition;	 //Calculate the transformed normal vector
  vec3 newNormal=(uMMatrix*vec4(normalTarget,1)).xyz-(uMMatrix*vec4(aPosition,1)).xyz;
  newNormal=normalize(newNormal);// Normalize the normal vector
  // Calculate the vector from the surface point to the camera
  vec3 eye= normalize(uCamera-(uMMatrix*vec4(aPosition,1)).xyz);
  vec3 vp= normalize(lightLocation); //normalized directional light direction vector
  vec3 halfVector=normalize(vp+eye);	//Find the half-vector of the line-of-sight and light
  float shininess=90.0;				//Roughness, the smaller the smoother
  float nDotViewPosition=max(0.0,dot(newNormal,vp));  //Find the maximum value of the dot product of the normal vector and vp and 0
  diffuse=lightDiffuse*nDotViewPosition;        //Calculate the final intensity of the scattered light
  float nDotViewHalfVector=dot(newNormal,halfVector);	//Dot product of normal and half vector
  float powerFactor=max(0.0,pow(nDotViewHalfVector,shininess)); 	//Specular Light Intensity Factor
  specular=lightSpecular*powerFactor;    			//Computes the final intensity of the specular light
  }
void main()
{
   gl_Position = uMVPMatrix * vec4(aPosition,1); //Calculate the position of this vertex for this drawing according to the total transformation matrix
   vTextureCoord = aTexCoor;//Pass the received texture coordinates to the fragment shader
   vec4 diffuseTemp,specularTemp;   //variable to receive the final intensities of the three channels
   pointLight(diffuseTemp,specularTemp,uLightLocation,vec4(0.8,0.8,0.8,1.0),vec4(0.6,0.6,0.6,1.0));
   vAmbient= vec4(0.4,0.4,0.4,1.0);    //Pass the final intensity of the ambient light to the fragment shader
   vDiffuse=diffuseTemp;    //Pass the scattered light final intensity to the fragment shader
   vSpecular=specularTemp; 		//Pass the final intensity of the specular light to the fragment shader
}
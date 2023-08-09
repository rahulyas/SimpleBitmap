uniform mat4 uMVPMatrix; //total transformation matrix
attribute vec3 aPosition;  //Vertex position
void main()
{
   gl_Position = uMVPMatrix * vec4(aPosition,1); //Calculate the position of this vertex for this drawing according to the total transformation matrix
   gl_PointSize=2.5;
}
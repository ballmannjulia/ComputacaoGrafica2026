package obj;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

import java.nio.FloatBuffer;
import java.util.Random;

import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.*;

import shaders.ShaderProgram;
import util.Utils3D;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import Model.Model;
import Model.VboCube;
import main.Main3D;

public class Personagem extends Object3D {
	//Sphere sphere = new Sphere();
	public Vector3f cor = new Vector3f();
	public Model model = null;
	
	FloatBuffer matrixBuffer = MemoryUtil.memAllocFloat(16);
	public float rotvel = 0;
	float ang = 0;
	
	public boolean UP = false;
	public boolean DOWN = false;
	public boolean LEFT = false;
	public boolean RIGHT = false;
	public boolean SHOOT = false;
	public boolean QBu = false;
	public boolean EBu = false;
	
	float velocidade = 5;
	long tirotimer = 0;
	
	public Random rnd = new Random();
	
	public Vector4f up = new Vector4f(0,1,0,1);
	public Vector4f front = new Vector4f(0,0,1,1);
	public Vector4f right = new Vector4f(1,0,0,1);
	
	
	public Personagem(float x, float y, float z, float r) {
		super(x, y, z);
		raio = r;
		
		Matrix4f mrot = new Matrix4f();
		mrot.rotate((float)Math.PI,new Vector3f(0.0f,1.0f,0.0f));
		mrot.transform(mrot, up, up);
		mrot.transform(mrot, front, front);
		mrot.transform(mrot, right, right);
		
		Utils3D.vec3dNormilize(front);
		Utils3D.vec3dNormilize(right);
		Utils3D.vec3dNormilize(up);
		
	}
	
	@Override
	public void DesenhaSe(ShaderProgram shader) {
//		glPushMatrix();
//		
//	    glDisable(GL_TEXTURE_2D);
//	    glColor3f(cor.x, cor.y, cor.z);
//	    
//		glTranslatef(x,y,z);
//		
//		sphere.draw(raio, 16, 16);
//		
//		glPopMatrix();
		
		
		//System.out.println(""+x+" "+y+" "+z);
		
		
		Matrix4f modelm1 = new Matrix4f();
		modelm1.translate(new Vector3f(x,y,z));
		Matrix4f modelm = Utils3D.positionMatrix(front, up, right); //new Matrix4f();
		Matrix4f.mul(modelm1,modelm, modelm);
		modelm.scale(new Vector3f(raio,raio,raio));
		
		//modelm.rotate((float)Math.PI,new Vector3f(0.0f,1.0f,0.0f));
		
		
		int modellocation = glGetUniformLocation(shader.programID, "model");
		
		modelm.storeTranspose(matrixBuffer);
		matrixBuffer.flip();
		glUniformMatrix4fv(modellocation, false, matrixBuffer);	
		
		model.draw();
	}
	
	@Override
	public void SimulaSe(long diftime) {
		super.SimulaSe(diftime);
		
//		if(UP) {
//			z -= velocidade*diftime/1000.0f;
//		}
//		if(DOWN) {
//			z += velocidade*diftime/1000.0f;
//		}
//		
//		if(LEFT) {
//			x -= velocidade*diftime/1000.0f;
//		}
//		if(RIGHT) {
//			x += velocidade*diftime/1000.0f;
//		}
		
		
		Matrix4f rotTmp = new Matrix4f();
		rotTmp.setIdentity();
		if(RIGHT) {
			rotTmp.rotate(-1.0f*diftime/1000.0f, new Vector3f(up.x, up.y, up.z));
		}
		if(LEFT) {
			rotTmp.rotate(1.0f*diftime/1000.0f, new Vector3f(up.x, up.y, up.z));
		}
		if(UP) {
			rotTmp.rotate(-1.0f*diftime/1000.0f, new Vector3f(right.x, right.y, right.z));
		}
		if(DOWN) {
			rotTmp.rotate(1.0f*diftime/1000.0f, new Vector3f(right.x, right.y, right.z));
		}
		if(QBu) {
			rotTmp.rotate(-1.0f*diftime/1000.0f, new Vector3f(front.x, front.y, front.z));
		}
		if(EBu) {
			rotTmp.rotate(1.0f*diftime/1000.0f, new Vector3f(front.x, front.y, front.z));
		}
		
		x += front.x*velocidade*diftime/1000.0f;
		y += front.y*velocidade*diftime/1000.0f;
		z += front.z*velocidade*diftime/1000.0f;
		
		rotTmp.transform(rotTmp,front, front);
		rotTmp.transform(rotTmp,right, right);
		rotTmp.transform(rotTmp,up, up);
		
		Utils3D.vec3dNormilize(front);
		Utils3D.vec3dNormilize(right);
		Utils3D.vec3dNormilize(up);
		
		tirotimer+=diftime;
		if(SHOOT && tirotimer > 100) {
			Projetil proj = new Projetil(x-0.5f, y-0.1f, z-0.1f, 0.1f);
			
			proj.vx = front.x*10 + (rnd.nextFloat()*0.8f-0.4f);
			proj.vy = front.y*10+ (rnd.nextFloat()*0.8f-0.4f);
			proj.vz = front.z*10+ (rnd.nextFloat()*0.8f-0.4f);
			proj.texture = Main3D.ttverde;
			Main3D.listaProjeteis.add(proj);
			proj = new Projetil(x+0.5f, y-0.1f, z-0.1f, 0.1f);
			proj.vx = front.x*10+ (rnd.nextFloat()*0.8f-0.4f);
			proj.vy = front.y*10+ (rnd.nextFloat()*0.8f-0.4f);
			proj.vz = front.z*10+ (rnd.nextFloat()*0.8f-0.4f);
			proj.texture = Main3D.ttverde;
			Main3D.listaProjeteis.add(proj);
			tirotimer = 0;
		}
		
		//y += vy*diftime/1000.0f;
		//z += vz*diftime/1000.0f;
		
		ang+=rotvel*diftime/1000.0f;
	}
}

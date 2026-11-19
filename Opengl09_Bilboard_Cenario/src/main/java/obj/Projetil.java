package obj;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL20.*;

import org.lwjgl.util.glu.*;

import shaders.ShaderProgram;
import util.Utils3D;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import Model.Model;
import Model.VboCube;
import dados.Constantes;
import main.Main3D;

public class Projetil extends Object3D {
	public Vector4f Front = new Vector4f(0.0f, 0.0f, -1.0f,1.0f);
	public Vector4f UP = new Vector4f(0.0f, 1.0f, 0.0f,1.0f);
	public Vector4f Right = new Vector4f(1.0f, 0.0f, 0.0f,1.0f);
	
	//Sphere sphere = new Sphere();
	public Vector3f cor = new Vector3f();
	public Model model = null;
	
	FloatBuffer matrixBuffer = MemoryUtil.memAllocFloat(16);
	public float rotvel = 0;
	float ang = 0;
	long timer = 0;
	public boolean vivo = true;
	public boolean morrendo = false;
	public int texture = -1;
	
	int timermorrendo = 0;
	
	public Projetil(float x, float y, float z, float r) {
		super(x, y, z);
		raio = r;
		model = Constantes.vboBilbord;//Main3D.vbocube;
	}
	
	//@Override
	public void DesenhaSe_old(ShaderProgram shader) {

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture);
        
		
		Matrix4f modelm = new Matrix4f();
		modelm.setIdentity();
		
		//System.out.println(""+x+" "+y+" "+z);
		modelm.translate(new Vector3f(x,y,z));
		modelm.scale(new Vector3f(raio,raio,raio));
		modelm.rotate(ang,new Vector3f(0.0f,1.0f,0.0f));
		
		int modellocation = glGetUniformLocation(shader.programID, "model");
		
		modelm.storeTranspose(matrixBuffer);
		matrixBuffer.flip();
		glUniformMatrix4fv(modellocation, false, matrixBuffer);	
		
		model.draw();
	}
	
	public void setRotation(Vector4f Front, Vector4f UP, Vector4f Right) {
		this.Front = Front;
		this.UP = UP;
		this.Right = Right;
	}
	
	@Override
	public void DesenhaSe(ShaderProgram shader) {
		
		Matrix4f modelm1 = new Matrix4f();
		modelm1.translate(new Vector3f(x,y,z));
		Matrix4f modelm = Utils3D.positionMatrix(Front, UP, Right); //new Matrix4f();
		Matrix4f.mul(modelm1,modelm, modelm);
		modelm.scale(new Vector3f(raio,raio,raio));
		
		int modellocation = glGetUniformLocation(shader.programID, "model");
		modelm.storeTranspose(matrixBuffer);
		matrixBuffer.flip();
		glUniformMatrix4fv(modellocation, false, matrixBuffer);	
		
		int bilbloc = glGetUniformLocation(shader.programID, "ligaluz");
		
		glUniform1i(bilbloc,0);
		
		//System.out.println("bilbloc "+bilbloc);
		//System.out.println("modellocation "+modellocation);
		
		if(morrendo) {
			glBindTexture(GL_TEXTURE_2D, Constantes.texturaExplosao);
		}else {
			glBindTexture(GL_TEXTURE_2D, Constantes.texturaTiro);
		}
		
		model.draw();
		
		glUniform1i(bilbloc,0);
	}	
	
	@Override
	public void SimulaSe(long diftime) {
		super.SimulaSe(diftime);
		timer+=diftime;
		
		
		x += vx*diftime/1000.0f;
		y += vy*diftime/1000.0f;
		z += vz*diftime/1000.0f;
		
		ang+=rotvel*diftime/1000.0f;
		
		if(timer>2000) {
			vivo = false;
		}
		
		if(morrendo==false) {
			for(int i = 0; i < Main3D.listaObjetos.size();i++) {
				Object3D obj = Main3D.listaObjetos.get(i);
				
				double dx = obj.x-x;
				double dy = obj.y-y;
				double dz = obj.z-z;
				
				double dist2 = dx*dx+dy*dy+dz*dz;
				
				double sraio = raio+obj.raio;
				
				//System.out.println(""+obj.raio+" "+raio);
				if(dist2 < sraio*sraio) {
					morrendo = true;
					//vivo = false;
					//float vtmp = vx;
					//vx = vy;
					//vy = vz;
					//vz = vtmp;
					
					obj.vx += vx/25;
					obj.vy += vy/25;
					obj.vz += vz/25;
					
					//obj.vivo = false;
					obj.levaDano(5);
				}
				
			}
		}
		if(morrendo) {
			timermorrendo+=diftime;
			raio = raio*((diftime/400.0f)+1);
			System.out.println("raio "+raio);
			if(timermorrendo>1000) {
				vivo = false;
			}
		}
	}
}

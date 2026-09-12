import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class MainCanvas extends JPanel implements Runnable{
	int W = 640;
	int H = 480;
	
	Thread runner;
	boolean ativo = true;
	int paintcounter = 0;
	
	BufferedImage imageBuffer;
	byte bufferDeVideo[];
	
	Random rand = new Random();
	
	byte memoriaPlacaVideo[];
	short paleta[][];
	
	int framecount = 0;
	int fps = 0;
	
	Font f = new Font("", Font.PLAIN, 30);
	
	int clickX = 0;
	int clickY = 0;
	int mouseX = 0;
	int mouseY = 0;
	
	int pixelSize = 0;
	int Largura = 0;
	int Altura = 0;
	
	BufferedImage imgtmp = null;
	
	float posx = 00;
	float posy = 00;
	
	boolean LEFT = false;
	boolean RIGHT = false;
	boolean UP = false;
	boolean DOWN = false;
	
	float filtroR = 1;
	float filtroG = 1;
	float filtroB = 1;
	
	float q1x = 10,q1y = 100;
	float q2x = 10,q2y = 200;
	
	// O universo 3D e uma lista de triangulos.
	// Cada triangulo possui tres Ponto3D, portanto X, Y e Z sao preservados.
	ArrayList<Triangulo3D> universo = new ArrayList<Triangulo3D>();

	
	public MainCanvas() {
		
		File f = new File("imgbmp.bmp");
		try {
			FileInputStream fin = new FileInputStream(f);

			byte todosodbytes[] = new byte[64000];
			int byteslidos = fin.read(todosodbytes);
			System.out.println("Bytes Lidos "+byteslidos);
			for(int i = 0; i < byteslidos;i++) {
				System.out.println(i+": "+todosodbytes[i]);
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
		setSize(640,480);
		setFocusable(true);
		
		Largura = 640;
		Altura = 480;
		
		pixelSize = 640*480;
		
		
//		try {
//			imgtmp = ImageIO.read(getClass().getResource("fundo.jpg"));
//			System.out.println(""+imgtmp.toString());
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
		
		imgtmp = loadImage("gato.jpg");
		
		imageBuffer = new BufferedImage(640,480, BufferedImage.TYPE_4BYTE_ABGR);
		//imageBuffer.getGraphics().drawImage(imgtmp, 0, 0, null);
		
		
		bufferDeVideo = ((DataBufferByte)imageBuffer.getRaster().getDataBuffer()).getData();
		
		System.out.println("Buffer SIZE "+bufferDeVideo.length );

		// Comeca com um triangulo no universo. Novos triangulos sao
		// adicionados com o clique esquerdo do mouse.
		adicionaTriangulo(320, 250);
		
		addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				int key = e.getKeyCode();
				if(key == KeyEvent.VK_W) {
					UP = false;
				}
				if(key == KeyEvent.VK_S) {
					DOWN = false;
				}
				if(key == KeyEvent.VK_A) {
					LEFT = false;
				}
				if(key == KeyEvent.VK_D) {
					RIGHT = false;
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();

				// TRANSLACAO 3D
				if(key == KeyEvent.VK_W) {
					UP = true;
					transformaUniverso(Matriz4x4.translacao(0, -10, 0));
				}
				if(key == KeyEvent.VK_S) {
					DOWN = true;
					transformaUniverso(Matriz4x4.translacao(0, 10, 0));
				}
				if(key == KeyEvent.VK_A) {
					LEFT = true;
					transformaUniverso(Matriz4x4.translacao(-10, 0, 0));
				}
				if(key == KeyEvent.VK_D) {
					RIGHT = true;
					transformaUniverso(Matriz4x4.translacao(10, 0, 0));
				}

				// R e F movem TODO o universo no eixo Z. A tela nao desenha Z diretamente,
				// mas o valor continua armazenado e participa das rotacoes X/Y.
				if(key == KeyEvent.VK_R) {
					transformaUniverso(Matriz4x4.translacao(0, 0, 10));
				}
				if(key == KeyEvent.VK_F) {
					transformaUniverso(Matriz4x4.translacao(0, 0, -10));
				}

				// ESCALA 3D ao redor do centro de todo o universo.
				if(key == KeyEvent.VK_Z) {
					transformaUniversoAoRedorDoCentro(Matriz4x4.escala(1.10f, 1.10f, 1.10f));
				}
				if(key == KeyEvent.VK_X) {
					transformaUniversoAoRedorDoCentro(Matriz4x4.escala(0.90f, 0.90f, 0.90f));
				}

				// ROTACAO nos tres eixos aplicada ao universo inteiro.
				if(key == KeyEvent.VK_I) {
					transformaUniversoAoRedorDoCentro(Matriz4x4.rotacaoX((float)(Math.PI / 18)));
				}
				if(key == KeyEvent.VK_K) {
					transformaUniversoAoRedorDoCentro(Matriz4x4.rotacaoX((float)(-Math.PI / 18)));
				}
				if(key == KeyEvent.VK_J) {
					transformaUniversoAoRedorDoCentro(Matriz4x4.rotacaoY((float)(Math.PI / 18)));
				}
				if(key == KeyEvent.VK_L) {
					transformaUniversoAoRedorDoCentro(Matriz4x4.rotacaoY((float)(-Math.PI / 18)));
				}
				if(key == KeyEvent.VK_Q) {
					transformaUniversoAoRedorDoCentro(Matriz4x4.rotacaoZ((float)(Math.PI / 18)));
				}
				if(key == KeyEvent.VK_E) {
					transformaUniversoAoRedorDoCentro(Matriz4x4.rotacaoZ((float)(-Math.PI / 18)));
				}
			}
		});		
		
		addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				clickX = e.getX();
				clickY = e.getY();

				// Cada clique esquerdo cria imediatamente um novo triangulo 3D
				// centrado na posicao do mouse. Nao existe mais criacao de Linha2D.
				if(e.getButton() == MouseEvent.BUTTON1) {
					adicionaTriangulo(clickX, clickY);
				}

				requestFocusInWindow();
				System.out.println("Triangulos no universo: " + universo.size());
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent arg0) {
				// TODO Auto-generated method stub
				mouseX = arg0.getX();
				mouseY = arg0.getY();
			}
			
			@Override
			public void mouseDragged(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		

		
	}
	private void drawImageToBuffer(BufferedImage image,int x,int y, float fr, float fg, float fb) {
		byte[] imgBuffer = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
		
		
		int iw = image.getWidth();
		int ih = image.getHeight();
		
		for(int yi = 0; yi < ih; yi++) {
			for(int xi = 0; xi < iw; xi++) {
				int pixi = yi*iw*4 + xi*4;
				int pixb = (yi+y)*W*4 + (xi+x)*4;
				bufferDeVideo[pixb] = imgBuffer[pixi];
			
				
				
				int b = (imgBuffer[pixi+1]&0xff);
				int g =	(imgBuffer[pixi+2]&0xff);
				int r = (imgBuffer[pixi+3]&0xff);
				
				b = (int)(b*fb);
				g = (int)(g*fg);
				r = (int)(r*fr);
//				
				b = Math.min(255, b);
				g = Math.min(255, g);
				r = Math.min(255, r);
				
				bufferDeVideo[pixb+1] = (byte)(b&0xff);
				bufferDeVideo[pixb+2] = (byte)(g&0xff);
				bufferDeVideo[pixb+3] = (byte)(r&0xff);
			}
		}
	}
	@Override
	public void paint(Graphics g) {
		
		// Limpa o buffer de video. Como a imagem usa TYPE_4BYTE_ABGR,
		// alpha = 0 deixa o pixel transparente.
		for(int i = 0; i < bufferDeVideo.length; i++) {
			bufferDeVideo[i] = 0;
		}

		// Desenha todo o universo: cada elemento da lista e um Triangulo3D.
		// A rasterizacao final usa X e Y, mas Z permanece nos pontos e
		// participa de todas as transformacoes 3D.
		for(int i = 0; i < universo.size(); i++) {
			desenhaTriangulo3D(universo.get(i), 0, 70, 220);
		}
		
		g.setFont(f);
		
		g.setColor(Color.white);
		g.fillRect(0, 0, 640, 480);
		
		// O Graphics apenas exibe o BufferedImage. As linhas ja foram
		// calculadas e gravadas pixel a pixel no bufferDeVideo.
		g.drawImage(imageBuffer,0,0,null);
		
		// Mostra o centro atual do universo, calculado pela media de todos
		// os vertices. E em torno desse ponto que escala e rotacao acontecem.
		Ponto3D centro = calculaCentroUniverso();
		g.setColor(Color.BLUE);
		if(centro != null) {
			g.fillRect((int)centro.X - 2, (int)centro.Y - 2, 5, 5);
		}

		g.setColor(Color.black);
		g.drawString("FPS "+fps+" mouse: "+mouseX+","+mouseY, 10, 25);
		g.setFont(new Font("", Font.PLAIN, 14));
		g.drawString("Triangulos no universo: " + universo.size() + " | Clique esquerdo: adicionar triangulo", 10, 48);
		g.drawString("WASD: mover universo XY | R/F: mover Z | Z/X: escala | I/K: rot X | J/L: rot Y | Q/E: rot Z", 10, 68);
	}
	
	/**
	 * Cria um triangulo 3D centrado no ponto clicado e o adiciona ao universo.
	 * Os vertices possuem Z diferentes para que rotacoes em X e Y realmente
	 * usem a profundidade, mesmo que a tela mostre somente X e Y.
	 */
	private void adicionaTriangulo(float centroX, float centroY) {
		float largura = 70.0f;
		float altura = 60.0f;
		float profundidade = 35.0f;

		Triangulo3D novo = new Triangulo3D(
			new Ponto3D(centroX - largura / 2.0f, centroY + altura / 2.0f, -profundidade),
			new Ponto3D(centroX + largura / 2.0f, centroY + altura / 2.0f,  profundidade),
			new Ponto3D(centroX, centroY - altura / 2.0f, 0.0f)
		);

		universo.add(novo);
	}

	/** Aplica uma matriz 4x4 a todos os triangulos do universo. */
	private void transformaUniverso(Matriz4x4 transformacao) {
		for(int i = 0; i < universo.size(); i++) {
			universo.get(i).transform(transformacao);
		}
	}

	/**
	 * Calcula o centro do universo usando a media de TODOS os vertices de
	 * TODOS os triangulos. Assim, os triangulos giram como um conjunto unico.
	 */
	private Ponto3D calculaCentroUniverso() {
		if(universo.isEmpty()) {
			return null;
		}

		float somaX = 0.0f;
		float somaY = 0.0f;
		float somaZ = 0.0f;
		int quantidadePontos = 0;

		for(int i = 0; i < universo.size(); i++) {
			Triangulo3D t = universo.get(i);
			Ponto3D[] pontos = { t.A, t.B, t.C };

			for(Ponto3D p : pontos) {
				somaX += p.X;
				somaY += p.Y;
				somaZ += p.Z;
				quantidadePontos++;
			}
		}

		return new Ponto3D(
			somaX / quantidadePontos,
			somaY / quantidadePontos,
			somaZ / quantidadePontos
		);
	}

	/**
	 * Rotaciona ou escala TODO o universo em torno do centro global.
	 * A matriz final e T(centro) * transformacao * T(-centro).
	 */
	private void transformaUniversoAoRedorDoCentro(Matriz4x4 transformacao) {
		Ponto3D centro = calculaCentroUniverso();
		if(centro == null) {
			return;
		}

		Matriz4x4 paraOrigem = Matriz4x4.translacao(-centro.X, -centro.Y, -centro.Z);
		Matriz4x4 voltaCentro = Matriz4x4.translacao(centro.X, centro.Y, centro.Z);

		Matriz4x4 matrizFinal = voltaCentro
			.multiplicar(transformacao)
			.multiplicar(paraOrigem);

		transformaUniverso(matrizFinal);
	}

	/**
	 * Projecao ortografica simples do triangulo 3D para a tela 2D.
	 * O Z nao e apagado nem zerado: ele permanece dentro dos Ponto3D e
	 * continua participando de translacoes, escalas e rotacoes.
	 * Apenas a rasterizacao final usa X e Y porque o buffer e bidimensional.
	 */
	public void desenhaTriangulo3D(Triangulo3D t, int r, int g, int b) {
		desenhaLinha((int)t.A.X, (int)t.A.Y, (int)t.B.X, (int)t.B.Y, r, g, b);
		desenhaLinha((int)t.B.X, (int)t.B.Y, (int)t.C.X, (int)t.C.Y, r, g, b);
		desenhaLinha((int)t.C.X, (int)t.C.Y, (int)t.A.X, (int)t.A.Y, r, g, b);
	}

	public void desenhaLinhaHorizontal(int x, int y,int w) {
		int pospix = y*(W*4)+x*4;
		
		for(int i = 0; i < w;i++) {
			
			bufferDeVideo[pospix] = (byte)255;
			bufferDeVideo[pospix+1] = (byte)0;
			bufferDeVideo[pospix+2] = (byte)0;
			bufferDeVideo[pospix+3] = (byte)0;
			pospix+=4;
		}
	}
	
	public void desenhaLinhaVertical(int x, int y,int h) {
		int pospix = y*(W*4)+x*4;
		
		for(int i = 0; i < h;i++) {
			
			bufferDeVideo[pospix] = (byte)255;
			bufferDeVideo[pospix+1] = (byte)0;
			bufferDeVideo[pospix+2] = (byte)0;
			bufferDeVideo[pospix+3] = (byte)255;
			pospix+=(W*4);
		}
	}
	
	public void desenhaPixel(int x, int y, int r, int g, int b) {
		// Evita acesso fora do vetor caso uma linha saia da tela.
		if(x < 0 || x >= W || y < 0 || y >= H) {
			return;
		}

		int pospix = y * (W * 4) + x * 4;

		// TYPE_4BYTE_ABGR: Alpha, Blue, Green, Red.
		bufferDeVideo[pospix]     = (byte)255;
		bufferDeVideo[pospix + 1] = (byte)(b & 0xff);
		bufferDeVideo[pospix + 2] = (byte)(g & 0xff);
		bufferDeVideo[pospix + 3] = (byte)(r & 0xff);
	}

	/**
	 * Desenha uma linha preta entre (x1,y1) e (x2,y2).
	 * Esta e a assinatura principal pedida no exercicio.
	 */
	public void desenhaLinha(int x1, int y1, int x2, int y2) {
		desenhaLinha(x1, y1, x2, y2, 0, 0, 0);
	}

	/**
	 * Desenha uma linha entre (x1,y1) e (x2,y2) diretamente no buffer.
	 * Implementacao do algoritmo de Bresenham para todos os octantes.
	 */
	public void desenhaLinha(int x1, int y1, int x2, int y2, int r, int g, int b) {
		int dx = Math.abs(x2 - x1);
		int dy = Math.abs(y2 - y1);

		int passoX = (x1 < x2) ? 1 : -1;
		int passoY = (y1 < y2) ? 1 : -1;

		int erro = dx - dy;

		while(true) {
			desenhaPixel(x1, y1, r, g, b);

			if(x1 == x2 && y1 == y2) {
				break;
			}

			int erro2 = 2 * erro;

			if(erro2 > -dy) {
				erro -= dy;
				x1 += passoX;
			}

			if(erro2 < dx) {
				erro += dx;
				y1 += passoY;
			}
		}
	}
	
	public void start(){
		runner = new Thread(this);
		runner.start();
	}
	
	int timer = 0;
	public void simulaMundo(long diftime){
		
		float difS = diftime/1000.0f;
		float vel = 50;
		
		timer+=diftime;
		
//		if(UP) {
//			
//		}
//		if(DOWN) {
//			posy += vel*difS;
//		}
//		if(LEFT) {
//			posx -= vel*difS;
//		}
//		if(RIGHT) {
//			posx += vel*difS;
//		}
		
	}
	
	
	@Override
	public void run() {
		long time = System.currentTimeMillis();
		long segundo = time/1000;
		long diftime = 0;
		while(ativo){
			simulaMundo(diftime);
			paintImmediately(0, 0, 640, 480);
			paintcounter+=100;
			
			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			long newtime = System.currentTimeMillis();
			long novoSegundo = newtime/1000;
			diftime = System.currentTimeMillis() - time;
			time = System.currentTimeMillis();
			framecount++;
			if(novoSegundo!=segundo) {	
				fps = framecount;
				framecount = 0;
				segundo = novoSegundo;
			}
		}
	}
	
	
	public BufferedImage loadImage(String filename) {
		try {
			imgtmp = ImageIO.read(new File(filename));
			
			BufferedImage imgout = new BufferedImage(imgtmp.getWidth(), imgtmp.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
			
			imgout.getGraphics().drawImage(imgtmp, 0, 0, null);
			
			imgtmp = null;
			
			return imgout;
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
	}
}

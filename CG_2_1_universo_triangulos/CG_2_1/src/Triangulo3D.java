public class Triangulo3D {
    Ponto3D A;
    Ponto3D B;
    Ponto3D C;

    public Triangulo3D(Ponto3D a, Ponto3D b, Ponto3D c) {
        A = a;
        B = b;
        C = c;
    }

    /**
     * Aplica a mesma matriz 4x4 aos tres pontos do triangulo.
     */
    public void transform(Matriz4x4 mat) {
        A = A.multiplicacao_mat4x4(mat);
        B = B.multiplicacao_mat4x4(mat);
        C = C.multiplicacao_mat4x4(mat);
    }

    public Triangulo3D copia() {
        return new Triangulo3D(A.copia(), B.copia(), C.copia());
    }
}

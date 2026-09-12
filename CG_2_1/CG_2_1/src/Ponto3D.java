public class Ponto3D {
    float X;
    float Y;
    float Z;
    float W;

    public Ponto3D(float x, float y, float z) {
        this(x, y, z, 1.0f);
    }

    public Ponto3D(float x, float y, float z, float w) {
        X = x;
        Y = y;
        Z = z;
        W = w;
    }

    /**
     * Multiplica este ponto 3D por uma matriz 4x4.
     * O calculo usa coordenadas homogeneas (x, y, z, w).
     */
    public Ponto3D multiplicacao_mat4x4(Matriz4x4 mat) {
        float novoX = mat.m[0][0] * X + mat.m[0][1] * Y + mat.m[0][2] * Z + mat.m[0][3] * W;
        float novoY = mat.m[1][0] * X + mat.m[1][1] * Y + mat.m[1][2] * Z + mat.m[1][3] * W;
        float novoZ = mat.m[2][0] * X + mat.m[2][1] * Y + mat.m[2][2] * Z + mat.m[2][3] * W;
        float novoW = mat.m[3][0] * X + mat.m[3][1] * Y + mat.m[3][2] * Z + mat.m[3][3] * W;

        // Se a matriz produzir um W diferente de 1, normaliza o ponto.
        // Para translacao, escala e rotacao afim, normalmente W continua 1.
        if (novoW != 0.0f && novoW != 1.0f) {
            novoX /= novoW;
            novoY /= novoW;
            novoZ /= novoW;
            novoW = 1.0f;
        }

        return new Ponto3D(novoX, novoY, novoZ, novoW);
    }

    public Ponto3D copia() {
        return new Ponto3D(X, Y, Z, W);
    }

    @Override
    public String toString() {
        return String.format("(%.2f, %.2f, %.2f)", X, Y, Z);
    }
}

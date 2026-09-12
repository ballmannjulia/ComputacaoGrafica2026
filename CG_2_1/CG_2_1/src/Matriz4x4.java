public class Matriz4x4 {
    float[][] m = new float[4][4];

    public Matriz4x4() {
        // Por padrao, cria matriz identidade.
        for (int i = 0; i < 4; i++) {
            m[i][i] = 1.0f;
        }
    }

    public static Matriz4x4 identidade() {
        return new Matriz4x4();
    }

    public static Matriz4x4 translacao(float tx, float ty, float tz) {
        Matriz4x4 mat = identidade();
        mat.m[0][3] = tx;
        mat.m[1][3] = ty;
        mat.m[2][3] = tz;
        return mat;
    }

    public static Matriz4x4 escala(float sx, float sy, float sz) {
        Matriz4x4 mat = new Matriz4x4();
        mat.m[0][0] = sx;
        mat.m[1][1] = sy;
        mat.m[2][2] = sz;
        return mat;
    }

    public static Matriz4x4 rotacaoX(float angulo) {
        Matriz4x4 mat = identidade();
        float c = (float) Math.cos(angulo);
        float s = (float) Math.sin(angulo);

        mat.m[1][1] = c;
        mat.m[1][2] = -s;
        mat.m[2][1] = s;
        mat.m[2][2] = c;
        return mat;
    }

    public static Matriz4x4 rotacaoY(float angulo) {
        Matriz4x4 mat = identidade();
        float c = (float) Math.cos(angulo);
        float s = (float) Math.sin(angulo);

        mat.m[0][0] = c;
        mat.m[0][2] = s;
        mat.m[2][0] = -s;
        mat.m[2][2] = c;
        return mat;
    }

    public static Matriz4x4 rotacaoZ(float angulo) {
        Matriz4x4 mat = identidade();
        float c = (float) Math.cos(angulo);
        float s = (float) Math.sin(angulo);

        mat.m[0][0] = c;
        mat.m[0][1] = -s;
        mat.m[1][0] = s;
        mat.m[1][1] = c;
        return mat;
    }

    /**
     * Retorna this * outra.
     * Isso permite combinar transformacoes em uma unica matriz 4x4.
     */
    public Matriz4x4 multiplicar(Matriz4x4 outra) {
        Matriz4x4 resultado = new Matriz4x4();

        // Zera a identidade criada pelo construtor antes de preencher.
        for (int linha = 0; linha < 4; linha++) {
            for (int coluna = 0; coluna < 4; coluna++) {
                resultado.m[linha][coluna] = 0.0f;
                for (int k = 0; k < 4; k++) {
                    resultado.m[linha][coluna] += this.m[linha][k] * outra.m[k][coluna];
                }
            }
        }

        return resultado;
    }
}

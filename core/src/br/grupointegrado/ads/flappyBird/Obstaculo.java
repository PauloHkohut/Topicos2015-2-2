package br.grupointegrado.ads.flappyBird;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by paulo on 26/10/2015.
 */
public class Obstaculo {

    private World mundo;
    private OrthographicCamera camera;
    private Body corpoCima,corpoBaixo;
    private float posicaoX;
    private float posicaoYcima, posicaoYbaixo;
    private float largura, altura;
    private boolean passou; // se passou pelo obstaculo

    private Obstaculo ultimoObstaculo; //ultimo antes do atual

    public  Obstaculo(World mundo, OrthographicCamera camera, Obstaculo ultimoObstaculo){
        this.mundo           = mundo;
        this.camera          = camera;
        this.ultimoObstaculo = ultimoObstaculo;

        initPosicao();
        initCorpoCima();
        initCorpoBaixo();
    }


    private void initCorpoBaixo() {
        corpoBaixo = Util.criarCorpo(mundo, BodyDef.BodyType.StaticBody, posicaoX, posicaoYbaixo);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(largura / 2, altura / 2);

        Util.criarForma(corpoBaixo, shape, "OBSTACULO_BAIXO");

        shape.dispose();
    }



    private void initCorpoCima() {
        corpoCima = Util.criarCorpo(mundo, BodyDef.BodyType.StaticBody, posicaoX, posicaoYcima);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(largura / 2, altura / 2);

        Util.criarForma(corpoCima, shape, "OBSTACULO_CIMA");

        shape.dispose();
    }


    private void initPosicao() {
        largura = 40 / Util.PIXEL_METRO;
        altura  = camera.viewportHeight / Util.PIXEL_METRO;

        float xInicial = largura;
        if (ultimoObstaculo != null){
            xInicial = ultimoObstaculo.getPosX();
        }

        posicaoX = xInicial + 8;// 8 é o espaço entre os obstaculos

        float parcela = (altura - Util.ALTURA_CHAO) / 6;// PARCELA é o tamanho da tela divido por 6, para encontrar
                                                        // a posicao Y dos obstaculos

        int multiplicador = MathUtils.random(1, 3);//Gera numero aleatorio entre 1 e 3

        posicaoYbaixo = Util.ALTURA_CHAO + (parcela * multiplicador) - (altura / 2);
        posicaoYcima  = posicaoYbaixo + altura + 2f;// espacço entre os corpos


    }

    public float getPosX(){
        return this.posicaoX;
    }


    public void remover(){
        mundo.destroyBody(corpoCima);
        mundo.destroyBody(corpoBaixo);
    }

}

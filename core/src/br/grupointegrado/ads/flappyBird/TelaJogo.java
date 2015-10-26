package br.grupointegrado.ads.flappyBird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by paulo on 28/09/2015.
 */
public class TelaJogo extends TelaBase {

    private static final float ESCALA      = 2;
    private static final float PIXEL_METRO = 32;

    private OrthographicCamera camera;// Camera do jogo
    private World mundo;// Representa o mundo do Box2d
    private Body chao; // corpo do chao
    private Passaro passaro;

    private Box2DDebugRenderer debug;// Desenha o mundo na tela para ajudar no desenvolvimento



    public TelaJogo(MainGame game) {
        super(game);
    }




    @Override
    public void show() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth() / ESCALA, Gdx.graphics.getHeight() / ESCALA);
        debug  = new Box2DDebugRenderer();
        mundo  = new World(new Vector2(0, -9.8f), false);

        initChao();
        initPassaro();

        new Obstaculo(mundo, camera, null);

    }

    private void initChao() {
        chao = Util.criarCorpo(mundo, BodyDef.BodyType.StaticBody, 0 ,0);

    }





    private void initPassaro() {
        passaro = new Passaro(mundo, camera, null);

    }





    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.25f, .25f, .25f, 1);// Limpa a tela e pinta a cor de fundo
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);// Mantem o buffer de cores

        capturaTeclas();

        atualizar(delta);
        renderizar(delta);


        debug.render(mundo, camera.combined.cpy().scl(Util.PIXEL_METRO));
    }




    private boolean pulando = false;

    private void capturaTeclas() {
        pulando = false;

        if (Gdx.input.justTouched()){
            pulando = true;
        }

    }






    /**
     * Renderizar/desenhar as imagens
     *
     * @param delta
     */
    private void renderizar(float delta) {
    }





    /**
     * Atualizacao e calculos dos corpos
     *
     * @param delta
     */
    private void atualizar(float delta) {
        passaro.atualizar(delta);
        mundo.step(1f / 60f, 6, 2);

        atualizarCamera();

        atualizarChao();
        if (pulando){
            passaro.pular();
        }

    }

    private void atualizarCamera() {
        camera.position.x = (passaro.getCorpo().getPosition().x + 34 / Util.PIXEL_METRO) * Util.PIXEL_METRO;
        camera.update();

    }






    /**
     * Atualiza a posicao do chao para acompanhar o passaro
     *
     */
    private void atualizarChao() {

        Vector2 posicao = passaro.getCorpo().getPosition();


        chao.setTransform(posicao.x, 0, 0);
    }








    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width / Util.ESCALA, height / Util.ESCALA);
        camera.update();

        redimensionaChao();

    }


    /**
     * Configura o tamanho do chao de acordo com  o tamanho da tela
     *
     */
    private void redimensionaChao() {
        chao.getFixtureList().clear(); // limpar as antigas em seguida cria as novas

        float largura = camera.viewportWidth / Util.PIXEL_METRO;
        PolygonShape shape = new PolygonShape();

        shape.setAsBox(largura / 2, Util.ALTURA_CHAO / 2);
        Fixture forma = Util.criarForma(chao, shape, "CHAO");
        shape.dispose();
    }





    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        debug.dispose();
        mundo.dispose();

    }
}

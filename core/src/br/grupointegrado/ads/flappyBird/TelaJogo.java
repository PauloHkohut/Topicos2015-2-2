package br.grupointegrado.ads.flappyBird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;

/**
 * Created by paulo on 28/09/2015.
 */
public class TelaJogo extends TelaBase {

    private static final float ESCALA      = 2;
    private static final float PIXEL_METRO = 32;
    private static final String PREF_FLAPYY_BIRD = "PREF_FLAPYY_BIRD";
    private static final String PREF_MAIOR_PONTUACAO = "PREF_MAIOR_PONTUACAO";

    private OrthographicCamera camera;// Camera do jogo
    private World mundo;// Representa o mundo do Box2d
    private Body chao; // corpo do chao
    private Passaro passaro;
    private Array<Obstaculo> obstaculos = new Array<Obstaculo>();

    private int pontuacao = 0;
    private BitmapFont fontePontuacao;
    private BitmapFont fonte;
    private Stage palcoInformacoes;
    private Label lbPontuacao;
    private Label lbMaiorPontuacao;
    private ImageButton btnPlay;
    private ImageButton btnGameOver;
    private OrthographicCamera cameraInfo;

    private Texture[] texturasPassaro;
    private Texture texturaObstaculoCima;
    private Texture texturaObstavuloBaixo;
    private Texture texturaChao;
    private Texture texturaFundo;
    private Texture texturaPlay;
    private Texture texturaGameOver;

    private SpriteBatch pincel;

    private Sprite spriteChao1;
    private Sprite spriteChao2;

    private boolean jogoIniciado = false;

    private Music musicaFundo;
    private Sound somAsas;
    private Sound somGameOver;



    private Box2DDebugRenderer debug;// Desenha o mundo na tela para ajudar no desenvolvimento



    public TelaJogo(MainGame game) {
        super(game);
    }




    @Override
    public void show() {
        camera     = new OrthographicCamera(Gdx.graphics.getWidth() / ESCALA, Gdx.graphics.getHeight() / ESCALA);
        cameraInfo = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        debug      = new Box2DDebugRenderer();
        mundo      = new World(new Vector2(0, -9.8f), false);
        mundo.setContactListener(new ContactListener() {

            @Override
            public void beginContact(Contact contact) {
                detectarColisao(contact.getFixtureA(), contact.getFixtureB());
            }

            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });
        pincel = new SpriteBatch();

        initTexturas();
        initChao();
        initPassaro();
        initFontes();
        initInformacoes();
        initAudio();

    }





    private void initAudio() {
        musicaFundo = Gdx.audio.newMusic(Gdx.files.internal("songs/music.mp3"));
        musicaFundo.setLooping(true);

        somAsas = Gdx.audio.newSound(Gdx.files.internal("songs/wing.ogg"));

        somGameOver = Gdx.audio.newSound(Gdx.files.internal("songs/game-over.mp3"));
    }






    private void initTexturas() {
        texturasPassaro = new Texture[3];
        texturasPassaro[0] = new Texture("sprites/bird-1.png");
        texturasPassaro[1] = new Texture("sprites/bird-2.png");
        texturasPassaro[2] = new Texture("sprites/bird-3.png");

        texturaObstaculoCima  = new Texture("sprites/toptube.png");
        texturaObstavuloBaixo = new Texture("sprites/bottomtube.png");

        texturaFundo = new Texture("sprites/bg.png");
        texturaChao  = new Texture("sprites/ground.png");

        texturaPlay     = new Texture("sprites/playbtn.png");
        texturaGameOver = new Texture("sprites/gameover.png");
    }






    private boolean gameOver = false;

    /**
     * Verifica se o passaro esta envolvido na colisao
     *
     * @param fixtureA
     * @param fixtureB
     */
    private void detectarColisao(Fixture fixtureA, Fixture fixtureB) {
        if ("PASSARO".equals(fixtureA.getUserData()) || "PASSARO".equals(fixtureB.getUserData())){
            //GAME OVER
            if (!gameOver){
                somGameOver.play(1);// 100 por cento do volume
            }

            gameOver = true;
            salvarPontuacao();

        }
    }





    private void salvarPontuacao() {

        Preferences pref = Gdx.app.getPreferences(PREF_FLAPYY_BIRD);
        int maiorPontuacao = pref.getInteger(PREF_MAIOR_PONTUACAO, 0);
        if (pontuacao > maiorPontuacao){
            pref.putInteger(PREF_MAIOR_PONTUACAO, pontuacao);
            pref.flush();
        }

    }








    private void initFontes() {
        FreeTypeFontGenerator.FreeTypeFontParameter fonteParam = new FreeTypeFontGenerator.FreeTypeFontParameter();

        fonteParam.size = 56;
        fonteParam.color = Color.WHITE;
        fonteParam.shadowColor = Color.BLACK;
        fonteParam.shadowOffsetX = 4;
        fonteParam.shadowOffsetY = 4;

        FreeTypeFontGenerator gerador = new FreeTypeFontGenerator(Gdx.files.internal("fonts/roboto.ttf"));

        fontePontuacao = gerador.generateFont(fonteParam);

        fonteParam.size = 24;
        fonteParam.shadowOffsetX = 2;
        fonteParam.shadowOffsetY = 2;

        fonte = gerador.generateFont(fonteParam);

        gerador.dispose();
    }





    private void initInformacoes() {
        //inicia labels
        palcoInformacoes = new Stage(new FillViewport(cameraInfo.viewportWidth, cameraInfo.viewportHeight, cameraInfo));
        Gdx.input.setInputProcessor(palcoInformacoes);

        Label.LabelStyle estilo = new Label.LabelStyle();
        estilo.font = fontePontuacao;

        lbPontuacao = new Label("0", estilo);
        palcoInformacoes.addActor(lbPontuacao);

        // inicia botoes

        ImageButton.ImageButtonStyle estiloBotao = new ImageButton.ImageButtonStyle();
        estiloBotao.up = new SpriteDrawable(new Sprite(texturaPlay));

        btnPlay = new ImageButton(estiloBotao);
        btnPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                jogoIniciado = true;
            }
        });



        estiloBotao = new ImageButton.ImageButtonStyle();
        estiloBotao.up = new SpriteDrawable(new Sprite(texturaGameOver));

        btnGameOver = new ImageButton(estiloBotao);
        btnGameOver.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                reiniciarJogo();
            }
        });


        palcoInformacoes.addActor(btnPlay);
        palcoInformacoes.addActor(btnGameOver);

        Preferences pref = Gdx.app.getPreferences(PREF_FLAPYY_BIRD);
        int maiorPontuacao = pref.getInteger(PREF_MAIOR_PONTUACAO, 0);

        estilo = new Label.LabelStyle();
        estilo.font = fonte;

        lbMaiorPontuacao = new Label("Maior: " + maiorPontuacao, estilo);
        palcoInformacoes.addActor(lbMaiorPontuacao);

    }


    /**
     * Recria a tela do jogo com todos os seus componentes
     *
     */
    private void reiniciarJogo() {

        game.setScreen(new TelaJogo(game));
    }


    private void initChao() {
        chao = Util.criarCorpo(mundo, BodyDef.BodyType.StaticBody, 0 ,0);

        float inicioCamera = 0;
        float altura       = (Util.ALTURA_CHAO * Util.PIXEL_METRO) / Util.ESCALA;

        spriteChao1 = new Sprite(texturaChao);
        spriteChao1.setBounds(inicioCamera, 0, camera.viewportWidth, altura);

        spriteChao2 = new Sprite(texturaChao);
        spriteChao2.setBounds(inicioCamera + camera.viewportWidth, 0, camera.viewportWidth, altura);

    }





    private void initPassaro() {
        passaro = new Passaro(mundo, camera, texturasPassaro);

    }





    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.25f, .25f, .25f, 1);// Limpa a tela e pinta a cor de fundo
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);// Mantem o buffer de cores

        capturaTeclas();

        atualizar(delta);
        renderizar(delta);


        //debug.render(mundo, camera.combined.cpy().scl(Util.PIXEL_METRO)); //contorno dos desenhos da tela
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

        pincel.begin();

        pincel.setProjectionMatrix(cameraInfo.combined);
        pincel.draw(texturaFundo, 0, 0, cameraInfo.viewportWidth, cameraInfo.viewportHeight);

        pincel.setProjectionMatrix(camera.combined);
        //desenha o passaro
        passaro.renderizar(pincel);

        //desenha os obstaculos
        for (Obstaculo obs : obstaculos){
            obs.renderizar(pincel);
        }

        //desenha o chao
        spriteChao1.draw(pincel);
        spriteChao2.draw(pincel);

        pincel.end();

        palcoInformacoes.draw();
    }





    /**
     * Atualizacao e calculos dos corpos
     *
     * @param delta
     */
    private void atualizar(float delta) {
        if (gameOver && musicaFundo.isPlaying()){
            musicaFundo.stop();
        }else if (!gameOver && !musicaFundo.isPlaying()){
            musicaFundo.setVolume(0.1f); // 10 % do volume
            musicaFundo.play();
        }

        palcoInformacoes.act(delta);

        passaro.getCorpo().setFixedRotation(!gameOver);

        passaro.atualizar(delta, !gameOver);


        if (jogoIniciado) {
            mundo.step(1f / 60f, 6, 2);
            atualizarObstaculos();
        }


            atualizaInformacoes();


            if (!gameOver) {
                atualizarCamera();
                atualizarChao();
            }


        if (pulando && !gameOver && jogoIniciado){
            somAsas.play(1);
            passaro.pular();
        }

    }





    private void atualizaInformacoes() {
        lbMaiorPontuacao.setPosition(10, cameraInfo.viewportHeight - lbMaiorPontuacao.getPrefHeight());
        lbMaiorPontuacao.setVisible(!jogoIniciado);

        lbPontuacao.setText(pontuacao + "");

        lbPontuacao.setPosition(cameraInfo.viewportWidth / 2 - lbPontuacao.getPrefWidth() / 2,
                cameraInfo.viewportHeight - lbPontuacao.getPrefHeight());
        lbPontuacao.setVisible(jogoIniciado);

        btnPlay.setPosition(cameraInfo.viewportWidth / 2 - btnPlay.getPrefWidth() / 2,
                cameraInfo.viewportHeight / 2 - btnPlay.getPrefHeight());
        btnPlay.setVisible(!jogoIniciado);

        btnGameOver.setPosition(cameraInfo.viewportWidth / 2 - btnGameOver.getPrefWidth() / 2,
                cameraInfo.viewportHeight / 2 - btnGameOver.getPrefHeight() / 2);
        btnGameOver.setVisible(gameOver);

    }






    private void atualizarObstaculos() {
        // enquanto a lista tiver menos do que 4, crie obstaculos
        while (obstaculos.size < 4){
            Obstaculo ultimo = null;

            if (obstaculos.size > 0){
                ultimo = obstaculos.peek();// recupera ultimo item da lista
            }

            Obstaculo o = new Obstaculo(mundo, camera, ultimo, texturaObstaculoCima, texturaObstavuloBaixo);
            obstaculos.add(o);
        }

        // verifica se os obstaculos sairam da tela para remove-las
        for (Obstaculo o : obstaculos){
            // calcula a lateral inicial da camera
            float inicioCamera = passaro.getCorpo().getPosition().x - (camera.viewportWidth / 2 / Util.PIXEL_METRO) - o.getLargura();

            //verifica se o obstaculo saiu da tela
            if (inicioCamera > o.getPosX()){
                o.remover();
                obstaculos.removeValue(o, true);

            }else if (!o.isPassou() && o.getPosX() < passaro.getCorpo().getPosition().x){
                o.setPassou(true);

                //calcular pontuacao
                pontuacao++;

                // reproduzir o som
            }
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

        float inicioCamera = (camera.position.x - camera.viewportWidth / 2) - camera.viewportWidth;

        if (spriteChao1.getX() < inicioCamera){
            spriteChao1.setBounds(spriteChao2.getX() + camera.viewportWidth, 0, spriteChao1.getWidth(), spriteChao1.getHeight());
        }

        if (spriteChao2.getX() < inicioCamera){
            spriteChao2.setBounds(spriteChao1.getX() + camera.viewportWidth, 0, spriteChao2.getWidth(), spriteChao2.getHeight());
        }
    }








    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width / Util.ESCALA, height / Util.ESCALA);
        camera.update();

        redimensionaChao();

        cameraInfo.setToOrtho(false, width, height);
        cameraInfo.update();

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
        palcoInformacoes.dispose();
        pincel.dispose();
        fontePontuacao.dispose();
        fonte.dispose();
        texturasPassaro[0].dispose();
        texturasPassaro[1].dispose();
        texturasPassaro[2].dispose();
        texturaObstaculoCima.dispose();
        texturaObstavuloBaixo.dispose();
        texturaFundo.dispose();
        texturaChao.dispose();
        texturaPlay.dispose();
        texturaGameOver.dispose();
        musicaFundo.dispose();
        somAsas.dispose();
        somGameOver.dispose();

    }
}

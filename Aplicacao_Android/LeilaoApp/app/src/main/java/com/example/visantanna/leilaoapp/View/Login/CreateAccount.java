package com.example.visantanna.leilaoapp.View.Login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.media.ExifInterface;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.visantanna.leilaoapp.R;
import com.example.visantanna.leilaoapp.base.baseActivity;
import com.example.visantanna.leilaoapp.controllers.MensagemRetorno;
import com.example.visantanna.leilaoapp.controllers.Validator;
import com.example.visantanna.leilaoapp.controllers.imageController;
import com.example.visantanna.leilaoapp.db_classes.Mensagem;
import com.example.visantanna.leilaoapp.db_classes.Usuario;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by vis_a on 17-Sep-17.
 */

public class CreateAccount extends baseActivity{
    private ImageView fotoPerfil;
    private AppCompatEditText email;
    private AppCompatEditText senha;
    private AppCompatEditText repeteSenha;
    private AppCompatEditText nomeCompleto;
    private TextView alertaEmail;
    private TextView alertaSenha;
    private TextView alertaNome;
    private Socket socket;
    private final static int RESULT_LOAD_IMAGE = 1;
    private Bitmap imagemPerfil;

    private Usuario usuario = new Usuario();

    @Override
    protected void onCreate(Bundle BundleSavedStance){
        super.onCreate(BundleSavedStance);
        setContentView(R.layout.new_account);
        mContext = getBaseContext();
        jbInit();

    }

    private void jbInit() {
        findViewById(R.id.loadingPanelNewUser).setVisibility(View.GONE);

        fotoPerfil = (ImageView)findViewById(R.id.UserPhoto);
        email = (AppCompatEditText)findViewById(R.id.emailTextBoxNewAccount);
        senha = (AppCompatEditText)findViewById(R.id.SenhaTextBoxNewAccount);
        repeteSenha = (AppCompatEditText)findViewById(R.id.RepeteSenhaTextBoxNewAccount);
        nomeCompleto =(AppCompatEditText)findViewById(R.id.NomeCompletoTextBoxNewAccount);
        alertaEmail = (TextView) findViewById(R.id.AlertaEmail);
        alertaSenha = (TextView) findViewById(R.id.AlertaSenha);
        alertaNome = (TextView) findViewById(R.id.AlertaNomeCompleto);

        email.setText(getIntent().getExtras().getString("email"));
        senha.setText(getIntent().getExtras().getString("senha"));
    }
    public void createNewAccount(View v){
        MensagemRetorno emailRetorno = Validator.validaEmailCadastro(email.getText().toString());
        if(emailRetorno.isOk()){
            usuario.setLogin(email.getText().toString());
            usuario.setEmail(email.getText().toString());
        }else{
            alertaEmail.setText(emailRetorno.getMensagem());
        }

        MensagemRetorno senhaRetorno = Validator.validaSenhaCadastro(senha.getText().toString() , repeteSenha.getText().toString() );
        if(senhaRetorno.isOk()){
            usuario.setSenha(senha.getText().toString());
        }else{
            alertaSenha.setText(senhaRetorno.getMensagem());
        }

        MensagemRetorno nomeRetorno  = Validator.validaNomeCadastro(nomeCompleto.getText().toString() );
        if(nomeRetorno.isOk()){
            usuario.setNome(nomeCompleto.getText().toString());
        }else{
            alertaNome.setText(nomeRetorno.getMensagem());
        }

        if(imagemPerfil != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imagemPerfil.compress(Bitmap.CompressFormat.PNG, 100, stream);
            usuario.setFotoPerfil(stream.toByteArray());
        }
        if(emailRetorno.isOk() && senhaRetorno.isOk() && nomeRetorno.isOk()){
            try {
                enviaRequestNewAccount();
            }catch(Exception e){
                Log.e("Erro-enviaRequest","MENSAGEM ERRO:",e);
                System.out.println(e);
            }
        }
        else
            Toast.makeText(getApplicationContext(), "Problemas de Validação", Toast.LENGTH_SHORT).show();
    }

    private void enviaRequestNewAccount() throws IOException {
        findViewById(R.id.loadingPanelNewUser).setVisibility(View.VISIBLE);
        //usado para decidir como tratar a mensagem no servidor
        usuario.setCabecalho("novo_usuario");

        new Thread(new Runnable() {
            public void run() {

                try {

                    try {
                        String ipServidor = getResources().getString(R.string.ipServidor);
                        socket = new Socket();
                        socket.connect(new InetSocketAddress(ipServidor, 9898), 10000);

                    } catch (UnknownHostException e1) {

                        e1.printStackTrace();
                        Log.e("Teste","ERRO Socket",e1);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                        Log.e("Teste","ERRO socket",e1);
                    }catch(Exception e) {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Toast.makeText(getApplicationContext(), "Falha ao Conectar Com o Servidor", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    if (socket.isConnected()) {


                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(socket.getInputStream()));


                        PrintWriter out = new PrintWriter(
                                socket.getOutputStream(), true);

                        Gson gson = new Gson();
                        String json = gson.toJson(usuario);

                        //enviando o json para o servidor
                        out.println(json);
                        out.write(json);
                        out.flush();

                        //dando um "print" do json no log
                        Log.w("JSON!!!",json);


                        String jsonIn = in.readLine();
                        final Mensagem mensagemRetorno = gson.fromJson(jsonIn, Mensagem.class);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                findViewById(R.id.loadingPanelNewUser).setVisibility(View.GONE);
                            }
                        });


                        Log.w("RETORNOACCOUNT",mensagemRetorno.getMensagem());

                        switch (mensagemRetorno.getCabecalho()){
                            case "erro":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), mensagemRetorno.getMensagem(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            case "sucesso":
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), mensagemRetorno.getMensagem(), Toast.LENGTH_SHORT).show();
                                        TransitionRightExtraField(LoginActivity.class,"email", usuario.getEmail(), "senha", usuario.getSenha());
                                    }
                                });
                                break;
                            default:
                                //dando um "print" do json no log
                                Log.w("default","algo errado no switch do cabecalho");
                                break;

                        }
                    }//fim do ifsocket is connected
                    else{
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                findViewById(R.id.loadingPanelNewUser).setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Falha ao Conectar Com o Servidor", Toast.LENGTH_LONG).show();
                            }
                        });
                    }//fim do else socket is coneccted

                } catch (Exception e) {
                    Log.e("Erro-envioJson","MENSAGEM ERRO:",e);
                }finally {

                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            findViewById(R.id.loadingPanelNewUser).setVisibility(View.GONE);
                        }
                    });
                    //Fechando o socket
                    if(socket.isConnected()) {
                        try {
                            socket.close();
                            Log.v("DesligaSocket","The client is shut down!");
                        } catch (IOException e) {
                            Log.e("FechaSocket","Problema Socket",e);
                        }
                    }//fim do if socket.isConeccted
                }//fim do finally
            }
        }).start();

    }

    private boolean validaSenha(String senha1, String senha2) {
        boolean isOk = true;
        if((Validator.allTrim(senha1).isEmpty())|| (Validator.allTrim(senha2).isEmpty())){
            isOk = false;
            alertaSenha.setText("Senha não foi preenchida!");
        }
        if(isOk){
            if(!senha1.equals(senha2)){
                isOk = false;
                alertaSenha.setText("As senhas são diferentes!");
            }
        }
        if(isOk){
            if(senha1.length() < 8 ){
                alertaSenha.setText("A senha deve ter 8 ou mais caracteres!");
            }
        }
        if(isOk){
            alertaSenha.setText("");

            usuario.setSenha(senha1);
        }
        return isOk;
    }


    private boolean validaNome(String nomeCompleto){

        if(Validator.allTrim(nomeCompleto).isEmpty()){
            alertaNome.setText("O campo Nome está vazio!");
            return false;
        }
        else{
            alertaNome.setText("");
            usuario.setNome(nomeCompleto);
        }
        return true;
    }

    private boolean validaEmail(String email) {

        if(Validator.allTrim(email).isEmpty()){
            alertaEmail.setText("O campo de E-mail está vazio!");
            return  false;
        }
        else {
            if(!Validator.validaEmail(email)){
                alertaEmail.setText("E-mail inválido!");
                return false;
            }
            else {
                alertaEmail.setText("");
                usuario.setEmail(email);
                usuario.setLogin(email);
            }

        }
        return true;
    }
    public void setPerfilImage(View v){
        Intent buscarImagem = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        buscarImagem.setType("image/*");
        startActivityForResult(buscarImagem , RESULT_LOAD_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode , Intent data){
        super.onActivityResult(requestCode , resultCode , data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri image = data.getData();
            try {
                InputStream imageStream = getContentResolver().openInputStream(image);
                Bitmap fotoBitMap = BitmapFactory.decodeStream(imageStream);
                if (fotoBitMap != null) {
                    ExifInterface exif = null;
                    try{
                        exif = new ExifInterface(image.getPath());
                        if(exif != null){
                            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION ,
                                    ExifInterface.ORIENTATION_UNDEFINED);
                            fotoBitMap = imageController.adjustOrientation(orientation,fotoBitMap);
                        }
                    }catch(Exception e){
                        Log.w("error" , e+"");
                    }
                    Bitmap squareImage = imageController.cutEdges(fotoBitMap);
                    Bitmap croppedImage = imageController.getCroppedBitmap(squareImage);
                    fotoPerfil.setImageBitmap(croppedImage);
                    imagemPerfil = fotoBitMap;
                }
            }catch(Exception e){
                Log.e("error" , e+"");
            }
        }
    }

}

/*
Se alguem quiser usar toast dentro de trhead usa isso aqui:

                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Toast.makeText(getApplicationContext(), "Socket", Toast.LENGTH_SHORT).show();
                        }
                    });

*/
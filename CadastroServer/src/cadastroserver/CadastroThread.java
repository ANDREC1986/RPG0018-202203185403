/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cadastroserver;

import controller.ProdutoJpaController;
import controller.UsuarioJpaController;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Usuario;

/**
 *
 * @author andre
 */
public class CadastroThread extends Thread {
    private ProdutoJpaController ctrl;
    private UsuarioJpaController ctrlUsu;
    private Socket s1;
    private boolean state;

    public CadastroThread(ProdutoJpaController ctrl, UsuarioJpaController ctrlUsu, Socket s1) {
        this.ctrl = ctrl;
        this.ctrlUsu = ctrlUsu;
        this.s1 = s1;
        this.state = true;
    }

@Override
public void run() {
        try {
            ObjectOutputStream saida = new ObjectOutputStream(s1.getOutputStream());
            ObjectInputStream entrada = new ObjectInputStream(s1.getInputStream());
            String login = (String) entrada.readObject();
            String senha = (String) entrada.readObject();
            Usuario usuario = ctrlUsu.findUsuario(login, senha);
            
            if (usuario == null) {
                saida.writeObject(null);
                s1.close();
                return;
            }
            
            while(this.state == true) {
                String comando = (String) entrada.readObject();
                if("l".equals(comando) || "L".equals(comando)) {
                    saida.writeObject(ctrl.findProdutoEntities());
                }
                this.state=false;
            }
            
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(CadastroThread.class.getName()).log(Level.SEVERE, null, ex);
        }
}
    
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package cadastroserver;

import controller.MovimentoJpaController;
import controller.PessoaJpaController;
import controller.ProdutoJpaController;
import controller.UsuarioJpaController;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author andre
 */
public class CadastroServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("CadastroServerPU");
    ProdutoJpaController ctrl = new ProdutoJpaController(emf);
    UsuarioJpaController ctrlUsu = new UsuarioJpaController(emf);
    PessoaJpaController ctrlPessoa = new PessoaJpaController(emf);
    MovimentoJpaController ctrlMov = new MovimentoJpaController(emf);
        try {
            ServerSocket serverSocket= new ServerSocket(4321);
            while(true) {
                Socket cliente = serverSocket.accept();
                Thread usrConnect = new Thread(new CadastroThreadV2(ctrl,ctrlUsu,ctrlMov,ctrlPessoa,cliente));
                usrConnect.start();
            }
        } catch (IOException ex) {
            Logger.getLogger(CadastroServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
}

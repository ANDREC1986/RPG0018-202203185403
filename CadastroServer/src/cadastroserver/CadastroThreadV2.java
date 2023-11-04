/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cadastroserver;

import controller.MovimentoJpaController;
import controller.PessoaJpaController;
import controller.ProdutoJpaController;
import controller.UsuarioJpaController;
import controller.exceptions.NonexistentEntityException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Movimento;
import model.Produto;
import model.Usuario;

/**
 *
 * @author andre
 */
public class CadastroThreadV2 extends Thread {
    private ProdutoJpaController ctrl;
    private UsuarioJpaController ctrlUsu;
    private MovimentoJpaController ctrlMov;
    private PessoaJpaController ctrlPessoa;
    private Socket s1;
    private boolean state;

    public CadastroThreadV2(ProdutoJpaController ctrl, UsuarioJpaController ctrlUsu, MovimentoJpaController ctrlMov, PessoaJpaController ctrlPessoa, Socket s1) {
        this.ctrl = ctrl;
        this.ctrlUsu = ctrlUsu;
        this.ctrlMov = ctrlMov;
        this.ctrlPessoa = ctrlPessoa;
        this.state = true;
        this.s1 = s1;
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
                if("l".equals(comando.toLowerCase())) {
                    saida.writeObject(ctrl.findProdutoEntities());
                } else if("e".equals(comando) || "E".equals(comando)) {
                    Movimento mov = new Movimento();
                    mov.setIdUsuario(usuario);
                    mov.setTipo('E');
                    int pessoaId = (int) entrada.readInt();
                    mov.setIdPessoa(this.ctrlPessoa.findPessoa(pessoaId));
                    int produtoId = (int) entrada.readInt();
                    mov.setIdProduto(this.ctrl.findProduto(produtoId));
                    int prodQnt = (int) entrada.readInt();
                    mov.setQuantidade(prodQnt);
                    int prodPrc = (int) entrada.readInt();
                    mov.setValorUnitario(BigDecimal.valueOf(prodPrc));
                    ctrlMov.create(mov);
                    Produto prod = ctrl.findProduto(produtoId);
                    prod.setQuantidade(prod.getQuantidade()+prodQnt);
                    ctrl.edit(prod);
                } else if("s".equals(comando) || "s".equals(comando)) {
                    Movimento mov = new Movimento();
                    mov.setIdUsuario(usuario);
                    mov.setTipo('S');
                    int pessoaId = (int) entrada.readInt();
                    mov.setIdPessoa(this.ctrlPessoa.findPessoa(pessoaId));
                    int produtoId = (int) entrada.readInt();
                    mov.setIdProduto(this.ctrl.findProduto(produtoId));
                    int prodQnt = (int) entrada.readInt();
                    mov.setQuantidade(prodQnt);
                    int prodPrc = (int) entrada.readInt();
                    mov.setValorUnitario(BigDecimal.valueOf(prodPrc));
                    ctrlMov.create(mov);
                    Produto prod = ctrl.findProduto(produtoId);
                    prod.setQuantidade(prod.getQuantidade()-prodQnt);
                    ctrl.edit(prod);
                }
                this.state=false;
            }
            
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(CadastroThreadV2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(CadastroThreadV2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(CadastroThreadV2.class.getName()).log(Level.SEVERE, null, ex);
        }
}
    
}
